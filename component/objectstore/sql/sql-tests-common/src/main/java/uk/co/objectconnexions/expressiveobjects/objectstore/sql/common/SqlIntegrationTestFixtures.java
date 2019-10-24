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
package uk.co.objectconnexions.expressiveobjects.objectstore.sql.common;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationDefault;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures;
import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures.Fixtures.Initialization;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.PersistenceMechanismInstallerAbstract;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.poly.ReferencingPolyTypesEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.scalars.PrimitiveValuedEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.sqlos.SqlDomainObjectRepository;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.sqlos.data.SimpleClass;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.sqlos.data.SimpleClassTwo;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.sqlos.data.SqlDataClass;
import uk.co.objectconnexions.expressiveobjects.objectstore.sql.SqlObjectStore;

/**
 * @author Kevin
 * 
 */
public class SqlIntegrationTestFixtures {
    
    static SqlIntegrationTestFixtures instance;

    public static SqlIntegrationTestFixtures getInstance() {
        if (instance == null) {
            instance = new SqlIntegrationTestFixtures();
        }
        return instance;
    }

    public static void recreate() {
        instance = new SqlIntegrationTestFixtures();
    }
    
    public enum State {
        INITIALIZE,
        DONT_INITIALIZE;
        
        public boolean isInitialize() { return this == INITIALIZE; }
    }
    
    private State state = State.INITIALIZE;
    public State getState() {
        return state;
    }
    public void setState(final State state) {
        this.state = state;
    }

    
    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////
    
    
    private ExpressiveObjectsSystemWithFixtures system;
    

    // JDBC
    private Connection conn = null;
    private Statement stmt = null;

    public void initSystem(final String propertiesDirectory, final String propertiesFileName) throws Exception {

        final Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesDirectory + "/" + propertiesFileName));
        
        initSystem(properties);
    }

    public void initSystem(final Properties properties) throws Exception {
        final ExpressiveObjectsConfigurationDefault configuration = new ExpressiveObjectsConfigurationDefault();
        configuration.add(properties);

        sqlDomainObjectRepository = new SqlDomainObjectRepository();
        if (system != null) {
            system.tearDownSystem();
        }
        
        final PersistenceMechanismInstallerAbstract persistorInstaller = Utils.createPersistorInstaller(configuration);
        system = ExpressiveObjectsSystemWithFixtures.builder().with(configuration).withServices(sqlDomainObjectRepository).with(Initialization.NO_INIT).with(persistorInstaller).build();
        
        system.setUpSystem();

        registerDriverAndConnect(configuration);
    }

    
    public void shutDown() throws Exception {
        if (system != null) {
            system.tearDownSystem();
        }
    }

    
    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////
    

    @SuppressWarnings("unchecked")
    private void registerDriverAndConnect(final ExpressiveObjectsConfiguration expressiveObjectsConfiguration) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        final String jdbcClassName = expressiveObjectsConfiguration.getString(SqlObjectStore.BASE_NAME + ".jdbc.driver");
        if (jdbcClassName == null) {
            conn = null;
            stmt = null;
            return;
        }
        final Class<Driver> driverClass = (Class<Driver>) Class.forName(jdbcClassName);
        final Driver driver = driverClass.newInstance();
        DriverManager.registerDriver(driver);

        // jdbc - connect to DB and drop tables.
        conn = DriverManager.getConnection(expressiveObjectsConfiguration.getString(SqlObjectStore.BASE_NAME + ".jdbc.connection"), expressiveObjectsConfiguration.getString(SqlObjectStore.BASE_NAME + ".jdbc.user"), expressiveObjectsConfiguration.getString(SqlObjectStore.BASE_NAME + ".jdbc.password"));
        stmt = conn.createStatement();

        
        // doesn't seem to be used...
        
        // dropTable(SqlObjectStore.getTableName());
    }

    public void dropTable(final String tableName) {
        if (stmt == null) {
            if (tableName.equalsIgnoreCase("sqldataclass")) {
                final List<SqlDataClass> list = sqlDomainObjectRepository.allDataClasses();
                for (final SqlDataClass sqlDataClass : list) {
                    sqlDomainObjectRepository.delete(sqlDataClass);
                }
                return;
            } 
            if (tableName.equalsIgnoreCase("simpleclass")) {
                final List<SimpleClass> list = sqlDomainObjectRepository.allSimpleClasses();
                for (final SimpleClass sqlClass : list) {
                    sqlDomainObjectRepository.delete(sqlClass);
                }
                return;
            } 
            if (tableName.equalsIgnoreCase("simpleclasstwo")) {
                final List<SimpleClassTwo> list = sqlDomainObjectRepository.allSimpleClassTwos();
                for (final SimpleClassTwo sqlClass : list) {
                    sqlDomainObjectRepository.delete(sqlClass);
                }
                return;
            } 
            if (tableName.equalsIgnoreCase("primitivevaluedentity")) {
                final List<PrimitiveValuedEntity> list = sqlDomainObjectRepository.allPrimitiveValueEntities();
                for (final PrimitiveValuedEntity pve : list) {
                    sqlDomainObjectRepository.delete(pve);
                }
                return;
            } 
            throw new ExpressiveObjectsException("Unknown table: " + tableName);
        }
        
        try {
            String tableIdentifier = Utils.tableIdentifierFor(tableName);
            stmt.executeUpdate("DROP TABLE " + tableIdentifier);
        } catch (final SQLException e) {
            // this can happen, not a problem.
            // e.printStackTrace();
        }
    }
    
    public void sqlExecute(final String sqlString) throws SQLException {
        if (stmt != null) {
            stmt.executeUpdate(sqlString);
        }
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////

    private SqlDomainObjectRepository sqlDomainObjectRepository = null;
    
    private SqlDataClass sqlDataClass;
    private ReferencingPolyTypesEntity referencingPolyTypesEntity;

    
    public SqlDomainObjectRepository getSqlDataClassFactory() {
        return sqlDomainObjectRepository;
    }


    
    public SqlDataClass getSqlDataClass() {
        return sqlDataClass;
    }
    public void setSqlDataClass(SqlDataClass sqlDataClass) {
        this.sqlDataClass = sqlDataClass;
    }

    
    
    public ReferencingPolyTypesEntity getPolyTestClass() {
        return referencingPolyTypesEntity;
    }
    public void setPolyTestClass(final ReferencingPolyTypesEntity referencingPolyTypesEntity) {
        this.referencingPolyTypesEntity = referencingPolyTypesEntity;
    }

}
