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

import uk.co.objectconnexions.expressiveobjects.applib.PersistFailedException;
import uk.co.objectconnexions.expressiveobjects.applib.value.DateTime;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.AbstractFieldMappingFactory;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Defaults;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Results;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.mapping.FieldMapping;

public class JdbcDateTimeMapper extends AbstractJdbcFieldMapping {

    private final String dataType;

    public static class Factory extends AbstractFieldMappingFactory {
        @Override
        public FieldMapping createFieldMapping(final ObjectSpecification object, final ObjectAssociation field) {
            final String dataType = getTypeOverride(object, field, Defaults.TYPE_DATETIME());
            return new JdbcDateTimeMapper(field, dataType);
        }
    }

    protected JdbcDateTimeMapper(final ObjectAssociation field, final String dataType) {
        super(field);
        this.dataType = dataType;
    }

    @Override
    protected Object preparedStatementObject(final ObjectAdapter value) {
        final DateTime asDate = (DateTime) value.getObject();
        final java.sql.Timestamp dateTime = new java.sql.Timestamp(asDate.millisSinceEpoch());
        return dateTime;
    }

    @Override
    public ObjectAdapter setFromDBColumn(final Results results, final String columnName, final ObjectAssociation field) {

        ObjectAdapter restoredValue;
        final Class<?> correspondingClass = field.getSpecification().getCorrespondingClass();
        if (correspondingClass == DateTime.class) {
            final java.sql.Timestamp o = (java.sql.Timestamp) results.getObject(columnName);
            final DateTime timeValue = new DateTime(o.getTime());
            restoredValue = ExpressiveObjectsContext.getPersistenceSession().getAdapterManager().adapterFor(timeValue);
        } else {
            throw new PersistFailedException("Unhandled time type: " + correspondingClass.getCanonicalName());
        }
        return restoredValue;
    }

    @Override
    public String columnType() {
        return dataType;
    }

}
