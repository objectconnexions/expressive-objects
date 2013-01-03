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

package uk.co.objectconnexions.expressiveobjects.objectstore.sql.jdbc;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.AbstractFieldMappingFactory;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.DatabaseConnector;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Defaults;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Results;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Sql;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.mapping.FieldMapping;

public class JdbcObjectReferenceFieldMapping extends JdbcObjectReferenceMapping implements FieldMapping {

    public static class Factory extends AbstractFieldMappingFactory {
        @Override
        public FieldMapping createFieldMapping(final ObjectSpecification object, final ObjectAssociation field) {
            if (field.getSpecification().isAbstract()) {
                final String dataType = getTypeOverride(object, field, Defaults.TYPE_LONG_STRING());
                return new JdbcAbstractReferenceFieldMapping(field, dataType);
            }
            return new JdbcObjectReferenceFieldMapping(field);
        }
    }

    protected final ObjectAssociation field;

    @Override
    public ObjectAssociation getField() {
        return field;
    }

    @Override
    public void appendWhereClause(final DatabaseConnector connector, final StringBuffer sql, final ObjectAdapter object) {
        final ObjectAdapter fieldValue = field.get(object);
        final RootOid oid = (RootOid) fieldValue.getOid();
        appendWhereClause(connector, sql, oid);
    }

    @Override
    public void appendWhereObject(final DatabaseConnector connector, final ObjectAdapter objectAdapter) {
        final ObjectAdapter fieldValue = field.get(objectAdapter);
        final RootOid oid = (RootOid) fieldValue.getOid();
        connector.addToQueryValues(primaryKey(oid));
    }

    public JdbcObjectReferenceFieldMapping(final ObjectAssociation field) {
        super(columnName(field), field.getSpecification());
        this.field = field;
    }

    private static String columnName(final ObjectAssociation field) {
        return Sql.sqlFieldName(field.getId());
    }

    @Override
    public void appendInsertValues(final DatabaseConnector connector, final StringBuffer sb, final ObjectAdapter object) {
        final ObjectAdapter fieldValue = field.get(object);
        super.appendInsertValues(connector, sb, fieldValue);
    }

    @Override
    public void appendUpdateValues(final DatabaseConnector connector, final StringBuffer sql, final ObjectAdapter object) {
        final ObjectAdapter fieldValue = field.get(object);
        super.appendUpdateValues(connector, sql, fieldValue);
    }

    @Override
    public void initializeField(final ObjectAdapter object, final Results rs) {
        final ObjectAdapter reference = initializeField(rs);
        ((OneToOneAssociation) field).initAssociation(object, reference);
    }

    @Override
    public void debugData(final DebugBuilder debug) {
        debug.appendln(field.getId(), getColumn());
    }

}
