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

import org.joda.time.LocalDate;

import uk.co.objectconnexions.expressiveobjects.applib.PersistFailedException;
import uk.co.objectconnexions.expressiveobjects.applib.value.Date;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsApplicationException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.AbstractFieldMappingFactory;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Defaults;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Results;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.mapping.FieldMapping;

/**
 * Handles reading and writing java.sql.Date and uk.co.objectconnexions.expressiveobjects.applib.value.Date values to and from the data store.
 * 
 * 
 * @version $Rev$ $Date$
 */
public class JdbcDateMapper extends AbstractJdbcFieldMapping {

    private final String dataType;

    public static class Factory extends AbstractFieldMappingFactory {
        @Override
        public FieldMapping createFieldMapping(final ObjectSpecification object, final ObjectAssociation field) {
            final String dataType = getTypeOverride(object, field, Defaults.TYPE_DATE());
            return new JdbcDateMapper(field, dataType);
        }
    }

    protected JdbcDateMapper(final ObjectAssociation field, final String dataType) {
        super(field);
        this.dataType = dataType;
    }

    @Override
    protected Object preparedStatementObject(final ObjectAdapter value) {
        final Object o = value.getObject();
        if (o instanceof java.sql.Date) {
            final java.sql.Date javaSqlDate = (java.sql.Date) value.getObject();
            final long millisSinceEpoch = javaSqlDate.getTime();
            return new LocalDate(millisSinceEpoch);
        } else if (o instanceof Date) {
            final Date asDate = (Date) value.getObject();
            return new LocalDate(asDate.getMillisSinceEpoch());
        } else {
            throw new ExpressiveObjectsApplicationException("Unimplemented JdbcDateMapper instance type: "
                + value.getClass().toString());
        }
    }

    @Override
    public ObjectAdapter setFromDBColumn(final Results results, final String columnName, final ObjectAssociation field) {
        ObjectAdapter restoredValue;
        final java.util.Date javaDateValue = results.getJavaDateOnly(columnName);
        final Class<?> correspondingClass = field.getSpecification().getCorrespondingClass();
        if (correspondingClass == java.util.Date.class || correspondingClass == java.sql.Date.class) {
            // 2011-04-08 = 1270684800000
            restoredValue = getAdapterManager().adapterFor(javaDateValue);
        } else if (correspondingClass == Date.class) {
            // 2010-03-05 = 1267747200000
            Date dateValue;
            dateValue = new Date(javaDateValue);
            restoredValue = getAdapterManager().adapterFor(dateValue);
        } else {
            throw new PersistFailedException("Unhandled date type: " + correspondingClass.getCanonicalName());
        }
        return restoredValue;
    }

    @Override
    public String columnType() {
        return dataType;
    }

    protected PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    protected AdapterManager getAdapterManager() {
        return getPersistenceSession().getAdapterManager();
    }
}
