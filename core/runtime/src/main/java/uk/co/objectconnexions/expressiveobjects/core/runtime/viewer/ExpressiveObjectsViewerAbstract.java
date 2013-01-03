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

package uk.co.objectconnexions.expressiveobjects.core.runtime.viewer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

import uk.co.objectconnexions.expressiveobjects.applib.fixtures.LogonFixture;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilderAware;
import uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Ensure;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequest;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequestPassword;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystem;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.SystemConstants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.systemdependencyinjector.SystemDependencyInjector;
import uk.co.objectconnexions.expressiveobjects.core.runtime.systemdependencyinjector.SystemDependencyInjectorAware;
import uk.co.objectconnexions.expressiveobjects.core.runtime.viewer.web.WebAppSpecification;

public abstract class ExpressiveObjectsViewerAbstract implements ExpressiveObjectsViewer {

    /**
     * @see {@link #setDeploymentType(DeploymentType)}
     */
    private DeploymentType deploymentType;

    private SystemDependencyInjector systemDependencyInjector;
    private ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder;

    /**
     * Optionally set, see
     * {@link #setAuthenticationRequestViaArgs(AuthenticationRequest)}
     */
    private AuthenticationRequest authenticationRequestViaArgs;

    // ////////////////////////////////////////////////////////////////
    // Settings
    // ////////////////////////////////////////////////////////////////

    @Override
    public void init() {

        ensureDependenciesInjected();

        final ExpressiveObjectsConfiguration configuration = expressiveObjectsConfigurationBuilder.getConfiguration();
        deploymentType = DeploymentType.lookup(configuration.getString(SystemConstants.DEPLOYMENT_TYPE_KEY));

        final String user = configuration.getString(SystemConstants.USER_KEY);
        final String password = configuration.getString(SystemConstants.PASSWORD_KEY);

        if (user != null) {
            authenticationRequestViaArgs = new AuthenticationRequestPassword(user, password);
        }
    }

    @Override
    public void shutdown() {
        // does nothing
    }

    // ////////////////////////////////////////////////////////////////
    // Settings
    // ////////////////////////////////////////////////////////////////

    public final DeploymentType getDeploymentType() {
        return deploymentType;
    }

    /**
     * Default implementation to return null, indicating that this viewer should
     * not be run in a web container.
     */
    @Override
    public WebAppSpecification getWebAppSpecification() {
        return null;
    }

    public AuthenticationRequest getAuthenticationRequestViaArgs() {
        return authenticationRequestViaArgs;
    }

    protected void clearAuthenticationRequestViaArgs() {
        authenticationRequestViaArgs = null;
    }

    // ////////////////////////////////////////////////////////////////
    // Post-bootstrapping
    // ////////////////////////////////////////////////////////////////

    public LogonFixture getLogonFixture() {
        return null;
    }

    // ////////////////////////////////////////////////////////////////
    // Dependencies (injected)
    // ////////////////////////////////////////////////////////////////

    protected void ensureDependenciesInjected() {
        Ensure.ensureThatState(systemDependencyInjector, is(not(nullValue())));
        Ensure.ensureThatState(expressiveObjectsConfigurationBuilder, is(not(nullValue())));
    }

    /**
     * Injected by virtue of being {@link SystemDependencyInjectorAware}.
     */
    @Override
    public void setSystemDependencyInjector(final SystemDependencyInjector dependencyInjector) {
        this.systemDependencyInjector = dependencyInjector;
    }

    protected ExpressiveObjectsConfigurationBuilder getConfigurationBuilder() {
        return expressiveObjectsConfigurationBuilder;
    }

    /**
     * Injected by virtue of being {@link ExpressiveObjectsConfigurationBuilderAware}.
     */
    @Override
    public void setConfigurationBuilder(final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder) {
        this.expressiveObjectsConfigurationBuilder = expressiveObjectsConfigurationBuilder;
    }

    // ////////////////////////////////////////////////////////////////
    // Dependencies (from context)
    // ////////////////////////////////////////////////////////////////

    /**
     * Available after {@link ExpressiveObjectsSystem} has been bootstrapped.
     */
    protected static ExpressiveObjectsConfiguration getConfiguration() {
        return ExpressiveObjectsContext.getConfiguration();
    }

    /**
     * Available after {@link ExpressiveObjectsSystem} has been bootstrapped.
     */
    public static AuthenticationManager getAuthenticationManager() {
        return ExpressiveObjectsContext.getAuthenticationManager();
    }

}
