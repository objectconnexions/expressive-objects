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

package uk.co.objectconnexions.expressiveobjects.viewer.junit.internal;

import org.junit.internal.runners.TestClass;

import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.profilestore.InMemoryUserProfileStoreInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookup;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.systemusinginstallers.ExpressiveObjectsSystemUsingInstallers;

public class ExpressiveObjectsSystemUsingInstallersWithinJunit extends ExpressiveObjectsSystemUsingInstallers {

    private final TestClass testClass;

    public ExpressiveObjectsSystemUsingInstallersWithinJunit(final DeploymentType deploymentType, final InstallerLookup installerLookup, final TestClass testClass) {
        super(deploymentType, installerLookup);
        installerLookup.getConfigurationBuilder().add("expressive-objects.deploymentType", deploymentType.nameLowerCase());
        
        this.testClass = testClass;

        final AnnotationInstaller installer = new AnnotationInstaller();

        try {
            setAuthenticationInstaller(getInstallerLookup().injectDependenciesInto(installer.addAuthenticatorAnnotatedOn(this.testClass.getJavaClass())));

            setAuthorizationInstaller(getInstallerLookup().injectDependenciesInto(installer.addAuthorizerAnnotatedOn(this.testClass.getJavaClass())));

            setPersistenceMechanismInstaller(getInstallerLookup().injectDependenciesInto(installer.addPersistorAnnotatedOn(this.testClass.getJavaClass())));

            setUserProfileStoreInstaller(getInstallerLookup().injectDependenciesInto(new InMemoryUserProfileStoreInstaller()));

            // fixture installer
            final FixtureInstallerAnnotatedClass fixtureInstaller = new FixtureInstallerAnnotatedClass();
            fixtureInstaller.addFixturesAnnotatedOn(this.testClass.getJavaClass());
            setFixtureInstaller(fixtureInstaller);
        } catch (final InstantiationException e) {
            throw new ExpressiveObjectsException(e);
        } catch (final IllegalAccessException e) {
            throw new ExpressiveObjectsException(e);
        }

        // services installer
        final ServicesInstallerAnnotatedClass servicesInstaller = new ServicesInstallerAnnotatedClass();
        try {
            servicesInstaller.addServicesAnnotatedOn(this.testClass.getJavaClass());
        } catch (final InstantiationException e) {
            throw new ExpressiveObjectsException(e);
        } catch (final IllegalAccessException e) {
            throw new ExpressiveObjectsException(e);
        }
        setServicesInstaller(servicesInstaller);
    }

    public TestClass getTestClass() {
        return testClass;
    }

}
