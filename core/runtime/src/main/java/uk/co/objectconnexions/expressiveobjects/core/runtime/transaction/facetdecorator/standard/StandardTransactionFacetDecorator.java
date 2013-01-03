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

package uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facetdecorator.standard;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.actions.invoke.ActionInvocationFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionAddToFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionClearFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionRemoveFromFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.properties.modify.PropertyClearFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.properties.modify.PropertySetterFacet;
import uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facetdecorator.TransactionFacetDecoratorAbstract;
import uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facets.ActionInvocationFacetWrapTransaction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facets.CollectionAddToFacetWrapTransaction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facets.CollectionClearFacetWrapTransaction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facets.CollectionRemoveFromFacetWrapTransaction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facets.PropertyClearFacetWrapTransaction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facets.PropertySetterFacetWrapTransaction;

public class StandardTransactionFacetDecorator extends TransactionFacetDecoratorAbstract {

    public StandardTransactionFacetDecorator(final ExpressiveObjectsConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Facet decorate(final Facet facet, final FacetHolder requiredHolder) {
        final Class<? extends Facet> facetType = facet.facetType();
        if (facetType == ActionInvocationFacet.class) {
            final ActionInvocationFacet decoratedFacet = (ActionInvocationFacet) facet;
            final Facet decoratingFacet = new ActionInvocationFacetWrapTransaction(decoratedFacet);
            requiredHolder.addFacet(decoratingFacet);
            return decoratingFacet;
        }

        if (facetType == CollectionAddToFacet.class) {
            final CollectionAddToFacet decoratedFacet = (CollectionAddToFacet) facet;
            final Facet decoratingFacet = new CollectionAddToFacetWrapTransaction(decoratedFacet);
            requiredHolder.addFacet(decoratingFacet);
            return decoratingFacet;
        }

        if (facetType == CollectionClearFacet.class) {
            final CollectionClearFacet decoratedFacet = (CollectionClearFacet) facet;
            final Facet decoratingFacet = new CollectionClearFacetWrapTransaction(decoratedFacet);
            requiredHolder.addFacet(decoratingFacet);
            return decoratingFacet;
        }

        if (facetType == CollectionRemoveFromFacet.class) {
            final CollectionRemoveFromFacet decoratedFacet = (CollectionRemoveFromFacet) facet;
            final Facet decoratingFacet = new CollectionRemoveFromFacetWrapTransaction(decoratedFacet);
            requiredHolder.addFacet(decoratingFacet);
            return decoratingFacet;
        }

        if (facetType == PropertyClearFacet.class) {
            final PropertyClearFacet decoratedFacet = (PropertyClearFacet) facet;
            final Facet decoratingFacet = new PropertyClearFacetWrapTransaction(decoratedFacet);
            requiredHolder.addFacet(decoratingFacet);
            return decoratingFacet;
        }

        if (facetType == PropertySetterFacet.class) {
            final PropertySetterFacet decoratedFacet = (PropertySetterFacet) facet;
            final Facet decoratingFacet = new PropertySetterFacetWrapTransaction(decoratedFacet);
            requiredHolder.addFacet(decoratingFacet);
            return decoratingFacet;
        }

        return facet;
    }

}
