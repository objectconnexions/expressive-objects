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

package uk.co.objectconnexions.expressiveobjects.core.runtime.system.session;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileLoader;

/**
 * As its superclass, but provides a default for some of more basic components
 * (that is, where the core framework offers only a single implementation).
 */
public class ExpressiveObjectsSessionFactoryDefault extends ExpressiveObjectsSessionFactoryAbstract {

    public ExpressiveObjectsSessionFactoryDefault(final DeploymentType deploymentType, final ExpressiveObjectsConfiguration configuration, final TemplateImageLoader templateImageLoader, final SpecificationLoaderSpi specificationLoader, final AuthenticationManager authenticationManager,
            final AuthorizationManager authorizationManager, final UserProfileLoader userProfileLoader, final PersistenceSessionFactory persistenceSessionFactory, final List<Object> servicesList, OidMarshaller oidMarshaller) {
        super(deploymentType, configuration, specificationLoader, templateImageLoader, authenticationManager, authorizationManager, userProfileLoader, persistenceSessionFactory, servicesList, oidMarshaller);
    }

}
