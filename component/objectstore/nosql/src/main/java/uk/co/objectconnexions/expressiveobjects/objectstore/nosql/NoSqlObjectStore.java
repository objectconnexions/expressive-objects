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

package uk.co.objectconnexions.expressiveobjects.objectstore.nosql;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.AggregatedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.TypedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.ObjectStoreSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.CreateObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.DestroyObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.SaveObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.query.PersistenceQueryBuiltIn;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.query.PersistenceQueryFindAllInstances;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.query.PersistenceQueryFindByPattern;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.query.PersistenceQueryFindByTitle;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.IdentifierGenerator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.OidGenerator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceQuery;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.NoSqlDataDatabase;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.StateReader;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.encryption.DataEncryption;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.keys.KeyCreatorDefault;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.versions.VersionCreator;

public class NoSqlObjectStore implements ObjectStoreSpi {
    
    private final KeyCreatorDefault keyCreator = new KeyCreatorDefault();
    private final Map<ObjectSpecId, RootOid> servicesByObjectSpecId = Maps.newHashMap();
    
    private final NoSqlDataDatabase database;
    private final VersionCreator versionCreator;
    private final ObjectReader objectReader = new ObjectReader();
    private final OidGenerator oidGenerator;
    private final DataEncryption wrtingDataEncrypter;
    private final Map<String, DataEncryption> availableDataEncrypters;
    private final boolean isDataLoaded;

    public NoSqlObjectStore(final NoSqlDataDatabase db, final OidGenerator oidGenerator, final VersionCreator versionCreator, final DataEncryption writingDataEncrypter, final Map<String, DataEncryption> availableDataEncrypters) {
        this.database = db;
        this.oidGenerator = oidGenerator;
        this.versionCreator = versionCreator;
        this.wrtingDataEncrypter = writingDataEncrypter;
        this.availableDataEncrypters = availableDataEncrypters;

        db.open();
        isDataLoaded = db.containsData();
        db.close();
    }

    public IdentifierGenerator getIdentifierGenerator() {
        return oidGenerator.getIdentifierGenerator();
    }

    @Override
    public CreateObjectCommand createCreateObjectCommand(final ObjectAdapter object) {
        // TODO should this be done at a higher level so it is applicable for
        // all OSes
        if (object.getSpecification().isParented()) {
            // throw new
            // UnexpectedCallException("Aggregated objects should not be created outside of their owner");
            return null;
        } else {
            return new NoSqlCreateObjectCommand(versionCreator, wrtingDataEncrypter, object);
        }
    }

    @Override
    public DestroyObjectCommand createDestroyObjectCommand(final ObjectAdapter adapter) {
        if (adapter.getSpecification().isParented()) {
            throw new NoSqlStoreException("Can't delete an aggregated object");
        } else {
            return new NoSqlDestroyObjectCommand(versionCreator, adapter);
        }
    }

    @Override
    public SaveObjectCommand createSaveObjectCommand(final ObjectAdapter adapter) {
        
        // TODO should this be done at a higher level 
        // so it is applicable for all object stores?
        
        final ObjectAdapter rootAdapter = adapter.getAggregateRoot();
        if (!(rootAdapter.getOid() instanceof RootOid)) {
            throw new NoSqlStoreException("Unexpected aggregated object to save: " + rootAdapter + " (" + adapter + ")");
        }
        return new NoSqlSaveObjectCommand(versionCreator, wrtingDataEncrypter, rootAdapter);
    }

    @Override
    public void execute(final List<PersistenceCommand> commands) {
        database.write(commands);
    }
    
    @Override
    public List<ObjectAdapter> loadInstancesAndAdapt(final PersistenceQuery persistenceQuery) {
        if (persistenceQuery instanceof PersistenceQueryFindByTitle) {
            return getAllInstances((PersistenceQueryFindByTitle) persistenceQuery);
        } else if (persistenceQuery instanceof PersistenceQueryFindAllInstances) {
            return getAllInstances((PersistenceQueryFindAllInstances) persistenceQuery);
        } else if (persistenceQuery instanceof PersistenceQueryFindByPattern) {
            return findByPattern((PersistenceQueryFindByPattern) persistenceQuery);
        } else {
            return findDefaultr(persistenceQuery);
        }
    }

    private List<ObjectAdapter> findDefaultr(PersistenceQuery persistenceQuery) {
        final List<ObjectAdapter> instances = Lists.newArrayList();
        final ObjectSpecification specification = persistenceQuery.getSpecification();
        final Iterator<StateReader> instanceData = database.instancesOf(specification.getSpecId());
        while (instanceData.hasNext()) {
            final StateReader reader = instanceData.next();
            final ObjectAdapter instance = objectReader.load(reader, versionCreator, availableDataEncrypters);
            instances.add(instance);
        }
        return instances;
    }

    private List<ObjectAdapter> findByPattern(PersistenceQueryFindByPattern query) {
        final ObjectSpecification specification = query.getSpecification();
        final List<ObjectAdapter> instances = Lists.newArrayList();
        appendPatternInstances(query, specification, instances);
        return instances;
    }
    
    private void appendPatternInstances(final PersistenceQueryFindByPattern persistenceQuery, final ObjectSpecification specification, final List<ObjectAdapter> instances) {      
        final Iterator<StateReader> instanceData = database.instancesOf(specification.getSpecId(), persistenceQuery.getPattern());
        while (instanceData.hasNext()) {
            final StateReader reader = instanceData.next();
            final ObjectAdapter instance = objectReader.load(reader, versionCreator, availableDataEncrypters);
            instances.add(instance);
        }
        for (final ObjectSpecification spec : specification.subclasses()) {
            appendPatternInstances(persistenceQuery, spec, instances);
        }
    }


    private List<ObjectAdapter> getAllInstances(PersistenceQuery query) {
        final ObjectSpecification specification = query.getSpecification();
        final List<ObjectAdapter> instances = Lists.newArrayList();
        appendInstances(query, specification, instances);
        return instances;
    }

    private void appendInstances(final PersistenceQuery persistenceQuery, final ObjectSpecification specification, final List<ObjectAdapter> instances) {      
        final Iterator<StateReader> instanceData = database.instancesOf(specification.getSpecId());
        while (instanceData.hasNext()) {
            final StateReader reader = instanceData.next();
            final ObjectAdapter instance = objectReader.load(reader, versionCreator, availableDataEncrypters);
            
            // TODO deal with this natively
            if (persistenceQuery instanceof PersistenceQueryBuiltIn) {
                if (!((PersistenceQueryBuiltIn) persistenceQuery).matches(instance)) {
                    continue;
                }
            }
            instances.add(instance);
        }
        for (final ObjectSpecification spec : specification.subclasses()) {
            appendInstances(persistenceQuery, spec, instances);
        }
    }



    @Override
    public ObjectAdapter loadInstanceAndAdapt(final TypedOid oid) {
        final String key = keyCreator.getIdentifierForPersistentRoot(oid);
        final ObjectSpecification objectSpec = getSpecificationLookup().lookupBySpecId(oid.getObjectSpecId());
        final StateReader reader = database.getInstance(key, objectSpec.getSpecId());
        return objectReader.load(reader, versionCreator, availableDataEncrypters);
    }

    @Override
    public boolean hasInstances(final ObjectSpecification specification) {
        return database.hasInstances(specification.getSpecId());
    }

    @Override
    public boolean isFixturesInstalled() {
        return isDataLoaded;
    }

    @Override
    public void reset() {
    }

    @Override
    public void resolveField(final ObjectAdapter object, final ObjectAssociation field) {
        final ObjectAdapter fieldValue = field.get(object);
        if (fieldValue != null && !fieldValue.isResolved() && !fieldValue.getSpecification().isParented()) {
            resolveImmediately(fieldValue);
        }
    }

    @Override
    public void resolveImmediately(final ObjectAdapter adapter) {
        final Oid oid = adapter.getOid();
        if (!(oid instanceof AggregatedOid)) {
            final ObjectSpecification objectSpec = adapter.getSpecification();
            final String key = keyCreator.getIdentifierForPersistentRoot(oid);
            final StateReader reader = database.getInstance(key, objectSpec.getSpecId());
            objectReader.update(reader, versionCreator, availableDataEncrypters, adapter);
        }
    }

    @Override
    public void debugData(final DebugBuilder debug) {
        // TODO show details
    }

    // ////////////////////////////////////////////////////////////////
    // open, close
    // ////////////////////////////////////////////////////////////////

    @Override
    public void close() {
        database.close();
    }

    @Override
    public void open() {
        database.open();
    }

    @Override
    public String name() {
        return "nosql";
    }

    
    // ////////////////////////////////////////////////////////////////
    // Services
    // ////////////////////////////////////////////////////////////////
    
    @Override
    public void registerService(final RootOid rootOid) {
        final String key = keyCreator.getIdentifierForPersistentRoot(rootOid);
        database.addService(rootOid.getObjectSpecId(), key);
    }

    @Override
    public RootOid getOidForService(ObjectSpecification serviceSpec) {
        final ObjectSpecId objectSpecId = serviceSpec.getSpecId();
        RootOid oid = servicesByObjectSpecId.get(objectSpecId);
        if (oid == null) {
            final String id = database.getService(objectSpecId);
            if (id == null) {
                oid = null;
            } else {
                oid = keyCreator.createRootOid(serviceSpec, id);
            }
            servicesByObjectSpecId.put(objectSpecId, oid);
        }
        return oid;
    }


    // ////////////////////////////////////////////////////////////////
    // Transaction Mgmt
    // ////////////////////////////////////////////////////////////////

    @Override
    public void abortTransaction() {
    }

    @Override
    public void endTransaction() {
    }

    @Override
    public void startTransaction() {
    }

    
    // ////////////////////////////////////////////////////////////////
    // debugging
    // ////////////////////////////////////////////////////////////////

    @Override
    public String debugTitle() {
        return "NoSql Object Store";
    }


    // ////////////////////////////////////////////////////////////////
    // Dependencies (from context)
    // ////////////////////////////////////////////////////////////////

    protected AdapterManager getAdapterManager() {
        return getPersistenceSession().getAdapterManager();
    }

    protected PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    protected SpecificationLoader getSpecificationLookup() {
        return ExpressiveObjectsContext.getSpecificationLoader();
    }

}
