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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.applib.fixtures.LogonFixture;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.Installer;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.Noop;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.Types;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.ClassSubstitutorFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MetaModelRefiner;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetdecorator.FacetDecorator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.layout.MemberLayoutArranger;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModel;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.ObjectReflectorDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor.ClassSubstitutor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistry;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistryDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.traverser.SpecificationTraverser;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.traverser.SpecificationTraverserDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidatorComposite;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.layout.dflt.MemberLayoutArrangerDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.exploration.ExplorationSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.fixtures.FixturesInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookup;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.internal.RuntimeContextFromSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystemException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystemFixturesHookAbstract;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.internal.InitialisationSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.internal.ExpressiveObjectsLocaleInitializer;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.internal.ExpressiveObjectsTimeZoneInitializer;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactoryDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileLoaderDefault;

/**
 * 
 */
public abstract class ExpressiveObjectsSystemAbstract extends ExpressiveObjectsSystemFixturesHookAbstract {

    public static final Logger LOG = Logger.getLogger(ExpressiveObjectsSystemAbstract.class);

    private FixturesInstaller fixtureInstaller;

    private LogonFixture logonFixture;

    // ///////////////////////////////////////////
    // Constructors
    // ///////////////////////////////////////////

    public ExpressiveObjectsSystemAbstract(final DeploymentType deploymentType) {
        super(deploymentType, new ExpressiveObjectsLocaleInitializer(), new ExpressiveObjectsTimeZoneInitializer());
    }

    public ExpressiveObjectsSystemAbstract(final DeploymentType deploymentType, final ExpressiveObjectsLocaleInitializer localeInitializer, final ExpressiveObjectsTimeZoneInitializer timeZoneInitializer) {
        super(deploymentType, localeInitializer, timeZoneInitializer);
    }

    @Override
    protected void installFixturesIfRequired() throws ExpressiveObjectsSystemException {
        // some deployment types (eg CLIENT) do not support installing fixtures
        // instead, any fixtures should be installed when server boots up.
        if (!getDeploymentType().canInstallFixtures()) {
            return;
        }

        fixtureInstaller = obtainFixturesInstaller();
        if (isNoop(fixtureInstaller)) {
            return;
        }

        ExpressiveObjectsContext.openSession(new InitialisationSession());
        fixtureInstaller.installFixtures();
        try {

            // only allow logon fixtures if not in production mode.
            if (!getDeploymentType().isProduction()) {
                logonFixture = fixtureInstaller.getLogonFixture();
            }
        } finally {
            ExpressiveObjectsContext.closeSession();
        }
    }

    private boolean isNoop(final FixturesInstaller candidate) {
        return candidate == null || (fixtureInstaller instanceof Noop);
    }

    // ///////////////////////////////////////////
    // Fixtures
    // ///////////////////////////////////////////

    /**
     * This is the only {@link Installer} that is used by any (all) subclass
     * implementations, because it effectively <i>is</i> the component we need
     * (as opposed to a builder/factory of the component we need).
     * 
     * <p>
     * The fact that the component <i>is</i> an installer (and therefore can be
     * {@link InstallerLookup} looked up} is at this level really just an
     * incidental implementation detail useful for the subclass that uses
     * {@link InstallerLookup} to create the other components.
     */
    protected abstract FixturesInstaller obtainFixturesInstaller() throws ExpressiveObjectsSystemException;

    // ///////////////////////////////////////////
    // Fixtures Installer
    // ///////////////////////////////////////////

    public FixturesInstaller getFixturesInstaller() {
        return fixtureInstaller;
    }

    /**
     * The {@link LogonFixture}, if any, obtained by running fixtures.
     * 
     * <p>
     * Intended to be used when for {@link DeploymentType#EXPLORATION
     * exploration} (instead of an {@link ExplorationSession}) or
     * {@link DeploymentType#PROTOTYPE prototype} deployments (saves logging
     * in). Should be <i>ignored</i> in other {@link DeploymentType}s.
     */
    @Override
    public LogonFixture getLogonFixture() {
        return logonFixture;
    }

    @Override
    protected void appendFixturesInstallerDebug(final DebugBuilder debug) {
        debug.appendln("Fixture Installer", fixtureInstaller == null ? "none" : fixtureInstaller.getClass().getName());
    }

    

    // ///////////////////////////////////////////
    // Session Factory
    // ///////////////////////////////////////////

    @Override
    public ExpressiveObjectsSessionFactory doCreateSessionFactory(final DeploymentType deploymentType) throws ExpressiveObjectsSystemException {
        final PersistenceSessionFactory persistenceSessionFactory = obtainPersistenceSessionFactory(deploymentType);
        final UserProfileLoader userProfileLoader = new UserProfileLoaderDefault(obtainUserProfileStore());
        return createSessionFactory(deploymentType, userProfileLoader, persistenceSessionFactory);
    }

    /**
     * Overloaded version designed to be called by subclasses that need to
     * explicitly specify different persistence mechanisms.
     * 
     * <p>
     * This is <i>not</i> a hook method, rather it is designed to be called
     * <i>from</i> the {@link #doCreateSessionFactory(DeploymentType) hook
     * method}.
     */
    protected final ExpressiveObjectsSessionFactory createSessionFactory(final DeploymentType deploymentType, final UserProfileLoader userProfileLoader, final PersistenceSessionFactory persistenceSessionFactory) throws ExpressiveObjectsSystemException {

        final ExpressiveObjectsConfiguration configuration = getConfiguration();
        final AuthenticationManager authenticationManager = obtainAuthenticationManager(deploymentType);
        final AuthorizationManager authorizationManager = obtainAuthorizationManager(deploymentType);
        final TemplateImageLoader templateImageLoader = obtainTemplateImageLoader();
        final OidMarshaller oidMarshaller = obtainOidMarshaller();
        
        final Collection<MetaModelRefiner> metaModelRefiners = refiners(authenticationManager, authorizationManager, templateImageLoader, persistenceSessionFactory);
        final SpecificationLoaderSpi reflector = obtainSpecificationLoaderSpi(deploymentType, persistenceSessionFactory, metaModelRefiners);

        final List<Object> servicesList = obtainServices();

        // bind metamodel to the (runtime) framework
        RuntimeContextFromSession runtimeContext = new RuntimeContextFromSession();
        runtimeContext.injectInto(reflector);

        return new ExpressiveObjectsSessionFactoryDefault(deploymentType, configuration, templateImageLoader, reflector, authenticationManager, authorizationManager, userProfileLoader, persistenceSessionFactory, servicesList, oidMarshaller);
    }


    
    private static Collection<MetaModelRefiner> refiners(Object... possibleRefiners ) {
        return Types.filtered(Arrays.asList(possibleRefiners), MetaModelRefiner.class);
    }

    
}