/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.co.objectconnexions.expressiveobjects.objectstore.sql.jdbc;

import uk.co.objectconnexions.expressiveobjects.applib.value.Color;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.AbstractFieldMappingFactory;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.Results;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.mapping.FieldMapping;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class JdbcColorValueMapper extends AbstractJdbcFieldMapping {

    public static class Factory extends AbstractFieldMappingFactory {
        private final String type;

        public Factory(final String type) {
            super();
            this.type = type;
        }

        @Override
        public FieldMapping createFieldMapping(final ObjectSpecification object, final ObjectAssociation field) {
            final String dataType = getTypeOverride(object, field, type);
            return new JdbcColorValueMapper(field, dataType);
        }
    }

    private final String type;

    public JdbcColorValueMapper(final ObjectAssociation field, final String type) {
        super(field);
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.objectconnexions.expressiveobjects.runtimes.dflt.objectstores.sql.jdbc.AbstractJdbcFieldMapping
     * #columnType()
     */
    @Override
    protected String columnType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.objectconnexions.expressiveobjects.runtimes.dflt.objectstores.sql.jdbc.AbstractJdbcFieldMapping
     * #preparedStatementObject(org.apache
     * .expressiveobjects.core.metamodel.adapter.ObjectAdapter)
     */
    @Override
    protected Object preparedStatementObject(final ObjectAdapter value) {
        final Object o = value.getObject();
        return ((Color) o).intValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.objectconnexions.expressiveobjects.runtimes.dflt.objectstores.sql.jdbc.AbstractJdbcFieldMapping
     * #setFromDBColumn(uk.co.objectconnexions.expressiveobjects. runtimes.dflt.objectstores.sql.Results,
     * java.lang.String,
     * uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation)
     */
    @Override
    protected ObjectAdapter setFromDBColumn(final Results results, final String columnName, final ObjectAssociation field) {
        ObjectAdapter restoredValue;
        final int intValue = results.getInt(columnName);
        final Color colorValue = new Color(intValue);
        restoredValue = ExpressiveObjectsContext.getPersistenceSession().getAdapterManager().adapterFor(colorValue);
        return restoredValue;
    }

}
