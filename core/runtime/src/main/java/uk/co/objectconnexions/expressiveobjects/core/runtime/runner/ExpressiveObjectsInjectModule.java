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

package uk.co.objectconnexions.expressiveobjects.core.runtime.runner;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilderDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookup;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.ExpressiveObjectsViewerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installers.InstallerLookupDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystem;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystemFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.systemusinginstallers.ExpressiveObjectsSystemThatUsesInstallersFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.viewer.ExpressiveObjectsViewer;

public class ExpressiveObjectsInjectModule extends AbstractModule {

    private final DeploymentType deploymentType;
    private final InstallerLookup installerLookup;
    private final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder;

    private final List<String> viewerNames = Lists.newArrayList();

    private static InstallerLookupDefault defaultInstallerLookup() {
        return new InstallerLookupDefault();
    }

    private static ExpressiveObjectsConfigurationBuilderDefault defaultConfigurationBuider() {
        return new ExpressiveObjectsConfigurationBuilderDefault();
    }

    public ExpressiveObjectsInjectModule(final DeploymentType deploymentType) {
        this(deploymentType, defaultConfigurationBuider(), defaultInstallerLookup());
    }

    public ExpressiveObjectsInjectModule(final DeploymentType deploymentType, final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder) {
        this(deploymentType, expressiveObjectsConfigurationBuilder, defaultInstallerLookup());
    }

    public ExpressiveObjectsInjectModule(final DeploymentType deploymentType, final InstallerLookup installerLookup) {
        this(deploymentType, defaultConfigurationBuider(), installerLookup);
    }

    public ExpressiveObjectsInjectModule(final DeploymentType deploymentType, final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder, final InstallerLookup installerLookup) {
        this.installerLookup = installerLookup;
        this.expressiveObjectsConfigurationBuilder = expressiveObjectsConfigurationBuilder;
        this.deploymentType = deploymentType;
    }

    /**
     * As passed in or defaulted by the constructors.
     */
    @SuppressWarnings("unused")
    @Provides
    @Singleton
    private DeploymentType provideDeploymentsType() {
        return deploymentType;
    }

    /**
     * As passed in or defaulted by the constructors.
     */
    @SuppressWarnings("unused")
    @Provides
    @Singleton
    private ExpressiveObjectsConfigurationBuilder providesConfigurationBuilder() {
        return expressiveObjectsConfigurationBuilder;
    }

    /**
     * As passed in or defaulted by the constructors.
     */
    @SuppressWarnings("unused")
    @Provides
    @Singleton
    @Inject
    private InstallerLookup providesInstallerLookup(final ExpressiveObjectsConfigurationBuilder configBuilder) {
        // wire up and initialize installer lookup
        configBuilder.injectInto(installerLookup);
        installerLookup.init();
        return installerLookup;
    }

    /**
     * Adjustment (as per GOOS book)
     */
    public void addViewerNames(final List<String> viewerNames) {
        this.viewerNames.addAll(viewerNames);
    }

    @Override
    protected void configure() {
        requireBinding(DeploymentType.class);
        requireBinding(ExpressiveObjectsConfigurationBuilder.class);
        requireBinding(InstallerLookup.class);
    }

    @SuppressWarnings("unused")
    @Provides
    @Inject
    @Singleton
    private ExpressiveObjectsSystemFactory provideExpressiveObjectsSystemFactory(final InstallerLookup installerLookup) {
        final ExpressiveObjectsSystemThatUsesInstallersFactory systemFactory = new ExpressiveObjectsSystemThatUsesInstallersFactory(installerLookup);
        systemFactory.init();
        return systemFactory;
    }

    @Provides
    @Inject
    @Singleton
    protected ExpressiveObjectsSystem provideExpressiveObjectsSystem(final DeploymentType deploymentType, final ExpressiveObjectsSystemFactory systemFactory) {
        final ExpressiveObjectsSystem system = systemFactory.createSystem(deploymentType);
        system.init();
        return system;
    }

    public static class ViewerList {
        private final List<ExpressiveObjectsViewer> viewers;

        public ViewerList(final List<ExpressiveObjectsViewer> viewers) {
            this.viewers = Collections.unmodifiableList(viewers);
        }

        public List<ExpressiveObjectsViewer> getViewers() {
            return viewers;
        }
    }

    @SuppressWarnings("unused")
    @Provides
    @Inject
    @Singleton
    private ViewerList lookupViewers(final InstallerLookup installerLookup, final DeploymentType deploymentType) {

        final List<String> viewersToStart = Lists.newArrayList(viewerNames);
        deploymentType.addDefaultViewer(viewersToStart);

        final List<ExpressiveObjectsViewer> viewers = Lists.newArrayList();
        for (final String requestedViewer : viewersToStart) {
            final ExpressiveObjectsViewerInstaller viewerInstaller = installerLookup.viewerInstaller(requestedViewer);
            final ExpressiveObjectsViewer viewer = viewerInstaller.createViewer();
            viewers.add(viewer);
        }
        return new ViewerList(viewers);
    }

}