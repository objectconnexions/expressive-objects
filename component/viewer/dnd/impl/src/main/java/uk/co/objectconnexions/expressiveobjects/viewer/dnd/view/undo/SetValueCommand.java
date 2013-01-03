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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.undo;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.encodeable.EncodableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.AdapterManagerSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Command;

public class SetValueCommand implements Command {
    private final String description;
    private final OneToOneAssociation value;
    private final ObjectAdapter object;
    private final String oldValue;

    public SetValueCommand(final ObjectAdapter object, final OneToOneAssociation value) {
        final EncodableFacet facet = value.getFacet(EncodableFacet.class);
        this.oldValue = facet.toEncodedString(object);
        this.object = object;
        this.value = value;

        this.description = "reset the value to " + oldValue;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void undo() {
        final EncodableFacet facet = value.getFacet(EncodableFacet.class);
        final Object obj = facet.fromEncodedString(oldValue);
        final ObjectAdapter adapter = getAdapterManager().adapterFor(obj);
        value.setAssociation(object, adapter);
        // have commented this out because it isn't needed; the transaction
        // manager will do this
        // for us on endTransaction. Still, if I'm wrong and it is needed,
        // hopefully this
        // comment will help...
        // ExpressiveObjectsContext.getObjectPersistor().objectChangedAllDirty();
    }

    @Override
    public void execute() {
    }

    @Override
    public String getName() {
        return "entry";
    }

    // //////////////////////////////////////////////////////////////////
    // Dependencies (from context)
    // //////////////////////////////////////////////////////////////////

    private static PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    private static AdapterManager getAdapterManager() {
        return getPersistenceSession().getAdapterManager();
    }

}
