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

package uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.commands;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.ObjectPersistenceException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.CreateObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommandContext;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.DataManager;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.ObjectData;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.version.FileVersion;

public final class XmlCreateObjectCommand extends AbstractXmlPersistenceCommand implements CreateObjectCommand {
    private static final Logger LOG = Logger.getLogger(XmlCreateObjectCommand.class);

    public XmlCreateObjectCommand(final ObjectAdapter adapter, final DataManager dataManager) {
        super(adapter, dataManager);
    }

    @Override
    public void execute(final PersistenceCommandContext context) throws ObjectPersistenceException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("  create object " + onAdapter());
        }
        final String user = getAuthenticationSession().getUserName();
        onAdapter().setVersion(FileVersion.create(user));
        final ObjectData data = createObjectData(onAdapter(), true);
        getDataManager().insertObject(data);
    }

    @Override
    public String toString() {
        return "CreateObjectCommand [object=" + onAdapter() + "]";
    }
}