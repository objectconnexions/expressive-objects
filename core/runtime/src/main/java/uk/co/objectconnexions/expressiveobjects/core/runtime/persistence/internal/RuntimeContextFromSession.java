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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.internal;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.ApplicationException;
import uk.co.objectconnexions.expressiveobjects.applib.bookmarks.Bookmark;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.applib.query.Query;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProviderAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.DomainObjectServices;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.DomainObjectServicesAbstract;
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
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManagerAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.TypedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContextAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjectorAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.container.query.QueryCardinality;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectInstantiationException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectInstantiator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectInstantiatorAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToManyAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.container.DomainObjectContainerObjectChanged;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.container.DomainObjectContainerResolve;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.AdapterManagerSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.MessageBroker;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.UpdateNotifier;

/**
 * Provides services to the metamodel based on the currently running
 * {@link ExpressiveObjectsSession session} (primarily the {@link PersistenceSession}).
 */
public class RuntimeContextFromSession extends RuntimeContextAbstract {

    private final AuthenticationSessionProvider authenticationSessionProvider;
    private final AdapterManager adapterManager;
    private final ObjectDirtier objectDirtier;
    private final ObjectInstantiator objectInstantiator;
    private final ObjectPersistor objectPersistor;
    private final ServicesProvider servicesProvider;
    private final ServicesInjector servicesInjector;
    private final QuerySubmitter querySubmitter;
    private final DomainObjectServices domainObjectServices;
    private final LocalizationProviderAbstract localizationProvider;

    // //////////////////////////////////////////////////////////////////
    // Constructor
    // //////////////////////////////////////////////////////////////////

    public RuntimeContextFromSession() {
        this.authenticationSessionProvider = new AuthenticationSessionProviderAbstract() {

            @Override
            public AuthenticationSession getAuthenticationSession() {
                return ExpressiveObjectsContext.getAuthenticationSession();
            }
        };
        this.adapterManager = new AdapterManager() {

            @Override
            public ObjectAdapter getAdapterFor(Oid oid) {
                return null;
            }

            @Override
            public ObjectAdapter getAdapterFor(final Object pojo) {
                return getRuntimeAdapterManager().getAdapterFor(pojo);
            }

            @Override
            public ObjectAdapter adapterFor(final Object pojo) {
                return getRuntimeAdapterManager().adapterFor(pojo);
            }

            @Override
            public ObjectAdapter adapterFor(final Object pojo, final ObjectAdapter ownerAdapter) {
                return getRuntimeAdapterManager().adapterFor(pojo, ownerAdapter);
            }

            @Override
            public ObjectAdapter adapterFor(final Object pojo, final ObjectAdapter ownerAdapter, final OneToManyAssociation collection) {
                return getRuntimeAdapterManager().adapterFor(pojo, ownerAdapter, collection);
            }

            @Override
            public ObjectAdapter adapterFor(TypedOid oid) {
            	return getRuntimeAdapterManager().adapterFor(oid);
            }

            @Override
            public ObjectAdapter adapterFor(TypedOid oid, ConcurrencyChecking concurrencyChecking) {
                return getRuntimeAdapterManager().adapterFor(oid, concurrencyChecking);
            }

            @Override
            public void injectInto(Object candidate) {
                if (AdapterManagerAware.class.isAssignableFrom(candidate.getClass())) {
                    final AdapterManagerAware cast = AdapterManagerAware.class.cast(candidate);
                    cast.setAdapterManager(this);
                }
            }


        };
        this.objectInstantiator = new ObjectInstantiatorAbstract() {

            @Override
            public Object instantiate(final Class<?> cls) throws ObjectInstantiationException {
                return getPersistenceSession().getObjectFactory().instantiate(cls);
            }
        };

        this.objectDirtier = new ObjectDirtierAbstract() {

            @Override
            public void objectChanged(final ObjectAdapter adapter) {
                getPersistenceSession().objectChanged(adapter);
            }

            @Override
            public void objectChanged(final Object object) {
                new DomainObjectContainerObjectChanged().objectChanged(object);
            }
        };
        this.objectPersistor = new ObjectPersistorAbstract() {
            @Override
            public void makePersistent(final ObjectAdapter adapter) {
                getPersistenceSession().makePersistent(adapter);
            }

            @Override
            public void remove(final ObjectAdapter adapter) {
                getUpdateNotifier().addDisposedObject(adapter);
                getPersistenceSession().destroyObject(adapter);
            }
        };
        this.servicesProvider = new ServicesProviderAbstract() {
            @Override
            public List<ObjectAdapter> getServices() {
                return getPersistenceSession().getServices();
            }
        };
        this.domainObjectServices = new DomainObjectServicesAbstract() {

            @Override
            public ObjectAdapter createTransientInstance(final ObjectSpecification spec) {
                return getPersistenceSession().createTransientInstance(spec);
            }

            @Override
            public ObjectAdapter createAggregatedInstance(final ObjectSpecification spec, final ObjectAdapter parent) {
                return getPersistenceSession().createAggregatedInstance(spec, parent);
            };

            @Override
            public Object lookup(Bookmark bookmark) {
                return new DomainObjectContainerResolve().lookup(bookmark);
            }


            @Override
            public Bookmark bookmarkFor(Object domainObject) {
                return new DomainObjectContainerResolve().bookmarkFor(domainObject);
            }

            @Override
            public void resolve(final Object parent) {
                new DomainObjectContainerResolve().resolve(parent);
            }

            @Override
            public void resolve(final Object parent, final Object field) {
                new DomainObjectContainerResolve().resolve(parent, field);
            }

            @Override
            public boolean flush() {
                return getTransactionManager().flushTransaction();
            }

            @Override
            public void commit() {
                getTransactionManager().endTransaction();
            }

            @Override
            public void informUser(final String message) {
                getMessageBroker().addMessage(message);
            }

            @Override
            public void warnUser(final String message) {
                getMessageBroker().addWarning(message);
            }

            @Override
            public void raiseError(final String message) {
                throw new ApplicationException(message);
            }

            @Override
            public String getProperty(final String name) {
                return RuntimeContextFromSession.this.getProperty(name);
            }

            @Override
            public List<String> getPropertyNames() {
                return RuntimeContextFromSession.this.getPropertyNames();
            }

        };
        this.querySubmitter = new QuerySubmitterAbstract() {

            @Override
            public <T> List<ObjectAdapter> allMatchingQuery(final Query<T> query) {
                final ObjectAdapter instances = getPersistenceSession().findInstances(query, QueryCardinality.MULTIPLE);
                return CollectionFacetUtils.convertToAdapterList(instances);
            }

            @Override
            public <T> ObjectAdapter firstMatchingQuery(final Query<T> query) {
                final ObjectAdapter instances = getPersistenceSession().findInstances(query, QueryCardinality.SINGLE);
                final List<ObjectAdapter> list = CollectionFacetUtils.convertToAdapterList(instances);
                return list.size() > 0 ? list.get(0) : null;
            }
        };
        this.servicesInjector = new ServicesInjector() {

            @Override
            public void injectServicesInto(final Object object) {
                getPersistenceSession().getServicesInjector().injectServicesInto(object);
            }

            @Override
            public void injectServicesInto(List<Object> objects) {
                getPersistenceSession().getServicesInjector().injectServicesInto(objects);
            }

            @Override
            public Object lookupService(Class<?> serviceClass) {
                return getPersistenceSession().getServicesInjector().lookupService(serviceClass);
            }

            @Override
            public void injectInto(Object candidate) {
                if (ServicesInjectorAware.class.isAssignableFrom(candidate.getClass())) {
                    final ServicesInjectorAware cast = ServicesInjectorAware.class.cast(candidate);
                    cast.setServicesInjector(this);
                }
            }
        };
        this.localizationProvider = new LocalizationProviderAbstract() {

            @Override
            public Localization getLocalization() {
                return ExpressiveObjectsContext.getLocalization();
            }
        };
    }

    // //////////////////////////////////////////////////////////////////
    // Components
    // //////////////////////////////////////////////////////////////////

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
    public DomainObjectServices getDomainObjectServices() {
        return domainObjectServices;
    }

    @Override
    public ServicesProvider getServicesProvider() {
        return servicesProvider;
    }

    @Override
    public LocalizationProviderAbstract getLocalizationProvider() {
        return localizationProvider;
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
    public ServicesInjector getDependencyInjector() {
        return servicesInjector;
    }

    @Override
    public QuerySubmitter getQuerySubmitter() {
        return querySubmitter;
    }

    // ///////////////////////////////////////////
    // Dependencies (from context)
    // ///////////////////////////////////////////

    @Override
    public DeploymentCategory getDeploymentCategory() {
        return ExpressiveObjectsContext.getDeploymentType().getDeploymentCategory();
    }

    private static PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    private static AdapterManager getRuntimeAdapterManager() {
        return getPersistenceSession().getAdapterManager();
    }

    private static UpdateNotifier getUpdateNotifier() {
        return ExpressiveObjectsContext.getUpdateNotifier();
    }

    private static ExpressiveObjectsTransactionManager getTransactionManager() {
        return getPersistenceSession().getTransactionManager();
    }

    private static MessageBroker getMessageBroker() {
        return ExpressiveObjectsContext.getMessageBroker();
    }



}
