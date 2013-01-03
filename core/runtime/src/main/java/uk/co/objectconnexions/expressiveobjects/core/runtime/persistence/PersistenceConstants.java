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
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.container.DomainObjectContainerDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor.ClassSubstitutor;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.ObjectFactory;

public final class PersistenceConstants {


    /**
     * Default implementation to use as {@link ObjectFactory}.
     */
    public static final String OBJECT_FACTORY_CLASS_NAME_DEFAULT = "uk.co.objectconnexions.expressiveobjects.core.bytecode.cglib.CglibObjectFactory";
    
    /**
     * Default implementation to use as {@link ClassSubstitutor}.
     */
    public static final String CLASS_SUBSTITUTOR_CLASS_NAME_DEFAULT = "uk.co.objectconnexions.expressiveobjects.core.bytecode.cglib.CglibClassSubstitutor";

    /**
     * Key used to lookup implementation of {@link DomainObjectContainer} in
     * {@link ExpressiveObjectsConfiguration}.
     */
    public static final String DOMAIN_OBJECT_CONTAINER_CLASS_NAME = ConfigurationConstants.ROOT + "persistor.domain-object-container";
    public static final String DOMAIN_OBJECT_CONTAINER_NAME_DEFAULT = DomainObjectContainerDefault.class.getName();

    private PersistenceConstants() {
    }

}
