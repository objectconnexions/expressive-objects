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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.noruntime;

import java.util.Collections;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.bookmarks.Bookmark;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.applib.query.Query;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProviderAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.DomainObjectServices;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.DomainObjectServicesAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.LocalizationDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.LocalizationProvider;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.LocalizationProviderAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectDirtier;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectDirtierAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectPersistor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectPersistorAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.QuerySubmitter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.QuerySubmitterAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ServicesProvider;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ServicesProviderAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManagerAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.TypedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContextAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectInstantiationException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectInstantiator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectInstantiatorAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToManyAssociation;

public class RuntimeContextNoRuntime extends RuntimeContextAbstract {

    private final DeploymentCategory deploymentCategory;
    private final ServicesInjector dependencyInjector;
    private final AuthenticationSessionProviderAbstract authenticationSessionProvider;
    private final AdapterManager adapterManager;
    private final ObjectInstantiatorAbstract objectInstantiator;
    private final ObjectDirtierAbstract objectDirtier;
    private final ObjectPersistorAbstract objectPersistor;
    private final DomainObjectServicesAbstract domainObjectServices;
    private final LocalizationProviderAbstract localizationProvider;
    private final QuerySubmitterAbstract querySubmitter;

    public RuntimeContextNoRuntime() {
        this(DeploymentCategory.PRODUCTION);
    }
    
    public RuntimeContextNoRuntime(DeploymentCategory deploymentCategory) {
        this.deploymentCategory = deploymentCategory;
        // Unlike most of the methods in this implementation, does nothing
        // (because this will always be called, even in a no-runtime context).
        dependencyInjector = new ServicesInjector() {
            @Override
            public void injectServicesInto(final Object domainObject) {
            }

            @Override
            public void injectServicesInto(List<Object> objects) {
            }

            @Override
            public Object lookupService(Class<?> serviceClass) {
                return null;
            }

            @Override
            public void injectInto(Object candidate) {
            }
        };
        authenticationSessionProvider = new AuthenticationSessionProviderAbstract() {
            @Override
            public AuthenticationSession getAuthenticationSession() {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }
        };
        adapterManager = new AdapterManagerAbstract() {

            @Override
            public ObjectAdapter getAdapterFor(final Object pojo) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public ObjectAdapter adapterFor(final Object pojo, final ObjectAdapter ownerAdapter) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public ObjectAdapter adapterFor(final Object pojo, final ObjectAdapter ownerAdapter, final OneToManyAssociation collection) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public ObjectAdapter adapterFor(final Object domainObject) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public ObjectAdapter adapterFor(TypedOid oid) {
            	throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }
            
            @Override
            public ObjectAdapter adapterFor(TypedOid oid, ConcurrencyChecking concurrencyChecking) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }
            
            @Override
            public ObjectAdapter getAdapterFor(Oid oid) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

        };
        objectInstantiator = new ObjectInstantiatorAbstract() {

            @Override
            public Object instantiate(final Class<?> cls) throws ObjectInstantiationException {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }
        };
        objectDirtier = new ObjectDirtierAbstract() {

            @Override
            public void objectChanged(final Object object) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public void objectChanged(final ObjectAdapter adapter) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }
        };
        objectPersistor = new ObjectPersistorAbstract() {

            @Override
            public void remove(final ObjectAdapter adapter) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public void makePersistent(final ObjectAdapter adapter) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }
        };
        domainObjectServices = new DomainObjectServicesAbstract() {


            @Override
            public ObjectAdapter createTransientInstance(final ObjectSpecification spec) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }
            
            @Override
            public ObjectAdapter createAggregatedInstance(final ObjectSpecification spec, final ObjectAdapter parent) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public Object lookup(Bookmark bookmark) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public Bookmark bookmarkFor(Object domainObject) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public void resolve(final Object parent, final Object field) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public void resolve(final Object parent) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public boolean flush() {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }
            
            @Override
            public void commit() {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public void informUser(final String message) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }
            
            @Override
            public void warnUser(final String message) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public void raiseError(final String message) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public List<String> getPropertyNames() {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public String getProperty(final String name) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

        };
        localizationProvider = new LocalizationProviderAbstract() {

            private final Localization defaultLocalization = new LocalizationDefault();

            @Override
            public Localization getLocalization() {
                return defaultLocalization;
            }
        };
        querySubmitter = new QuerySubmitterAbstract() {

            @Override
            public <T> ObjectAdapter firstMatchingQuery(final Query<T> query) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }

            @Override
            public <T> List<ObjectAdapter> allMatchingQuery(final Query<T> query) {
                throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
            }
        };
    }

    // ///////////////////////////////////////////
    // Components
    // ///////////////////////////////////////////

    @Override
    public DeploymentCategory getDeploymentCategory() {
        return deploymentCategory;
    }


    @Override
    public AuthenticationSessionProvider getAuthenticationSessionProvider() {
        return authenticationSessionProvider;
    }

    @Override
    public AdapterManager getAdapterManager() {
        return adapterManager;
    }

    @Override
    public ObjectInstantiator getObjectInstantiator() {
        return objectInstantiator;
    }

    @Override
    public ObjectDirtier getObjectDirtier() {
        return objectDirtier;
    }

    @Override
    public ObjectPersistor getObjectPersistor() {
        return objectPersistor;
    }

    @Override
    public DomainObjectServices getDomainObjectServices() {
        return domainObjectServices;
    }

    @Override
    public QuerySubmitter getQuerySubmitter() {
        return querySubmitter;
    }

    @Override
    public ServicesInjector getDependencyInjector() {
        return dependencyInjector;
    }

    // ///////////////////////////////////////////
    // allInstances, allMatching*
    // ///////////////////////////////////////////

    public List<ObjectAdapter> allInstances(final ObjectSpecification noSpec) {
        throw new UnsupportedOperationException("Not supported by this implementation of RuntimeContext");
    }

    // ///////////////////////////////////////////
    // getServices, injectDependenciesInto
    // ///////////////////////////////////////////

    @Override
    public ServicesProvider getServicesProvider() {
        return new ServicesProviderAbstract() {
            /**
             * Just returns an empty array.
             */
            @Override
            public List<ObjectAdapter> getServices() {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public LocalizationProvider getLocalizationProvider() {
        return localizationProvider;
    }
}
