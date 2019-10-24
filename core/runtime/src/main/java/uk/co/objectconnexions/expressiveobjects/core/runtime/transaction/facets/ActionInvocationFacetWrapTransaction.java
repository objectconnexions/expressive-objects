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

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.DecoratingFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.actions.invoke.ActionInvocationFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.actions.invoke.ActionInvocationFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.TransactionalClosureWithReturnAbstract;

public class ActionInvocationFacetWrapTransaction extends ActionInvocationFacetAbstract implements DecoratingFacet<ActionInvocationFacet> {

    private final static Logger LOG = Logger.getLogger(ActionInvocationFacetWrapTransaction.class);

    private final ActionInvocationFacet underlyingFacet;

    @Override
    public ActionInvocationFacet getDecoratedFacet() {
        return underlyingFacet;
    }

    public ActionInvocationFacetWrapTransaction(final ActionInvocationFacet underlyingFacet) {
        super(underlyingFacet.getFacetHolder());
        this.underlyingFacet = underlyingFacet;
    }

    @Override
    public ObjectAdapter invoke(final ObjectAdapter targetAdapter, final ObjectAdapter[] parameterAdapters) {
        return getTransactionManager().executeWithinTransaction(new TransactionalClosureWithReturnAbstract<ObjectAdapter>() {
            @Override
            public ObjectAdapter execute() {
                return underlyingFacet.invoke(targetAdapter, parameterAdapters);
            }
        });
    }

    @Override
    public ObjectSpecification getReturnType() {
        return underlyingFacet.getReturnType();
    }

    @Override
    public ObjectSpecification getOnType() {
        return underlyingFacet.getOnType();
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
