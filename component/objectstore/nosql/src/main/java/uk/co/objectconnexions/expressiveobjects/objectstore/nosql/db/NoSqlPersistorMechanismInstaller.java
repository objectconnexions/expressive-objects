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

package uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.commons.factory.InstanceUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapterFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.PersistenceMechanismInstallerAbstract;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.ObjectStoreSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.AdapterManagerSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.IdentifierGenerator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.OidGenerator;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.NoSqlIdentifierGenerator;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.NoSqlObjectStore;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.encryption.DataEncryption;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.encryption.none.DataEncryptionNone;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.versions.VersionCreator;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.versions.VersionCreatorDefault;

public abstract class NoSqlPersistorMechanismInstaller extends PersistenceMechanismInstallerAbstract {

    private static final Logger LOG = Logger.getLogger(NoSqlPersistorMechanismInstaller.class);

    private static final String NAKEDOBJECTS_ENCRYPTION_CLASSES = ConfigurationConstants.ROOT + "nosql.encryption";

    private NoSqlObjectStore objectStore;

    public NoSqlPersistorMechanismInstaller(final String name) {
        super(name);
    }

    @Override
    protected ObjectStoreSpi createObjectStore(final ExpressiveObjectsConfiguration configuration, final ObjectAdapterFactory objectFactory, final AdapterManagerSpi adapterManager) {
        return getObjectStore(configuration);
    }

    @Override
    public IdentifierGenerator createIdentifierGenerator(final ExpressiveObjectsConfiguration configuration) {
        return getObjectStore(configuration).getIdentifierGenerator();
    }

    private NoSqlObjectStore getObjectStore(final ExpressiveObjectsConfiguration configuration) {
        if (objectStore == null) {
            //final KeyCreatorDefault keyCreator = createKeyCreator();
            final VersionCreator versionCreator = createVersionCreator();
            final NoSqlDataDatabase db = createNoSqlDatabase(configuration);
            final OidGenerator oidGenerator = new OidGenerator(createIdentifierGenerator(db));

            final Map<String, DataEncryption> availableDataEncryption = new HashMap<String, DataEncryption>();
            try {
                final String[] encryptionClasses = getConfiguration().getList(NAKEDOBJECTS_ENCRYPTION_CLASSES);
                DataEncryption writeWithEncryption = null;
                boolean encryptionSpecified = false;
                for (final String fullyQualifiedClass : encryptionClasses) {
                    LOG.info("  adding encryption " + fullyQualifiedClass);
                    final DataEncryption encryption = (DataEncryption) InstanceUtil.createInstance(fullyQualifiedClass);
                    encryption.init(configuration);
                    availableDataEncryption.put(encryption.getType(), encryption);
                    if (!encryptionSpecified) {
                        writeWithEncryption = encryption;
                    }
                    encryptionSpecified = true;
                }
                if (!encryptionSpecified) {
                    LOG.warn("No encryption specified");
                    final DataEncryption encryption = new DataEncryptionNone();
                    availableDataEncryption.put(encryption.getType(), encryption);
                    writeWithEncryption = encryption;
                }
                objectStore = new NoSqlObjectStore(db, oidGenerator, versionCreator, writeWithEncryption, availableDataEncryption);
            } catch (final IllegalArgumentException e) {
                throw new ExpressiveObjectsException(e);
            } catch (final SecurityException e) {
                throw new ExpressiveObjectsException(e);
            }
        }
        return objectStore;
    }

    protected NoSqlIdentifierGenerator createIdentifierGenerator(final NoSqlDataDatabase database) {
        return new NoSqlIdentifierGenerator(database);
    }

    protected abstract NoSqlDataDatabase createNoSqlDatabase(ExpressiveObjectsConfiguration configuration);

    private VersionCreator createVersionCreator() {
        return new VersionCreatorDefault();
    }
}
