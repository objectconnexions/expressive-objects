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

package uk.co.objectconnexions.expressiveobjects.viewer.html.context;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Assert;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.Version;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.AdapterManagerSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;

public class PersistentRootAdapterMapping extends RootAdapterMappingAbstract {
    
    private static final long serialVersionUID = 1L;
    
    private Version version;

    public PersistentRootAdapterMapping(final ObjectAdapter adapter) {
        super(adapter);
        Assert.assertFalse("OID is for transient", adapter.getOid().isTransient());
        Assert.assertFalse("adapter is for transient", adapter.isTransient());
    }


    // /////////////////////////////////////////////////////
    // version
    // /////////////////////////////////////////////////////

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public void checkVersion(final ObjectAdapter object) {
        object.checkLock(getVersion());
    }

    @Override
    public void updateVersion() {
        final ObjectAdapter adapter = getAdapterManager().getAdapterFor(getOid());
        version = adapter.getVersion();
    }


    // /////////////////////////////////////////////////////
    // restoreToLoader
    // /////////////////////////////////////////////////////

    @Override
    public void restoreToLoader() {
        final RootOidDefault oid = RootOidDefault.deString(getOidStr(), getOidMarshaller());
        final ObjectAdapter adapter = getAdapterManager().adapterFor(oid);
        adapter.setVersion(getVersion());
    }


    // /////////////////////////////////////////////////////
    // value semantics
    // /////////////////////////////////////////////////////

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof PersistentRootAdapterMapping)) {
            return false;
        } 
        return ((PersistentRootAdapterMapping) obj).getOidStr().equals(getOidStr());
    }


    // /////////////////////////////////////////////////////
    // debugging, toString
    // /////////////////////////////////////////////////////

    @Override
    public void debugData(final DebugBuilder debug) {
        debug.appendln(getOidStr());
        if (version != null) {
            debug.appendln(version.toString());
        }
    }

    @Override
    public String toString() {
        return getOidStr() + " : " + version;
    }

    // /////////////////////////////////////////////////////
    // Dependencies (from context)
    // /////////////////////////////////////////////////////

    protected PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    protected AdapterManager getAdapterManager() {
        return getPersistenceSession().getAdapterManager();
    }
    
}
