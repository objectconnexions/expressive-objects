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

package uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facets;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.DecoratingFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.properties.modify.PropertyClearFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.properties.modify.PropertyClearFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.TransactionalClosureAbstract;

public class PropertyClearFacetWrapTransaction extends PropertyClearFacetAbstract implements DecoratingFacet<PropertyClearFacet> {

    private final PropertyClearFacet underlyingFacet;

    @Override
    public PropertyClearFacet getDecoratedFacet() {
        return underlyingFacet;
    }

    public PropertyClearFacetWrapTransaction(final PropertyClearFacet underlyingFacet) {
        super(underlyingFacet.getFacetHolder());
        this.underlyingFacet = underlyingFacet;
    }

    @Override
    public void clearProperty(final ObjectAdapter adapter) {
        if (adapter.isTransient()) {
            // NOT !adapter.isPersistent();
            // (value adapters are neither persistent or transient)
            underlyingFacet.clearProperty(adapter);
        } else {
            getTransactionManager().executeWithinTransaction(new TransactionalClosureAbstract() {
                @Override
                public void execute() {
                    underlyingFacet.clearProperty(adapter);
                }
            });
        }
    }

    @Override
    public String toString() {
        return super.toString() + " --> " + underlyingFacet.toString();
    }

    // ///////////////////////////////////////////////////////////////
    // Dependencies (from context)
    // ///////////////////////////////////////////////////////////////

    private static ExpressiveObjectsTransactionManager getTransactionManager() {
        return getPersistenceSession().getTransactionManager();
    }

    private static PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

}
