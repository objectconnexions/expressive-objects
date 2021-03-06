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
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionAddToFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionAddToFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.TransactionalClosureAbstract;

public class CollectionAddToFacetWrapTransaction extends CollectionAddToFacetAbstract implements DecoratingFacet<CollectionAddToFacet> {

    private final CollectionAddToFacet underlyingFacet;

    @Override
    public CollectionAddToFacet getDecoratedFacet() {
        return underlyingFacet;
    }

    public CollectionAddToFacetWrapTransaction(final CollectionAddToFacet underlyingFacet) {
        super(underlyingFacet.getFacetHolder());
        this.underlyingFacet = underlyingFacet;
    }

    @Override
    public void add(final ObjectAdapter adapter, final ObjectAdapter referencedAdapter) {
        if (adapter.isTransient()) {
            // NOT !adapter.isPersistent();
            // (value adapters are neither persistent or transient)
            underlyingFacet.add(adapter, referencedAdapter);
        } else {
            getTransactionManager().executeWithinTransaction(new TransactionalClosureAbstract() {
                @Override
                public void execute() {
                    underlyingFacet.add(adapter, referencedAdapter);
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
