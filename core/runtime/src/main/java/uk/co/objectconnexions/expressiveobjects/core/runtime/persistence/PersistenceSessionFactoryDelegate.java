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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence;

import uk.co.objectconnexions.expressiveobjects.applib.DomainObjectContainer;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilderAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapterFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.ClassSubstitutorFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MetaModelRefiner;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.ServicesInjectorSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adaptermanager.PojoRecreator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.IdentifierGenerator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.ObjectFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;

/**
 * Creates a {@link PersistenceSession} on behalf of a
 * {@link PersistenceSessionFactory}.
 */
public interface PersistenceSessionFactoryDelegate extends ExpressiveObjectsConfigurationBuilderAware, ClassSubstitutorFactory, MetaModelRefiner {


    ///////////////////////////////////////////////////////////////////////////
    // singleton threadsafe components created during init
    ///////////////////////////////////////////////////////////////////////////
    
    PojoRecreator createPojoRecreator(ExpressiveObjectsConfiguration configuration);

    ObjectAdapterFactory createAdapterFactory(ExpressiveObjectsConfiguration configuration);

    ObjectFactory createObjectFactory(ExpressiveObjectsConfiguration configuration);

    IdentifierGenerator createIdentifierGenerator(ExpressiveObjectsConfiguration configuration);

    ServicesInjectorSpi createServicesInjector(ExpressiveObjectsConfiguration configuration);

    DomainObjectContainer createContainer(ExpressiveObjectsConfiguration configuration);

    RuntimeContext createRuntimeContext(ExpressiveObjectsConfiguration configuration);

    
    ///////////////////////////////////////////////////////////////////////////
    // created for each session
    ///////////////////////////////////////////////////////////////////////////

    /**
     * As per {@link PersistenceSessionFactory#createPersistenceSession()}, but
     * passing a {@link PersistenceSessionFactory} to act as the
     * {@link PersistenceSession}'s
     * {@link PersistenceSession#getPersistenceSessionFactory() owning factory}.
     */
    PersistenceSession createPersistenceSession(PersistenceSessionFactory persistenceSessionFactory);

    
    
}
