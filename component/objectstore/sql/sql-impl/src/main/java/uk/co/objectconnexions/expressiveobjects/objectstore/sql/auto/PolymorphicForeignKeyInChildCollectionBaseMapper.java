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

/**
 * 
 */
package uk.co.objectconnexions.expressiveobjects.objectstore.sql.auto;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.TypedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.OidGenerator;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.DatabaseConnector;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Defaults;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.FieldMappingLookup;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.IdMappingAbstract;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.ObjectMapping;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.ObjectMappingLookup;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Results;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Sql;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.VersionMapping;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.jdbc.JdbcPolymorphicObjectReferenceMapping;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.mapping.FieldMapping;

/**
 * Used to map 1-to-many collections by creating, in the collection child table
 * (which may be an interface or abstract class), 2 columns per parent
 * collection. The first column is the class type, the second is the entity ID.
 * The columns are named by combining the final part of the parent class name
 * and the collection variable name.
 * 
 * You have a choice between this class and
 * {@link PolymorphicForeignKeyInChildCollectionMapper}
 * 
 * @author Kevin
 */
public class PolymorphicForeignKeyInChildCollectionBaseMapper extends ForeignKeyInChildCollectionMapper {

    private static final Logger LOG = Logger.getLogger(PolymorphicForeignKeyInChildCollectionBaseMapper.class);

    private final String classColumnName;
    private final String itemIdColumnName;
    private final IdMappingAbstract polyIdMapper;

    private final OidGenerator oidGenerator;

    public PolymorphicForeignKeyInChildCollectionBaseMapper(final ObjectAssociation objectAssociation, final String parameterBase, final FieldMappingLookup lookup, final ObjectMappingLookup objectMapperLookup, final AbstractAutoMapper abstractAutoMapper, final ObjectAssociation field) {

        super(objectAssociation, parameterBase, lookup, objectMapperLookup, abstractAutoMapper, field);

        classColumnName = Sql.identifier(Sql.sqlName(getForeignKeyName() + "_cls"));
        itemIdColumnName = Sql.identifier("item_id");

        polyIdMapper = new JdbcPolymorphicObjectReferenceMapping(itemIdColumnName);
        oidGenerator = ExpressiveObjectsContext.getPersistenceSession().getOidGenerator();
    }

    @Override
    public boolean needsTables(final DatabaseConnector connection) {
        return super.needsTables(connection) || !connection.hasColumn(table, classColumnName);
    }

    @Override
    public void createTables(final DatabaseConnector connection) {
        if (super.needsTables(connection)) {
            super.createTables(connection);
        }

        if (!connection.hasColumn(table, classColumnName)) {
            addColumn(connection, classColumnName, Defaults.TYPE_LONG_STRING());
            addColumn(connection, itemIdColumnName, Defaults.TYPE_PK());
        }
    }

    protected void addColumn(final DatabaseConnector connection, final String columnName, final String columnType) {
        final StringBuffer sql = new StringBuffer();
        sql.append("alter table ");
        sql.append(table);
        sql.append(" add ");
        sql.append(columnName);
        sql.append(" ");
        sql.append(columnType);
        connection.update(sql.toString());
    }

    @Override
    protected void appendCollectionUpdateColumnsToNull(final StringBuffer sql) {
        super.appendCollectionUpdateColumnsToNull(sql);
        sql.append(", " + classColumnName + "=NULL ");
    }

    @Override
    protected void appendCollectionUpdateValues(final DatabaseConnector connector, final ObjectAdapter parent, final StringBuffer sql) {
        super.appendCollectionUpdateValues(connector, parent, sql);
    }

    @Override
    protected void appendColumnDefinitions(final StringBuffer sql) {
        super.appendColumnDefinitions(sql);
    }

    @Override
    protected void clearCollectionParent(final DatabaseConnector connector, final ObjectAdapter parent) {
        // Delete collection parent
        final StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM ");
        sql.append(table);
        sql.append(" WHERE ");
        appendCollectionWhereValues(connector, parent, sql);
        connector.update(sql.toString());
    }

    @Override
    protected void resetCollectionParent(final DatabaseConnector connector, final ObjectAdapter parent, final Iterator<ObjectAdapter> elements) {
        LOG.debug("Saving polymorphic list");

        ObjectSpecification elementSpecification;
        while (elements.hasNext()) {
            final ObjectAdapter thisAdapter = elements.next();
            elementSpecification = thisAdapter.getSpecification();

            // Reinstall collection parent
            final StringBuffer update = new StringBuffer();
            update.append("INSERT INTO ");
            update.append(table);
            update.append(" (");
            // list of column names
            super.getIdMapping().appendColumnNames(update);
            update.append("," + getForeignKeyName());
            update.append(", " + itemIdColumnName);
            update.append("," + classColumnName);
            update.append(") VALUES (");

            // Row ID column
            final Object pojo = thisAdapter.getObject();
            final RootOid transientRootOid = oidGenerator.createTransientOid(pojo);
            
            final RootOid persistentRootOid = oidGenerator.createPersistent(pojo, transientRootOid);
            
            polyIdMapper.appendObjectId(connector, update, persistentRootOid);
            
            // polyIdMapper.appendObjectId(connector, update,
            // thisAdapter.getOid());
            update.append(",");

            // Foreign key ID column
            getForeignKeyMapping().appendInsertValues(connector, update, parent);
            update.append(",");

            // item Id column
            final RootOid oid = (RootOid) thisAdapter.getOid();
            getIdMapping().appendObjectId(connector, update, oid);

            // Class name column
            update.append(",?)");
            connector.addToQueryValues(elementSpecification.getFullIdentifier());

            connector.insert(update.toString());
        }
    }

    @Override
    public IdMappingAbstract getIdMapping() {
        return polyIdMapper;
    }

    @Override
    protected void loadCollectionIntoList(final DatabaseConnector connector, final ObjectAdapter parent, final String table, final ObjectSpecification specification, final IdMappingAbstract idMappingAbstract, final Map<ObjectAssociation, FieldMapping> fieldMappingByField, final VersionMapping versionMapping,
            final List<ObjectAdapter> list) {
        LOG.debug("Loading polymorphic list");

        final StringBuffer sql = new StringBuffer();
        sql.append("select ");
        super.getIdMapping().appendColumnNames(sql);

        sql.append("," + getForeignKeyName());
        sql.append("," + classColumnName);
        sql.append("," + itemIdColumnName);

        sql.append(" from ");
        sql.append(table);
        sql.append(" where ");
        appendCollectionWhereValues(connector, parent, sql);

        final Results rs = connector.select(sql.toString());

        final SpecificationLoaderSpi reflector = ExpressiveObjectsContext.getSpecificationLoader();
        final JdbcPolymorphicObjectReferenceMapping idMapping = (JdbcPolymorphicObjectReferenceMapping) idMappingAbstract;

        while (rs.next()) {
            final ObjectSpecification itemSpecification = reflector.loadSpecification(rs.getString(classColumnName));
            idMapping.setObjectSpecification(itemSpecification);

            // Load new recordSet for the actual class
            final ObjectMapping itemMapper = objectMappingLookup.getMapping(itemSpecification, connector);
            final TypedOid oid = idMapping.recreateOid(rs, itemSpecification);
            final ObjectAdapter loadedObject = itemMapper.getObject(connector, oid);

            LOG.debug("  element  " + loadedObject.getOid());

            list.add(loadedObject);
        }
        rs.close();

    }
}
