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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProviderAware;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManagerAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContextAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjectorAware;

public abstract class ValueUsingValueSemanticsProviderFacetFactory<T> extends FacetFactoryAbstract implements ExpressiveObjectsConfigurationAware, AuthenticationSessionProviderAware, AdapterManagerAware, ServicesInjectorAware, RuntimeContextAware {

    private ExpressiveObjectsConfiguration configuration;
    private RuntimeContext runtimeContext;
    private AuthenticationSessionProvider authenticationSessionProvider;
    private AdapterManager adapterManager;
    private ServicesInjector servicesInjector;

    /**
     * Lazily created.
     */
    private ValueSemanticsProviderContext context;

    protected ValueUsingValueSemanticsProviderFacetFactory() {
        super(FeatureType.OBJECTS_ONLY);
    }

    protected void addFacets(final ValueSemanticsProviderAndFacetAbstract<T> adapter) {
        final ValueFacetUsingSemanticsProvider facet = new ValueFacetUsingSemanticsProvider(adapter, adapter, getContext());
        FacetUtil.addFacet(facet);
    }

    // ////////////////////////////////////////////////////
    // Dependencies (injected via setter)
    // ////////////////////////////////////////////////////

    /**
     * Derived from {@link #setRuntimeContext(RuntimeContext)} (since {@link RuntimeContextAware}).
     */
    private DeploymentCategory getDeploymentCategory() {
        return runtimeContext.getDeploymentCategory();
    }


    public ExpressiveObjectsConfiguration getConfiguration() {
        return configuration;
    }

    public ValueSemanticsProviderContext getContext() {
        if (context == null) {
            context = new ValueSemanticsProviderContext(getDeploymentCategory(), authenticationSessionProvider, getSpecificationLoader(), adapterManager, servicesInjector);
        }
        return context;
    }

    @Override
    public void setConfiguration(final ExpressiveObjectsConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setAuthenticationSessionProvider(final AuthenticationSessionProvider authenticationSessionProvider) {
        this.authenticationSessionProvider = authenticationSessionProvider;
    }

    @Override
    public void setAdapterManager(final AdapterManager adapterManager) {
        this.adapterManager = adapterManager;
    }

    @Override
    public void setServicesInjector(final ServicesInjector dependencyInjector) {
        this.servicesInjector = dependencyInjector;
    }

    @Override
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

}
