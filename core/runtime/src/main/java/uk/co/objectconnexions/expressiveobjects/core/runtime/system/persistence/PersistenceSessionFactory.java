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

package uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.components.ApplicationScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapterFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.ClassSubstitutorFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MetaModelRefiner;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.ServicesInjectorSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.PersistenceSessionFactoryDelegate;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adaptermanager.PojoRecreator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;

/**
 * @see PersistenceSessionFactoryDelegate
 */
public interface PersistenceSessionFactory extends ClassSubstitutorFactory, MetaModelRefiner, ApplicationScopedComponent {

    DeploymentType getDeploymentType();


    // //////////////////////////////////////////////////////
    // Singleton threadsafe components
    // //////////////////////////////////////////////////////

    ObjectAdapterFactory getAdapterFactory();
    ObjectFactory getObjectFactory();
    PojoRecreator getPojoRecreator();
    IdentifierGenerator getIdentifierGenerator();
    ServicesInjectorSpi getServicesInjector();

    
    // //////////////////////////////////////////////////////
    // main API
    // //////////////////////////////////////////////////////

    /**
     * Creates a {@link PersistenceSession} with the implementing object as the
     * {@link PersistenceSession}'s
     * {@link PersistenceSession#getPersistenceSessionFactory() owning factory}.
     */
    PersistenceSession createPersistenceSession();

    // //////////////////////////////////////////////////////
    // Services
    // //////////////////////////////////////////////////////

    public void setServices(List<Object> servicesList);

    public List<Object> getServices();





}
