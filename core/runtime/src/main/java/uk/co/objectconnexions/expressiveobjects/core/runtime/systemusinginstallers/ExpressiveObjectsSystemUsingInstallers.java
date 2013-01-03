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

import static uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Ensure.ensureThatArg;
import static uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Ensure.ensureThatState;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.ClassSubstitutorFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MetaModelRefiner;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.ObjectReflectorInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManagerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManagerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.fixtures.FixturesInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoaderInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookup;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.PersistenceMechanismInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.services.ServicesInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystemException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.SystemConstants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.systemdependencyinjector.SystemDependencyInjector;
import uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facetdecorator.standard.TransactionFacetDecoratorInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileStore;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileStoreInstaller;

public class ExpressiveObjectsSystemUsingInstallers extends ExpressiveObjectsSystemAbstract {

    public static final Logger LOG = Logger.getLogger(ExpressiveObjectsSystemUsingInstallers.class);

    private final InstallerLookup installerLookup;

    private AuthenticationManagerInstaller authenticationInstaller;
    private AuthorizationManagerInstaller authorizationInstaller;
    private ObjectReflectorInstaller reflectorInstaller;
    private ServicesInstaller servicesInstaller;
    private UserProfileStoreInstaller userProfileStoreInstaller;
    private PersistenceMechanismInstaller persistenceMechanismInstaller;
    private FixturesInstaller fixtureInstaller;

    // ///////////////////////////////////////////
    // Constructors
    // ///////////////////////////////////////////

    public ExpressiveObjectsSystemUsingInstallers(final DeploymentType deploymentType, final InstallerLookup installerLookup) {
        super(deploymentType);
        ensureThatArg(installerLookup, is(not(nullValue())));
        this.installerLookup = installerLookup;
    }

    // ///////////////////////////////////////////
    // InstallerLookup
    // ///////////////////////////////////////////

    /**
     * As per
     * {@link #ExpressiveObjectsSystemUsingInstallers(DeploymentType, InstallerLookup)
     * constructor}.
     */
    public SystemDependencyInjector getInstallerLookup() {
        return installerLookup;
    }

    // ///////////////////////////////////////////
    // Create context hooks
    // ///////////////////////////////////////////


    // ///////////////////////////////////////////
    // Configuration
    // ///////////////////////////////////////////

    /**
     * Returns a <i>snapshot</i> of the {@link ExpressiveObjectsConfiguration configuration}
     * held by the {@link #getInstallerLookup() installer lookup}.
     * 
     * @see InstallerLookup#getConfiguration()
     */
    @Override
    public ExpressiveObjectsConfiguration getConfiguration() {
        return installerLookup.getConfiguration();
    }

    // ///////////////////////////////////////////
    // Authentication & Authorization
    // ///////////////////////////////////////////

    public void lookupAndSetAuthenticatorAndAuthorization(final DeploymentType deploymentType) {

        //final ExpressiveObjectsConfiguration configuration = installerLookup.getConfiguration();

        // use the one specified in configuration
        final String authenticationManagerKey = getConfiguration().getString(SystemConstants.AUTHENTICATION_INSTALLER_KEY);
        final AuthenticationManagerInstaller authenticationInstaller = installerLookup.authenticationManagerInstaller(authenticationManagerKey, deploymentType);
        if (authenticationInstaller != null) {
            setAuthenticationInstaller(authenticationInstaller);
        }
        
        // use the one specified in configuration
        final String authorizationManagerKey = getConfiguration().getString(SystemConstants.AUTHORIZATION_INSTALLER_KEY);
        final AuthorizationManagerInstaller authorizationInstaller = installerLookup.authorizationManagerInstaller(authorizationManagerKey, deploymentType);
        if (authorizationInstaller != null) {
            setAuthorizationInstaller(authorizationInstaller);
        }
    }

    public void setAuthenticationInstaller(final AuthenticationManagerInstaller authenticationManagerInstaller) {
        this.authenticationInstaller = authenticationManagerInstaller;
    }

    public void setAuthorizationInstaller(final AuthorizationManagerInstaller authorizationManagerInstaller) {
        this.authorizationInstaller = authorizationManagerInstaller;
    }

    @Override
    protected AuthenticationManager obtainAuthenticationManager(final DeploymentType deploymentType) {
        return authenticationInstaller.createAuthenticationManager();
    }

    @Override
    protected AuthorizationManager obtainAuthorizationManager(final DeploymentType deploymentType) {
        return authorizationInstaller.createAuthorizationManager();
    }

    // ///////////////////////////////////////////
    // Fixtures
    // ///////////////////////////////////////////

    public void lookupAndSetFixturesInstaller() {
        final ExpressiveObjectsConfiguration configuration = installerLookup.getConfiguration();
        final String fixture = configuration.getString(SystemConstants.FIXTURES_INSTALLER_KEY);

        final FixturesInstaller fixturesInstaller = installerLookup.fixturesInstaller(fixture);
        if (fixturesInstaller != null) {
            this.fixtureInstaller = fixturesInstaller;
        }
    }

    public void setFixtureInstaller(final FixturesInstaller fixtureInstaller) {
        this.fixtureInstaller = fixtureInstaller;
    }

    @Override
    protected FixturesInstaller obtainFixturesInstaller() throws ExpressiveObjectsSystemException {
        return fixtureInstaller;
    }

    // ///////////////////////////////////////////
    // Template Image Loader
    // ///////////////////////////////////////////

    /**
     * Uses the {@link TemplateImageLoader} configured in
     * {@link InstallerLookup}, if available, else falls back to that of the
     * superclass.
     */
    @Override
    protected TemplateImageLoader obtainTemplateImageLoader() {
        final TemplateImageLoaderInstaller templateImageLoaderInstaller = installerLookup.templateImageLoaderInstaller(null);
        if (templateImageLoaderInstaller != null) {
            return templateImageLoaderInstaller.createLoader();
        } else {
            return super.obtainTemplateImageLoader();
        }
    }

    // ///////////////////////////////////////////
    // Reflector
    // ///////////////////////////////////////////

    public void setReflectorInstaller(final ObjectReflectorInstaller reflectorInstaller) {
        this.reflectorInstaller = reflectorInstaller;
    }

    @Override
    protected SpecificationLoaderSpi obtainSpecificationLoaderSpi(final DeploymentType deploymentType, final ClassSubstitutorFactory classSubstitutorFactory, final Collection<MetaModelRefiner> metaModelRefiners) throws ExpressiveObjectsSystemException {
        if (reflectorInstaller == null) {
            final String fromCmdLine = getConfiguration().getString(SystemConstants.REFLECTOR_KEY);
            reflectorInstaller = installerLookup.reflectorInstaller(fromCmdLine);
        }
        ensureThatState(reflectorInstaller, is(not(nullValue())), "reflector installer has not been injected and could not be looked up");

        // add in transaction support (if already in set then will be ignored)
        reflectorInstaller.addFacetDecoratorInstaller(installerLookup.getInstaller(TransactionFacetDecoratorInstaller.class));

        return reflectorInstaller.createReflector(classSubstitutorFactory, metaModelRefiners);
    }

    // ///////////////////////////////////////////
    // Services
    // ///////////////////////////////////////////

    public void setServicesInstaller(final ServicesInstaller servicesInstaller) {
        this.servicesInstaller = servicesInstaller;
    }

    @Override
    protected List<Object> obtainServices() {
        if (servicesInstaller == null) {
            servicesInstaller = installerLookup.servicesInstaller(null);
        }
        ensureThatState(servicesInstaller, is(not(nullValue())), "services installer has not been injected and could not be looked up");

        return servicesInstaller.getServices(getDeploymentType());
    }

    // ///////////////////////////////////////////
    // User Profile Loader/Store
    // ///////////////////////////////////////////

    public void lookupAndSetUserProfileFactoryInstaller() {
        final ExpressiveObjectsConfiguration configuration = installerLookup.getConfiguration();
        final String persistor = configuration.getString(SystemConstants.PROFILE_PERSISTOR_INSTALLER_KEY);

        final UserProfileStoreInstaller userProfilePersistenceMechanismInstaller = installerLookup.userProfilePersistenceMechanismInstaller(persistor, getDeploymentType());
        if (userProfilePersistenceMechanismInstaller != null) {
            setUserProfileStoreInstaller(userProfilePersistenceMechanismInstaller);
        }
    }

    public void setUserProfileStoreInstaller(final UserProfileStoreInstaller userProfilestoreInstaller) {
        this.userProfileStoreInstaller = userProfilestoreInstaller;
    }

    @Override
    protected UserProfileStore obtainUserProfileStore() {
        return userProfileStoreInstaller.createUserProfileStore(getConfiguration());
    }

    // ///////////////////////////////////////////
    // PersistenceSessionFactory
    // ///////////////////////////////////////////

    public void setPersistenceMechanismInstaller(final PersistenceMechanismInstaller persistenceMechanismInstaller) {
        this.persistenceMechanismInstaller = persistenceMechanismInstaller;
    }

    @Override
    protected PersistenceSessionFactory obtainPersistenceSessionFactory(final DeploymentType deploymentType) throws ExpressiveObjectsSystemException {

        // look for a object store persistor
        if (persistenceMechanismInstaller == null) {
            final String persistenceMechanism = getConfiguration().getString(SystemConstants.OBJECT_PERSISTOR_INSTALLER_KEY);
            persistenceMechanismInstaller = installerLookup.persistenceMechanismInstaller(persistenceMechanism, deploymentType);
        }

        ensureThatState(persistenceMechanismInstaller, is(not(nullValue())), "persistor installer has not been injected and could not be looked up");
        return persistenceMechanismInstaller.createPersistenceSessionFactory(deploymentType);
    }
}
