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

package uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry;

import uk.co.objectconnexions.expressiveobjects.core.commons.components.ApplicationScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.Injectable;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.Installer;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilderAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.ObjectReflectorInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManagerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManagerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.fixtures.FixturesInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoaderInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.EmbeddedWebServerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.ExpressiveObjectsViewerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.PersistenceMechanismInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.services.ServicesInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystem;
import uk.co.objectconnexions.expressiveobjects.core.runtime.systemdependencyinjector.SystemDependencyInjector;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileStoreInstaller;

/**
 * The installers correspond more-or-less to the configurable top-level
 * components of {@link ExpressiveObjectsSystem}.
 * 
 * <p>
 * The methods of {@link InstallerRepository} may be called without
 * {@link #init() initializing} this class, but other methods may not.
 */
public interface InstallerLookup extends InstallerRepository, ApplicationScopedComponent, ExpressiveObjectsConfigurationBuilderAware, Injectable, SystemDependencyInjector {

    // /////////////////////////////////////////////////////////
    // metamodel
    // /////////////////////////////////////////////////////////

    ObjectReflectorInstaller reflectorInstaller(final String requested);

    // /////////////////////////////////////////////////////////
    // framework
    // /////////////////////////////////////////////////////////

    AuthenticationManagerInstaller authenticationManagerInstaller(String requested, final DeploymentType deploymentType);

    AuthorizationManagerInstaller authorizationManagerInstaller(String requested, final DeploymentType deploymentType);

    FixturesInstaller fixturesInstaller(String requested);

    ServicesInstaller servicesInstaller(final String requested);

    TemplateImageLoaderInstaller templateImageLoaderInstaller(String requested);

    PersistenceMechanismInstaller persistenceMechanismInstaller(final String requested, final DeploymentType deploymentType);

    UserProfileStoreInstaller userProfilePersistenceMechanismInstaller(final String requested, DeploymentType deploymentType);

    ExpressiveObjectsViewerInstaller viewerInstaller(final String requested, final String defaultName);

    ExpressiveObjectsViewerInstaller viewerInstaller(final String requested);

    EmbeddedWebServerInstaller embeddedWebServerInstaller(final String requested);

    // /////////////////////////////////////////////////////////
    // framework - generic
    // /////////////////////////////////////////////////////////

    <T extends Installer> T getInstaller(final Class<T> cls, final String requested);

    <T extends Installer> T getInstaller(final Class<T> cls);

    <T extends Installer> T getInstaller(final String implClassName);

    // /////////////////////////////////////////////////////////
    // configuration
    // /////////////////////////////////////////////////////////

    /**
     * Returns a <i>snapshot</i> of the current {@link ExpressiveObjectsConfiguration}.
     * 
     * <p>
     * The {@link ExpressiveObjectsConfiguration} could subsequently be appended to if
     * further {@link Installer}s are loaded.
     */
    ExpressiveObjectsConfiguration getConfiguration();

    // /////////////////////////////////////////////////////////
    // dependencies (injected)
    // /////////////////////////////////////////////////////////

    /**
     * Injected.
     */
    ExpressiveObjectsConfigurationBuilder getConfigurationBuilder();

}
