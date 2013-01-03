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

package uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.file;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModel;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidatorComposite;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.NoSqlDataDatabase;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.NoSqlPersistorMechanismInstaller;

public class FileServerPersistorMechanismInstaller extends NoSqlPersistorMechanismInstaller {

    private static final String ROOT = ConfigurationConstants.ROOT + "nosql.fileserver.";
    private static final String DB_HOST = ROOT + "host";
    private static final String DB_PORT = ROOT + "port";
    private static final String DB_TIMEMOUT = ROOT + "timeout";

    public FileServerPersistorMechanismInstaller() {
        super("fileserver");
    }

    @Override
    protected NoSqlDataDatabase createNoSqlDatabase(final ExpressiveObjectsConfiguration configuration) {
        NoSqlDataDatabase db;
        final String host = configuration.getString(DB_HOST, "localhost");
        final int port = configuration.getInteger(DB_PORT, 0);
        final int timeout = configuration.getInteger(DB_TIMEMOUT, 5000);
        db = new FileServerDb(host, port, timeout);
        return db;
    }


}
