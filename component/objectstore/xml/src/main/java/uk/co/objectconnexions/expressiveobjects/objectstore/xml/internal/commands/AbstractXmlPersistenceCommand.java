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

package uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.commands;

import java.util.List;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.encodeable.EncodableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommandAbstract;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.DataManager;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.ObjectData;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.version.FileVersion;

abstract class AbstractXmlPersistenceCommand extends PersistenceCommandAbstract {
    private static final Logger LOG = Logger.getLogger(AbstractXmlPersistenceCommand.class);

    private final DataManager dataManager;

    public AbstractXmlPersistenceCommand(final ObjectAdapter adapter, final DataManager dataManager) {
        super(adapter);
        this.dataManager = dataManager;
    }

    protected DataManager getDataManager() {
        return dataManager;
    }

    protected ObjectData createObjectData(final ObjectAdapter adapter, final boolean ensurePersistent) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("compiling object data for " + adapter);
        }

        final ObjectSpecification adapterSpec = adapter.getSpecification();
        final ObjectData data = new ObjectData((RootOidDefault) adapter.getOid(), adapter.getVersion());

        final List<ObjectAssociation> associations = adapterSpec.getAssociations();
        for (final ObjectAssociation association : associations) {
            if (association.isNotPersisted()) {
                continue;
            }

            final ObjectAdapter associatedObject = association.get(adapter);
            final boolean isEmpty = association.isEmpty(adapter);
            final String associationId = association.getId();

            if (association.isOneToManyAssociation()) {
                saveCollection(associationId, data, associatedObject, ensurePersistent);
            } else if (association.getSpecification().isEncodeable()) {
                saveEncoded(data, associationId, associatedObject, isEmpty);
            } else if (association.isOneToOneAssociation()) {
                saveReference(data, associationId, associatedObject, ensurePersistent);
            }
        }

        return data;
    }

    private void saveReference(final ObjectData data, final String associationId, final ObjectAdapter associatedObject, final boolean ensurePersistent) {
        data.addAssociation(associatedObject, associationId, ensurePersistent);
    }

    private void saveCollection(final String associationId, final ObjectData data, final ObjectAdapter associatedObject, final boolean ensurePersistent) {
        data.addInternalCollection(associatedObject, associationId, ensurePersistent);
    }

    private void saveEncoded(final ObjectData data, final String associationId, final ObjectAdapter associatedObject, final boolean isEmpty) {
        if (associatedObject == null || isEmpty) {
            data.saveValue(associationId, isEmpty, null);
        } else {
            final EncodableFacet facet = associatedObject.getSpecification().getFacet(EncodableFacet.class);
            final String encodedValue = facet.toEncodedString(associatedObject);
            data.saveValue(associationId, isEmpty, encodedValue);
        }
    }

}