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

package uk.co.objectconnexions.expressiveobjects.objectstore.xml;

import java.text.MessageFormat;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Assert;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.commons.xml.XmlFile;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ResolveState;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.TypedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.encodeable.EncodableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.ObjectNotFoundException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.ObjectPersistenceException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.PersistorUtil;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.ObjectStoreSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.CreateObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.DestroyObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.SaveObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.query.PersistenceQueryBuiltIn;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.AdapterManagerSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceQuery;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.clock.Clock;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.commands.XmlCreateObjectCommand;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.commands.XmlDestroyObjectCommand;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.commands.XmlUpdateObjectCommand;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.CollectionData;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.Data;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.DataManager;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.ListOfRootOid;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.ObjectData;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.ObjectDataVector;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.xml.Utils;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.xml.XmlDataManager;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.services.ServiceManager;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.services.xml.XmlServiceManager;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.version.FileVersion;

public class XmlObjectStore implements ObjectStoreSpi {

    private static final Logger LOG = Logger.getLogger(XmlObjectStore.class);
    private static final String XMLOS_DIR = ConfigurationConstants.ROOT + "xmlos.dir";
    private final DataManager dataManager;
    private final ServiceManager serviceManager;
    private boolean isFixturesInstalled;

    public XmlObjectStore(final ExpressiveObjectsConfiguration configuration) {
        final String charset = Utils.lookupCharset(configuration);
        final String directory = configuration.getString(XMLOS_DIR, "xml/objects");
        final XmlFile xmlFile = new XmlFile(charset, directory);
        dataManager = new XmlDataManager(xmlFile);
        serviceManager = new XmlServiceManager(xmlFile);
        serviceManager.loadServices();
    }

    public XmlObjectStore(final DataManager dataManager, final ServiceManager serviceManager) {
        this.dataManager = dataManager;
        this.serviceManager = serviceManager;
        serviceManager.loadServices();
    }

    // /////////////////////////////////////////////////////////
    // name
    // /////////////////////////////////////////////////////////

    @Override
    public String name() {
        return "XML";
    }

    // /////////////////////////////////////////////////////////
    // close
    // /////////////////////////////////////////////////////////

    @Override
    public void close() {
        LOG.info("close " + this);
    }

    // /////////////////////////////////////////////////////////
    // reset
    // /////////////////////////////////////////////////////////

    @Override
    public void reset() {
    }

    // /////////////////////////////////////////////////////////
    // init, shutdown, finalize
    // /////////////////////////////////////////////////////////

    @Override
    public boolean hasInstances(final ObjectSpecification cls) {
        LOG.debug("checking instance of " + cls);
        final ObjectData data = new ObjectData(RootOidDefault.create(cls.getSpecId(), "---dummy-value-never-used---"), null);
        return dataManager.numberOfInstances(data) > 0;
    }

    @Override
    public void open() throws ObjectPersistenceException {
        isFixturesInstalled = dataManager.isFixturesInstalled();
    }

    @Override
    public boolean isFixturesInstalled() {
        return isFixturesInstalled;
    }

    private void initObject(final ObjectAdapter adapter, final ObjectData data) {
        if (!adapter.canTransitionToResolving()) {
            return;
        } 
        try {
            PersistorUtil.startResolving(adapter);
            final List<ObjectAssociation> fields = adapter.getSpecification().getAssociations();
            for (int i = 0; i < fields.size(); i++) {
                final ObjectAssociation field = fields.get(i);
                if (field.isNotPersisted()) {
                    continue;
                }

                final ObjectSpecification fieldSpecification = field.getSpecification();
                if (fieldSpecification.isEncodeable()) {
                    final EncodableFacet encoder = fieldSpecification.getFacet(EncodableFacet.class);
                    ObjectAdapter value;
                    final String valueData = data.value(field.getId());
                    if (valueData != null) {
                        if (valueData.equals("NULL")) {
                            value = null;
                        } else {
                            value = encoder.fromEncodedString(valueData);
                        }
                        ((OneToOneAssociation) field).initAssociation(adapter, value);
                    }
                } else if (field.isOneToManyAssociation()) {
                    initObjectSetupCollection(adapter, data, field);
                } else if (field.isOneToOneAssociation()) {
                    initObjectSetupReference(adapter, data, field);
                }
            }
            adapter.setVersion(data.getVersion());
        } finally {
            PersistorUtil.toEndState(adapter);
        }
    }

    private void initObjectSetupReference(final ObjectAdapter object, final ObjectData data, final ObjectAssociation field) {
    	
        final RootOid referencedOid = (RootOidDefault) data.get(field.getId());
        if(LOG.isDebugEnabled()) {
            LOG.debug("setting up field " + field + " with " + referencedOid);
        }
        if (referencedOid == null) {
            return;
        }

        final Data fieldData = dataManager.loadData(referencedOid);

        final ObjectAdapter referencedAdapter = getAdapterManager().adapterFor(referencedOid);
        if (fieldData == null) {
            if (!referencedAdapter.isDestroyed()) {
                referencedAdapter.changeState(ResolveState.DESTROYED);
            }
            LOG.warn("No data found for " + referencedOid + " so field '" + field.getName() + "' not set in object '" + object.titleString() + "'");
        }
        ((OneToOneAssociation) field).initAssociation(object, referencedAdapter);

        // REVIEW: what was this commented out code for?
        /*
         * if (loadedObjects().isLoaded(referenceOid)) { ObjectAdapter
         * loadedObject = loadedObjects().getLoadedObject(referenceOid);
         * LOG.debug("using loaded object " + loadedObject);
         * object.initAssociation((OneToOneAssociation) field, loadedObject); }
         * else { ObjectAdapter fieldObject; Data fieldData = (Data)
         * dataManager.loadData((SerialOid) referenceOid);
         * 
         * if (fieldData != null) { fieldObject = (ObjectAdapter)
         * specFor(fieldData).acquireInstance(); } else { fieldObject =
         * (ObjectAdapter) field.getSpecification().acquireInstance(); }
         * 
         * fieldObject.setOid(referenceOid);
         * 
         * if (fieldObject instanceof CollectionAdapter) {
         * fieldObject.setResolved(); }
         * 
         * loadedObjects().loaded(fieldObject);
         * object.initAssociation((OneToOneAssociation) field, fieldObject); }
         */
    }

    private void initObjectSetupCollection(final ObjectAdapter objectAdapter, final ObjectData data, final ObjectAssociation field) {
        /*
         * The internal collection is already a part of the object, and
         * therefore cannot be recreated, but its oid must be set
         */
        final ListOfRootOid refs = (ListOfRootOid) data.get(field.getId());
        
        final ObjectAdapter collectionAdapter = field.get(objectAdapter);
        if (!collectionAdapter.canTransitionToResolving()) {
            return;
        } 
        
        try {
            PersistorUtil.startResolving(collectionAdapter);
            final int size = refs == null ? 0 : refs.size();
            final ObjectAdapter[] elements = new ObjectAdapter[size];
            for (int j = 0; j < size; j++) {
                final RootOid elementOid = refs.elementAt(j);
                ObjectAdapter adapter;
                adapter = getAdapterManager().getAdapterFor(elementOid);
                if (adapter == null) {
                    adapter = loadInstanceAndAdapt(elementOid);
                }
                elements[j] = adapter;
            }
            final CollectionFacet facet = CollectionFacetUtils.getCollectionFacetFromSpec(collectionAdapter);
            facet.init(collectionAdapter, elements);
        } finally {
            PersistorUtil.toEndState(collectionAdapter);
        }
    }

    // /////////////////////////////////////////////////////////
    // Transaction Management
    // /////////////////////////////////////////////////////////

    @Override
    public void startTransaction() {
        LOG.debug("start transaction");
    }

    @Override
    public void endTransaction() {
        LOG.debug("end transaction");
    }

    @Override
    public void abortTransaction() {
        LOG.debug("transaction aborted");
    }

    // /////////////////////////////////////////////////////////
    // createXxxCommands
    // /////////////////////////////////////////////////////////

    @Override
    public CreateObjectCommand createCreateObjectCommand(final ObjectAdapter object) {
        return new XmlCreateObjectCommand(object, dataManager);
    }

    @Override
    public SaveObjectCommand createSaveObjectCommand(final ObjectAdapter object) {
        return new XmlUpdateObjectCommand(object, dataManager);
    }

    @Override
    public DestroyObjectCommand createDestroyObjectCommand(final ObjectAdapter object) {
        return new XmlDestroyObjectCommand(object, dataManager);
    }

    // /////////////////////////////////////////////////////////
    // execute, flush
    // /////////////////////////////////////////////////////////

    @Override
    public void execute(final List<PersistenceCommand> commands) {
        LOG.debug("start execution of transaction");
        for (final PersistenceCommand command : commands) {
            command.execute(null);
        }
        LOG.debug("end execution");
    }

    // /////////////////////////////////////////////////////////
    // getObject, resolveImmediately, resolveField
    // /////////////////////////////////////////////////////////


    @Override
    public ObjectAdapter loadInstanceAndAdapt(final TypedOid oid) {
        LOG.debug("getObject " + oid);
        final Data data = dataManager.loadData((RootOidDefault) oid);
        LOG.debug("  data read " + data);

        ObjectAdapter object;

        if (data instanceof ObjectData) {
            object = recreateAdapter((ObjectData) data);
        } else if (data instanceof CollectionData) {
            throw new ExpressiveObjectsException();
        } else {
            throw new ObjectNotFoundException(oid);
        }
        return object;
    }

    @Override
    public void resolveField(final ObjectAdapter object, final ObjectAssociation field) {
        final ObjectAdapter reference = field.get(object);
        resolveImmediately(reference);
    }

    @Override
    public void resolveImmediately(final ObjectAdapter object) {
        final ObjectData data = (ObjectData) dataManager.loadData((RootOidDefault) object.getOid());
        Assert.assertNotNull("Not able to read in data during resolve", object, data);
        initObject(object, data);
    }

    /*
     * The ObjectData holds all references for internal collections, so the
     * object should haves its internal collection populated by this method.
     */
    private ObjectAdapter recreateAdapter(final ObjectData data) {
        final RootOid oid = data.getRootOid();
        final ObjectAdapter adapter = getAdapterManager().adapterFor(oid);
        initObject(adapter, data);
        return adapter;
    }

    // /////////////////////////////////////////////////////////
    // getInstances, allInstances
    // /////////////////////////////////////////////////////////

    @Override
    public List<ObjectAdapter> loadInstancesAndAdapt(final PersistenceQuery persistenceQuery) {

        if (!(persistenceQuery instanceof PersistenceQueryBuiltIn)) {
            throw new IllegalArgumentException(MessageFormat.format("Provided PersistenceQuery not supported; was {0}; " + "the XML object store only supports {1}", persistenceQuery.getClass().getName(), PersistenceQueryBuiltIn.class.getName()));
        }
        final PersistenceQueryBuiltIn builtIn = (PersistenceQueryBuiltIn) persistenceQuery;

        final ObjectSpecification objSpec = builtIn.getSpecification();
        LOG.debug("getInstances of " + objSpec + " where " + builtIn);
        final RootOid oid = RootOidDefault.create(objSpec.getSpecId(), "dummy");
        final ObjectData patternData = new ObjectData(oid, null);
        return getInstances(patternData, builtIn);
    }

    private List<ObjectAdapter> getInstances(final ObjectData patternData, final PersistenceQueryBuiltIn persistenceQuery) {
        final ObjectDataVector data = dataManager.getInstances(patternData);
        final List<ObjectAdapter> instances = Lists.newArrayList();
        
        for (int i = 0; i < data.size(); i++) {
            final ObjectData instanceData = data.element(i);
            if(LOG.isDebugEnabled()) {
                LOG.debug("instance data " + instanceData);
            }

            final RootOid oid = instanceData.getRootOid();

            final ObjectAdapter adapter = getAdapterManager().adapterFor(oid);
            if(LOG.isDebugEnabled()) {
                LOG.debug("recreated instance " + adapter);
            }
            initObject(adapter, instanceData);

            if (persistenceQuery == null || persistenceQuery.matches(adapter)) {
                instances.add(adapter);
            }
        }
        return instances;
    }

    // /////////////////////////////////////////////////////////
    // services
    // /////////////////////////////////////////////////////////

    @Override
    public RootOid getOidForService(ObjectSpecification serviceSpec) {
        return serviceManager.getOidForService(serviceSpec.getSpecId());
    }

    @Override
    public void registerService(final RootOid rootOid) {
        serviceManager.registerService(rootOid);
    }

    // /////////////////////////////////////////////////////////
    // debugging
    // /////////////////////////////////////////////////////////

    @Override
    public void debugData(final DebugBuilder debug) {
        debug.appendTitle("Business Objects");
        debug.appendln(dataManager.getDebugData());
    }

    @Override
    public String debugTitle() {
        return "XML Object Store";
    }

    // /////////////////////////////////////////////////////////
    // Dependencies (injected)
    // /////////////////////////////////////////////////////////

    /**
     * Set the clock used to generate sequence numbers and last changed dates
     * for version objects.
     */
    public void setClock(final Clock clock) {
        FileVersion.setClock(clock);
    }

    // /////////////////////////////////////////////////////////
    // Dependencies (from singleton)
    // /////////////////////////////////////////////////////////

    protected SpecificationLoaderSpi getSpecificationLookup() {
        return ExpressiveObjectsContext.getSpecificationLoader();
    }

    private AdapterManager getAdapterManager() {
        return getPersistenceSession().getAdapterManager();
    }

    protected PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

}
