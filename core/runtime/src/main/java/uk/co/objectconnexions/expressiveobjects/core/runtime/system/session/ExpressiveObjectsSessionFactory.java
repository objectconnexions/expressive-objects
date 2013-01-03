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

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.ApplicationScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileLoader;

/**
 * Analogous to a Hibernate <tt>SessionFactory</tt>.
 * 
 * @see ExpressiveObjectsSession
 */
public interface ExpressiveObjectsSessionFactory extends ApplicationScopedComponent {

    /**
     * Creates and {@link ExpressiveObjectsSession#open() open}s the {@link ExpressiveObjectsSession}.
     */
    ExpressiveObjectsSession openSession(final AuthenticationSession session);

    /**
     * The {@link ApplicationScopedComponent application-scoped}
     * {@link DeploymentType}.
     */
    public DeploymentType getDeploymentType();

    /**
     * The {@link ApplicationScopedComponent application-scoped}
     * {@link ExpressiveObjectsConfiguration}.
     */
    public ExpressiveObjectsConfiguration getConfiguration();

    /**
     * The {@link ApplicationScopedComponent application-scoped}
     * {@link SpecificationLoaderSpi}.
     */
    public SpecificationLoaderSpi getSpecificationLoader();

    /**
     * The {@link ApplicationScopedComponent application-scoped}
     * {@link TemplateImageLoader}.
     */
    public TemplateImageLoader getTemplateImageLoader();

    /**
     * The {@link AuthenticationManager} that will be used to authenticate and
     * create {@link AuthenticationSession}s
     * {@link ExpressiveObjectsSession#getAuthenticationSession() within} the
     * {@link ExpressiveObjectsSession}.
     */
    public AuthenticationManager getAuthenticationManager();

    /**
     * The {@link AuthorizationManager} that will be used to authorize access to
     * domain objects.
     */
    public AuthorizationManager getAuthorizationManager();

    /**
     * The {@link PersistenceSessionFactory} that will be used to create
     * {@link PersistenceSession} {@link ExpressiveObjectsSession#getPersistenceSession()
     * within} the {@link ExpressiveObjectsSession}.
     */
    public PersistenceSessionFactory getPersistenceSessionFactory();

    public UserProfileLoader getUserProfileLoader();

    public List<Object> getServices();

    /**
     * The {@link OidMarshaller} to use for marshalling and unmarshalling {@link Oid}s
     * into strings.
     */
	public OidMarshaller getOidMarshaller();

}
