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

package uk.co.objectconnexions.expressiveobjects.core.objectstore;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.objectstore.internal.ObjectStoreInstances;
import uk.co.objectconnexions.expressiveobjects.core.objectstore.internal.ObjectStorePersistedObjects;
import uk.co.objectconnexions.expressiveobjects.core.objectstore.internal.ObjectStorePersistedObjectsDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.PersistenceSessionFactoryDelegate;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.PersistenceSessionFactoryDelegating;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.IdentifierGenerator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.IdentifierGeneratorDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.OidGenerator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;

public class InMemoryPersistenceSessionFactory extends PersistenceSessionFactoryDelegating {

    private ObjectStorePersistedObjects persistedObjects;

    public InMemoryPersistenceSessionFactory(final DeploymentType deploymentType, final ExpressiveObjectsConfiguration configuration, final PersistenceSessionFactoryDelegate persistenceSessionFactoryDelegate) {
        super(deploymentType, configuration, persistenceSessionFactoryDelegate);
    }

    ObjectStorePersistedObjects getPersistedObjects() {
        return persistedObjects;
    }

    @Override
    public PersistenceSession createPersistenceSession() {
        final PersistenceSession persistenceSession = super.createPersistenceSession();
        if (persistedObjects != null) {
            final OidGenerator oidGenerator = persistenceSession.getOidGenerator();
            final IdentifierGenerator identifierGenerator = oidGenerator.getIdentifierGenerator();
            if (identifierGenerator instanceof IdentifierGeneratorDefault) {
                final IdentifierGeneratorDefault identifierGeneratorDefault = (IdentifierGeneratorDefault) identifierGenerator;
                identifierGeneratorDefault.resetTo(persistedObjects.getOidGeneratorMemento());
            }
        }

        return persistenceSession;
    }

    /**
     * Not API - called when {@link InMemoryObjectStore} first
     * {@link InMemoryObjectStore#open() open}ed.
     */
    public ObjectStorePersistedObjects createPersistedObjects() {
        return new ObjectStorePersistedObjectsDefault();
    }

    /**
     * Not API - called when {@link InMemoryObjectStore} is
     * {@link InMemoryObjectStore#close() close}d.
     */
    public void attach(final PersistenceSession persistenceSession, final ObjectStorePersistedObjects persistedObjects) {
        final OidGenerator oidGenerator = persistenceSession.getOidGenerator();
        final IdentifierGenerator identifierGenerator = oidGenerator.getIdentifierGenerator();
        if (identifierGenerator instanceof IdentifierGeneratorDefault) {
            final IdentifierGeneratorDefault identifierGeneratorDefault = (IdentifierGeneratorDefault) identifierGenerator;
            persistedObjects.saveOidGeneratorMemento(identifierGeneratorDefault.getMemento());
        }
        this.persistedObjects = persistedObjects;
    }

    @Override
    protected void doShutdown() {
        if (persistedObjects != null) {
            for (final ObjectStoreInstances inst : persistedObjects.instances()) {
                inst.shutdown();
            }
            persistedObjects.clear();
        }
    }

}
