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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext;

import uk.co.objectconnexions.expressiveobjects.applib.DomainObjectContainer;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.ApplicationScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.Injectable;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.DomainObjectServices;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.LocalizationProvider;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectDirtier;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectPersistor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.QuerySubmitter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ServicesProvider;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectInstantiator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;

/**
 * Decouples the metamodel from a runtime.
 * 
 */
public interface RuntimeContext extends Injectable, ApplicationScopedComponent {

    public DeploymentCategory getDeploymentCategory();

    /**
     * A mechanism for returning the <tt>current</tt>
     * {@link AuthenticationSession}.
     * 
     * <p>
     * Note that the scope of {@link RuntimeContext} is global, whereas
     * {@link AuthenticationSession} may change over time.
     */
    public AuthenticationSessionProvider getAuthenticationSessionProvider();

    public QuerySubmitter getQuerySubmitter();

    public AdapterManager getAdapterManager();

    public ObjectInstantiator getObjectInstantiator();

    public SpecificationLoader getSpecificationLoader();

    public ServicesProvider getServicesProvider();

    /**
     * aka the ServicesInjector...
     */
    public ServicesInjector getDependencyInjector();

    public ObjectDirtier getObjectDirtier();

    public ObjectPersistor getObjectPersistor();

    public DomainObjectServices getDomainObjectServices();

    public LocalizationProvider getLocalizationProvider();


    // ///////////////////////////////////////////
    // container
    // ///////////////////////////////////////////

    public void setContainer(DomainObjectContainer container);




}
