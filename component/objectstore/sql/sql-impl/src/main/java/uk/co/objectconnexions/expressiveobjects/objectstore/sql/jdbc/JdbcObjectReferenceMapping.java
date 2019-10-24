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

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.DatabaseConnector;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.IdMappingAbstract;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Results;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Sql;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.SqlObjectStoreException;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.mapping.ObjectReferenceMapping;

public class JdbcObjectReferenceMapping extends IdMappingAbstract implements ObjectReferenceMapping {
    private final ObjectSpecification specification;

    public JdbcObjectReferenceMapping(final String columnName, final ObjectSpecification specification) {
        this.specification = specification;
        final String idColumn = Sql.sqlName("fk_" + columnName);
        setColumn(idColumn);
    }

    @Override
    public void appendUpdateValues(final DatabaseConnector connector, final StringBuffer sql, final ObjectAdapter adapter) {
        sql.append(getColumn());
        if (adapter == null) {
            sql.append("= NULL ");
        } else {
            sql.append("= ?");
            // sql.append(primaryKey(object.getOid()));
            final RootOid oid = (RootOid) adapter.getOid();
            connector.addToQueryValues(primaryKey(oid));
        }
    }

    public ObjectAdapter initializeField(final Results rs) {
        final Oid oid = recreateOid(rs, specification);
        if (oid != null) {
            if (specification.isAbstract()) {
                throw new SqlObjectStoreException("NOT DEALING WITH POLYMORPHIC ASSOCIATIONS");
            } else {
                return getAdapter(specification, oid);
            }
        } else {
            return null;
        }
    }

}
