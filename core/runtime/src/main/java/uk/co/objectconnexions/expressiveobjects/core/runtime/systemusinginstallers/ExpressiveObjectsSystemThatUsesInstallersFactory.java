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

package uk.co.objectconnexions.expressiveobjects.core.runtime.systemusinginstallers;

import com.google.inject.Inject;

import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookup;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystem;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystemFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.systemdependencyinjector.SystemDependencyInjector;

/**
 * Implementation of {@link ExpressiveObjectsSystemFactory} that uses {@link InstallerLookup}
 * to convert the names of components into actual component instances.
 */
public class ExpressiveObjectsSystemThatUsesInstallersFactory implements ExpressiveObjectsSystemFactory {

    private final InstallerLookup installerLookup;

    // //////////////////////////////////////////////////////////
    // constructor
    // //////////////////////////////////////////////////////////

    @Inject
    public ExpressiveObjectsSystemThatUsesInstallersFactory(final InstallerLookup installerLookup) {
        this.installerLookup = installerLookup;
    }

    // //////////////////////////////////////////////////////////
    // init, shutdown
    // //////////////////////////////////////////////////////////

    @Override
    public void init() {
        // nothing to do
    }

    @Override
    public void shutdown() {
        // nothing to do
    }

    // //////////////////////////////////////////////////////////
    // main API
    // //////////////////////////////////////////////////////////

    @Override
    public ExpressiveObjectsSystem createSystem(final DeploymentType deploymentType) {

        final ExpressiveObjectsSystemUsingInstallers system = new ExpressiveObjectsSystemUsingInstallers(deploymentType, installerLookup);

        system.lookupAndSetAuthenticatorAndAuthorization(deploymentType);
        system.lookupAndSetUserProfileFactoryInstaller();
        system.lookupAndSetFixturesInstaller();
        return system;
    }

    // //////////////////////////////////////////////////////////
    // Dependencies (injected or defaulted in constructor)
    // //////////////////////////////////////////////////////////

    public SystemDependencyInjector getInstallerLookup() {
        return installerLookup;
    }

}
