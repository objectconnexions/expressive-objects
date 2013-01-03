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

package uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facetdecorator;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetdecorator.FacetDecoratorAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.actions.invoke.ActionInvocationFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionAddToFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionClearFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionRemoveFromFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.properties.modify.PropertyClearFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.properties.modify.PropertySetterFacet;

public abstract class TransactionFacetDecoratorAbstract extends FacetDecoratorAbstract implements TransactionFacetDecorator {

    private final ExpressiveObjectsConfiguration configuration;

    public TransactionFacetDecoratorAbstract(final ExpressiveObjectsConfiguration configuration) {
        this.configuration = configuration;
    }

    protected ExpressiveObjectsConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Class<? extends Facet>[] getFacetTypes() {
        return new Class[] { ActionInvocationFacet.class, PropertyClearFacet.class, PropertySetterFacet.class, CollectionAddToFacet.class, CollectionRemoveFromFacet.class, CollectionClearFacet.class };
    }
}
