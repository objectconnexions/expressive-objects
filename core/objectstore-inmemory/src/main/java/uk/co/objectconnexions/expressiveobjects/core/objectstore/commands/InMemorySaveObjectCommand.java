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

package uk.co.objectconnexions.expressiveobjects.core.objectstore.commands;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.objectstore.internal.ObjectStorePersistedObjects;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.ObjectPersistenceException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommandContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.SaveObjectCommand;

public final class InMemorySaveObjectCommand extends AbstractInMemoryPersistenceCommand implements SaveObjectCommand {

    @SuppressWarnings("unused")
    private final static Logger LOG = Logger.getLogger(InMemorySaveObjectCommand.class);

    public InMemorySaveObjectCommand(final ObjectAdapter object, final ObjectStorePersistedObjects persistedObjects) {
        super(object, persistedObjects);
    }

    @Override
    public void execute(final PersistenceCommandContext context) throws ObjectPersistenceException {
        save(onAdapter());
    }

    @Override
    public String toString() {
        return "SaveObjectCommand [object=" + onAdapter() + "]";
    }
}