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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.autocomplete;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionFilters;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionContainer.Contributed;

public abstract class AutoCompleteFacetAbstract extends FacetAbstract implements AutoCompleteFacet {

    public static Class<? extends Facet> type() {
        return AutoCompleteFacet.class;
    }

    private final Class<?> repositoryClass;
    private final String actionName;

    private final SpecificationLoader specificationLoader;
    private final AdapterManager adapterManager;
    private final ServicesInjector servicesInjector;

    /**
     * cached once searched for
     */
    private ObjectAction repositoryAction;
    private boolean cachedRepositoryAction = false;
    
    private boolean cachedRepositoryObject = false;
    private Object repository;

    public AutoCompleteFacetAbstract(FacetHolder holder, Class<?> repositoryClass, String actionName, SpecificationLoader specificationLoader, AdapterManager adapterManager, ServicesInjector servicesInjector) {
        super(type(), holder, Derivation.NOT_DERIVED);
        this.repositoryClass = repositoryClass;
        this.actionName = actionName;
        this.specificationLoader = specificationLoader;
        this.adapterManager = adapterManager;
        this.servicesInjector = servicesInjector;
    }

    @Override
    public List<ObjectAdapter> execute(final String search) {

        cacheRepositoryAndRepositoryActionIfRequired();
        if(repositoryAction == null || repository == null) {
            return Collections.<ObjectAdapter>emptyList();
        }
        
        final ObjectAdapter repositoryAdapter = adapterManager.getAdapterFor(repository);
        final ObjectAdapter searchAdapter = adapterManager.adapterFor(search);
        final ObjectAdapter resultAdapter = repositoryAction.execute(repositoryAdapter, new ObjectAdapter[] { searchAdapter} );
        // check a collection was returned
        if(CollectionFacetUtils.getCollectionFacetFromSpec(resultAdapter) == null) {
            return Collections.<ObjectAdapter>emptyList();
        }
        return CollectionFacetUtils.convertToAdapterList(resultAdapter);
    }

    @Override
    public ObjectAdapter lookup(RootOid oid) {
        return adapterManager.adapterFor(oid);
    }

    private void cacheRepositoryAndRepositoryActionIfRequired() {
        if(!cachedRepositoryAction) {
            cacheRepositoryAction();
        }
        if(!cachedRepositoryObject) {
            cacheRepositoryObject();
        }
    }

    private void cacheRepositoryAction() {
        try {
            final ObjectSpecification repositorySpec = specificationLoader.loadSpecification(repositoryClass);
            final List<ObjectAction> objectActions = repositorySpec.getObjectActions(ActionType.USER, Contributed.EXCLUDED, ObjectActionFilters.withId(actionName));

            repositoryAction = objectActions.size() == 1? objectActions.get(0): null;
        } finally {
            cachedRepositoryAction = true;
        }
    }

    private void cacheRepositoryObject() {
        repository = servicesInjector.lookupService(repositoryClass);
        cachedRepositoryObject = true;
    }

}
