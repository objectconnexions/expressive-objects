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

package uk.co.objectconnexions.expressiveobjects.objectstore.sql;

import java.util.List;
import java.util.Vector;

import com.google.common.collect.Lists;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugString;
import uk.co.objectconnexions.expressiveobjects.core.commons.ensure.ExpressiveObjectsAssertException;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.NotYetImplementedException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.CollectionOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.TypedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.ObjectStoreSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.CreateObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.DestroyObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommandContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.SaveObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.query.PersistenceQueryFindAllInstances;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.query.PersistenceQueryFindByPattern;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.query.PersistenceQueryFindByTitle;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceQuery;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.MessageBroker;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.UpdateNotifier;

public final class SqlObjectStore implements ObjectStoreSpi {

    private static final String TABLE_NAME = "expressive-objects_admin_services";
    // private static final String ID_COLUMN = "id";
    // private static final String PRIMARYKEY_COLUMN = "pk_id";
    public static final String BASE_NAME = "expressive-objects.persistor.sql";
    private static final Logger LOG = Logger.getLogger(SqlObjectStore.class);
    private DatabaseConnectorPool connectionPool;
    private ObjectMappingLookup objectMappingLookup;
    private boolean isInitialized;

    @Override
    public String name() {
        return "SQL Object Store";
    }

    @Override
    public void open() {
        Sql.setMetaData(connectionPool.acquire().getMetaData());

        if (!isInitialized) {
            Defaults.initialise(BASE_NAME, ExpressiveObjectsContext.getConfiguration());
            Defaults.setPkIdLabel(Sql.identifier(Defaults.getPkIdLabel()));
            Defaults.setIdColumn(Sql.identifier(Defaults.getIdColumn()));
        }

        final DebugBuilder debug = new DebugString();
        connectionPool.debug(debug);
        LOG.info("Database: " + debug);

        objectMappingLookup.init();

        final DatabaseConnector connector = connectionPool.acquire();
        final String tableIdentifier = Sql.tableIdentifier(TABLE_NAME);
        isInitialized = connector.hasColumn(tableIdentifier, Defaults.getPkIdLabel());
        if (!isInitialized) {
            if (connector.hasTable(tableIdentifier)) {
                final StringBuffer sql = new StringBuffer();
                sql.append("drop table ");
                sql.append(tableIdentifier);
                connector.update(sql.toString());
            }
            final StringBuffer sql = new StringBuffer();
            sql.append("create table ");
            sql.append(tableIdentifier);
            sql.append(" (");
            sql.append(Defaults.getPkIdLabel());
            sql.append(" int, ");
            sql.append(Defaults.getIdColumn());
            sql.append(" varchar(255)");
            sql.append(")");
            connector.update(sql.toString());
        }
    }

    @Override
    public boolean isFixturesInstalled() {
        return isInitialized;
    }

    @Override
    public void registerService(final RootOid rootOid) {
        final DatabaseConnector connector = connectionPool.acquire();

        final StringBuffer sql = new StringBuffer();
        sql.append("insert into ");
        sql.append(Sql.tableIdentifier(TABLE_NAME));
        sql.append(" (");
        sql.append(Defaults.getPkIdLabel());
        sql.append(", ");
        sql.append(Defaults.getIdColumn());
        sql.append(") values (?,?)");

        final RootOidDefault sqlOid = (RootOidDefault) rootOid;
        connector.addToQueryValues(sqlOid.getIdentifier());
        connector.addToQueryValues(rootOid.getObjectSpecId().asString());

        connector.insert(sql.toString());
        connectionPool.release(connector);
    }

    @Override
    public void reset() {
    }

    @Override
    public void close() {
        objectMappingLookup.shutdown();
        connectionPool.shutdown();
    }

    @Override
    public void startTransaction() {
        executeSql(Defaults.START_TRANSACTION());
    }

    @Override
    public void abortTransaction() {
        executeSql(Defaults.ABORT_TRANSACTION());
    }

    @Override
    public void endTransaction() {
        executeSql(Defaults.COMMIT_TRANSACTION());
    }

    private void executeSql(String sql) {
        final DatabaseConnector connector = connectionPool.acquire();
        try {
            connector.begin();
            connector.update(sql);
            connector.commit();
            // connector.close();
        } finally {
            connectionPool.release(connector);
        }
    }

    @Override
    public CreateObjectCommand createCreateObjectCommand(final ObjectAdapter object) {
        return new CreateObjectCommand() {
            @Override
            public void execute(final PersistenceCommandContext context) {
                final DatabaseConnector connection = ((SqlExecutionContext) context).getConnection();
                LOG.debug("  create object " + object);
                final ObjectMapping mapping = objectMappingLookup.getMapping(object, connection);
                mapping.createObject(connection, object);
            }

            @Override
            public ObjectAdapter onAdapter() {
                return object;
            }

            @Override
            public String toString() {
                return "CreateObjectCommand [object=" + object + "]";
            }
        };
    }

    @Override
    public DestroyObjectCommand createDestroyObjectCommand(final ObjectAdapter object) {
        return new DestroyObjectCommand() {
            @Override
            public void execute(final PersistenceCommandContext context) {
                final DatabaseConnector connection = ((SqlExecutionContext) context).getConnection();
                LOG.debug("  destroy object " + object);
                final ObjectMapping mapping = objectMappingLookup.getMapping(object, connection);
                mapping.destroyObject(connection, object);
            }

            @Override
            public ObjectAdapter onAdapter() {
                return object;
            }

            @Override
            public String toString() {
                return "DestroyObjectCommand [object=" + object + "]";
            }
        };
    }

    @Override
    public SaveObjectCommand createSaveObjectCommand(final ObjectAdapter adapter) {
        return new SaveObjectCommand() {
            @Override
            public void execute(final PersistenceCommandContext context) {
                final DatabaseConnector connection = ((SqlExecutionContext) context).getConnection();
                LOG.debug("  save object " + adapter.toString());

                try {
                    final ObjectSpecification adapterSpec = adapter.getSpecification();
                    if (!adapterSpec.isParented()) {
                        saveRootAdapter(adapter, connection);
                    } else if(adapterSpec.isParentedOrFreeCollection()) { 
                        saveParentedCollectionAdapter(adapter, connection);
                    } else {
                        throw new NotYetImplementedException("cannot yet persist aggregated objects: " + adapter.toString());
                    }
                } finally {
                    connectionPool.release(connection);
                }
            }

            private void saveRootAdapter(final ObjectAdapter adapter, final DatabaseConnector connection) {
                final ObjectMapping mapping = objectMappingLookup.getMapping(adapter, connection);
                mapping.save(connection, adapter);
            }

            private void saveParentedCollectionAdapter(final ObjectAdapter collectionAdapter, final DatabaseConnector connection) {
                final ObjectAdapter parent = collectionAdapter.getAggregateRoot();
                LOG.debug("change to internal collection being persisted through parent");
   
                final Oid oid = collectionAdapter.getOid();
                final CollectionOid collectionOid = (CollectionOid) oid;
                if (!(oid instanceof CollectionOid)) {
                    throw new ExpressiveObjectsAssertException("object should have a CollectionOid");
                }
                
                final ObjectMapping mapping = objectMappingLookup.getMapping(parent, connection);
                if (! mapping.saveCollection(connection, parent, collectionOid.getName()) ) {
                    final ObjectMapping parentMapping = objectMappingLookup.getMapping(parent, connection);
                    parentMapping.save(connection, collectionAdapter);
                }
            }

            @Override
            public ObjectAdapter onAdapter() {
                return adapter;
            }

            @Override
            public String toString() {
                return "SaveObjectCommand [object=" + adapter + "]";
            }

        };
    }


    @Override
    public void debugData(final DebugBuilder debug) {
        debug.appendln("initialised", isInitialized);
        debug.appendln("connection pool", connectionPool);
        debug.appendln("Database:");
        debug.indent();
        connectionPool.debug(debug);
        debug.unindent();
        objectMappingLookup.debugData(debug);
    }

    @Override
    public String debugTitle() {
        return null;
    }

    @Override
    public void execute(final List<PersistenceCommand> commands) {
        final DatabaseConnector connector = connectionPool.acquire();
        connector.begin();

        final ExpressiveObjectsTransactionManager transactionManager = ExpressiveObjectsContext.getTransactionManager();
        final MessageBroker messageBroker = ExpressiveObjectsContext.getMessageBroker();
        final UpdateNotifier updateNotifier = ExpressiveObjectsContext.getUpdateNotifier();
        final SqlExecutionContext context =
            new SqlExecutionContext(connector, transactionManager, messageBroker, updateNotifier);
        try {
            for (final PersistenceCommand command : commands) {
                command.execute(context);
            }
            connector.commit();
        } catch (final ExpressiveObjectsException e) {
            LOG.warn("Failure during execution", e);
            connector.rollback();
            throw e;
        } finally {
            connectionPool.release(connector);
        }
    }

    public boolean flush(final PersistenceCommand[] commands) {
        return false;
    }

    @Override
    public List<ObjectAdapter> loadInstancesAndAdapt(final PersistenceQuery query) {
        if (query instanceof PersistenceQueryFindByTitle) {
            return findByTitle((PersistenceQueryFindByTitle) query);
        } else if (query instanceof PersistenceQueryFindAllInstances) {
            return getAllInstances((PersistenceQueryFindAllInstances) query);
        } else if (query instanceof PersistenceQueryFindByPattern) {
            return findByPattern((PersistenceQueryFindByPattern) query);
        } else {
            throw new SqlObjectStoreException("Query type not supported: " + query);
        }
    }

    private List<ObjectAdapter> findByPattern(final PersistenceQueryFindByPattern query) {
        final ObjectSpecification specification = query.getSpecification();// query.getPattern().getSpecification();//
                                                                           // getSpecification();
        final DatabaseConnector connector = connectionPool.acquire();
        try {
            final List<ObjectAdapter> matchingInstances = Lists.newArrayList();

            addSpecQueryInstances(specification, connector, query, matchingInstances);
            return matchingInstances;

        } finally {
            connectionPool.release(connector);
        }
    }

    private void addSpecQueryInstances(final ObjectSpecification specification, final DatabaseConnector connector,
        final PersistenceQueryFindByPattern query, final List<ObjectAdapter> matchingInstances) {

        if (specification.isAbstract() == false) {
            final ObjectMapping mapper = objectMappingLookup.getMapping(specification, connector);
            final Vector<ObjectAdapter> instances = mapper.getInstances(connector, specification, query);
            matchingInstances.addAll(instances);

        }
        if (specification.hasSubclasses()) {
            final List<ObjectSpecification> subclasses = specification.subclasses();
            for (final ObjectSpecification subclassSpec : subclasses) {
                addSpecQueryInstances(subclassSpec, connector, query, matchingInstances);
            }
        }
    }

    private List<ObjectAdapter> getAllInstances(final PersistenceQueryFindAllInstances criteria) {
        final ObjectSpecification spec = criteria.getSpecification();
        return allInstances(spec);
    }

    private List<ObjectAdapter> allInstances(final ObjectSpecification spec) {
        final DatabaseConnector connector = connectionPool.acquire();
        final List<ObjectAdapter> matchingInstances = Lists.newArrayList();

        addSpecInstances(spec, connector, matchingInstances);

        connectionPool.release(connector);
        return matchingInstances;
    }

    private void addSpecInstances(final ObjectSpecification spec, final DatabaseConnector connector,
        final List<ObjectAdapter> matchingInstances) {

        if (!spec.isAbstract()) {
            final ObjectMapping mapper = objectMappingLookup.getMapping(spec, connector);
            final List<ObjectAdapter> instances = mapper.getInstances(connector, spec);
            matchingInstances.addAll(instances);
        }

        if (spec.hasSubclasses()) {
            final List<ObjectSpecification> subclasses = spec.subclasses();
            for (final ObjectSpecification subclassSpec : subclasses) {
                addSpecInstances(subclassSpec, connector, matchingInstances);
            }
        }

    }

    private List<ObjectAdapter> findByTitle(final PersistenceQueryFindByTitle criteria) {
        final ObjectSpecification spec = criteria.getSpecification();
        final DatabaseConnector connector = connectionPool.acquire();
        final ObjectMapping mapper = objectMappingLookup.getMapping(spec, connector);

        final Vector<ObjectAdapter> instances = mapper.getInstances(connector, spec, criteria.getTitle());
        connectionPool.release(connector);

        return instances;
    }


    @Override
    public ObjectAdapter loadInstanceAndAdapt(final TypedOid oid) {
        final DatabaseConnector connection = connectionPool.acquire();
        final ObjectSpecification objectSpec = getSpecificationLookup().lookupBySpecId(oid.getObjectSpecId());
        final ObjectMapping mapper = objectMappingLookup.getMapping(objectSpec, connection);
        final ObjectAdapter object = mapper.getObject(connection, oid);
        connectionPool.release(connection);
        return object;
    }

    @Override
    public RootOid getOidForService(ObjectSpecification serviceSpec) {

        final DatabaseConnector connector = connectionPool.acquire();
        try {
            final StringBuffer sql = new StringBuffer();
            sql.append("select ");
            sql.append(Defaults.getPkIdLabel());
            sql.append(" from ");
            sql.append(Sql.tableIdentifier(TABLE_NAME));
            sql.append(" where ");
            sql.append(Defaults.getIdColumn());
            sql.append(" = ?");
            connector.addToQueryValues(serviceSpec.getSpecId().asString());

            final Results results = connector.select(sql.toString());
            if (!results.next()) {
                return null;
            } 
            final int id = results.getInt(Defaults.getPkIdLabel());
            return RootOidDefault.create(serviceSpec.getSpecId(), ""+id);
            
        } finally {
            connectionPool.release(connector);
        }
    }

    @Override
    public boolean hasInstances(final ObjectSpecification spec) {
        final DatabaseConnector connection = connectionPool.acquire();
        final ObjectMapping mapper = objectMappingLookup.getMapping(spec, connection);
        final boolean hasInstances = mapper.hasInstances(connection, spec);
        connectionPool.release(connection);
        return hasInstances;
    }

    @Override
    public void resolveField(final ObjectAdapter object, final ObjectAssociation field) {
        if (field.isOneToManyAssociation()) {
            final DatabaseConnector connection = connectionPool.acquire();
            final ObjectSpecification spec = object.getSpecification();
            final ObjectMapping mapper = objectMappingLookup.getMapping(spec, connection);
            mapper.resolveCollection(connection, object, field);
            connectionPool.release(connection);
        } else {
            resolveImmediately(field.get(object));
        }
    }

    @Override
    public void resolveImmediately(final ObjectAdapter object) {
        final DatabaseConnector connector = connectionPool.acquire();
        final ObjectMapping mapping = objectMappingLookup.getMapping(object, connector);
        mapping.resolve(connector, object);
        connectionPool.release(connector);
    }

    ///////////////////////////////////////////////////////////
    // Dependencies (injected)
    ///////////////////////////////////////////////////////////

    public void setConnectionPool(final DatabaseConnectorPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void setMapperLookup(final ObjectMappingLookup mapperLookup) {
        this.objectMappingLookup = mapperLookup;
    }


    ///////////////////////////////////////////////////////////
    // Dependencies (from context)
    ///////////////////////////////////////////////////////////
    
    protected AdapterManager getAdapterManager() {
        return ExpressiveObjectsContext.getPersistenceSession().getAdapterManager();
    }

    protected SpecificationLoader getSpecificationLookup() {
        return ExpressiveObjectsContext.getSpecificationLoader();
    }

}
