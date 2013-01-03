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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.PropertyOrCollectionIdentifyingFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistry;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistryAware;

public abstract class PropertyOrCollectionIdentifyingFacetFactoryAbstract extends MethodPrefixBasedFacetFactoryAbstract implements PropertyOrCollectionIdentifyingFacetFactory, CollectionTypeRegistryAware {

    private CollectionTypeRegistry collectionTypeRegistry;

    public PropertyOrCollectionIdentifyingFacetFactoryAbstract(final List<FeatureType> featureTypes, final String... prefixes) {
        super(featureTypes, OrphanValidation.DONT_VALIDATE, prefixes);
    }

    protected boolean isCollectionOrArray(final Class<?> cls) {
        return getCollectionTypeRepository().isCollectionType(cls) || getCollectionTypeRepository().isArrayType(cls);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Injected: CollectionTypeRegistry
    // /////////////////////////////////////////////////////////////////////////

    protected CollectionTypeRegistry getCollectionTypeRepository() {
        return collectionTypeRegistry;
    }

    /**
     * Injected so can propogate to any {@link #registerFactory(FacetFactory)
     * registered} {@link FacetFactory} s that are also
     * {@link CollectionTypeRegistryAware}.
     */
    @Override
    public void setCollectionTypeRegistry(final CollectionTypeRegistry collectionTypeRegistry) {
        this.collectionTypeRegistry = collectionTypeRegistry;
    }

}
