/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package uk.co.objectconnexions.expressiveobjects.core.runtime.fixturedomainservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.applib.AbstractService;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.DescribedAs;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Exploration;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.MemberOrder;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Programmatic;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.Persistability;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.AdapterManagerSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;

public class ObjectFixtureService {

    private static final Logger LOG = Logger.getLogger(ObjectFixtureService.class);
    private static final String DATA_FILEPATH = ConfigurationConstants.ROOT + "exploration-objects.file";
    private static final String DEFAULT_FILEPATH = "fixture-data";

    private final ObjectFixtureFilePersistor persistor = new ObjectFixtureFilePersistor();
    private Set<Object> objects = Sets.newHashSet();

    // {{ title, Id
    public String title() {
        return "Fixture Objects";
    }

    public String getId() {
        return "fixtures";
    }

    public String iconName() {
        return "Fixture";
    }

    // }}

    // {{ action: save
    @DescribedAs("Add this object to the set of saved objects")
    @MemberOrder(sequence = "1")
    @Exploration
    public void save(final Object object) {
        final ObjectAdapter adapter = getAdapterManager().adapterFor(object);
        if (adapter.getSpecification().persistability() != Persistability.TRANSIENT) {
            LOG.info("Saving object for fixture: " + adapter);
            addObjectAndAssociates(adapter);
            saveAll();
        }
    }

    private void addObjectAndAssociates(final ObjectAdapter adapter) {
        if (objects.contains(adapter.getObject())) {
            return;
        }
        objects.add(adapter.getObject());

        final ObjectSpecification adapterSpec = adapter.getSpecification();
        final List<ObjectAssociation> associations = adapterSpec.getAssociations();
        for (final ObjectAssociation association : associations) {
            if (association.isNotPersisted()) {
                continue;
            }

            final ObjectAdapter associatedObject = association.get(adapter);
            final boolean isEmpty = association.isEmpty(adapter);
            if (isEmpty) {
                continue;
            }
            if (association.isOneToManyAssociation()) {
                final CollectionFacet facet = associatedObject.getSpecification().getFacet(CollectionFacet.class);
                for (final ObjectAdapter element : facet.iterable(associatedObject)) {
                    addObjectAndAssociates(element);
                }
            } else if (association.isOneToOneAssociation() && !association.getSpecification().isParseable()) {
                addObjectAndAssociates(associatedObject);
            }
        }
    }

    public String validateSave(final Object object) {
        if (object == this || object instanceof AbstractService) {
            return "Can't add/remove a service";
        }
        return objects.contains(object) ? "This object has already been saved" : null;
    }

    // }}

    // {{ action: remove
    @DescribedAs("Remove this object from the set of saved objects")
    @MemberOrder(sequence = "2")
    @Exploration
    public void remove(final Object object) {
        objects.remove(object);
        saveAll();
    }

    public String validateRemove(final Object object) {
        if (object == this || object instanceof AbstractService) {
            return "Can't add/remove a service";
        }
        return objects.contains(object) ? null : "Can't remove an object that has not been saved";
    }

    // }}

    // {{ action: all Saved Objects
    @DescribedAs("Retrieved all the saved objects")
    @MemberOrder(sequence = "4")
    @Exploration
    public Set<Object> allSavedObjects() {
        return objects;
    }

    // }}

    // {{ action: saveAll
    @DescribedAs("Save the current state of the saved objects")
    @MemberOrder(sequence = "3")
    @Exploration
    public void saveAll() {
        FileWriter out = null;
        try {
            final File file = file(true);
            out = new FileWriter(file);
            persistor.save(objects, out);
        } catch (final IOException e) {
            throw new ExpressiveObjectsException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                    throw new ExpressiveObjectsException(e);
                }
            }
        }
    }

    // }}

    // //////////////////////////////////////////////////////////////////
    // programmatic
    // //////////////////////////////////////////////////////////////////

    // {{ loadFile (ignored)
    @Programmatic
    public void loadFile() {
        FileReader reader = null;
        try {
            final File file = file(false);
            reader = new FileReader(file);
            objects = persistor.loadData(reader);
        } catch (final FileNotFoundException e) {
            return;
        } catch (final IOException e) {
            throw new ExpressiveObjectsException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    throw new ExpressiveObjectsException(e);
                }
            }
        }
    }

    private File file(final boolean createFileIfDoesntExist) throws IOException {
        final String fixturePath = getConfiguration().getString(DATA_FILEPATH, DEFAULT_FILEPATH);
        final File file = new File(fixturePath);
        final File directory = file.getParentFile();
        mkdirIfRequired(directory);
        if (!file.exists() && createFileIfDoesntExist) {
            createFile(file);
        }
        return file;
    }

    private void mkdirIfRequired(final File directory) {
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }
    }

    private void createFile(final File file) throws IOException {
        file.createNewFile();
    }
    // }}
    
    // //////////////////////////////////////////////////////////////////
    // from context
    // //////////////////////////////////////////////////////////////////

    protected AdapterManager getAdapterManager() {
        return getPersistenceSession().getAdapterManager();
    }

    protected PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    protected ExpressiveObjectsConfiguration getConfiguration() {
        return ExpressiveObjectsContext.getConfiguration();
    }

}
