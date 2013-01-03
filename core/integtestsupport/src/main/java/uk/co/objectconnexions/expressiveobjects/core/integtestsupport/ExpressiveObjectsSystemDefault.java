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
package uk.co.objectconnexions.expressiveobjects.core.integtestsupport;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationDefault;
import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceContextLoaderClassPath;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.ClassSubstitutorFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MetaModelRefiner;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetdecorator.FacetDecorator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModel;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistry;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistryDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidator;
import uk.co.objectconnexions.expressiveobjects.core.objectstore.InMemoryPersistenceMechanismInstaller;
import uk.co.objectconnexions.expressiveobjects.core.profilestore.InMemoryUserProfileStore;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.metamodelvalidator.dflt.MetaModelValidatorDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard.AuthenticationManagerStandard;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard.Authenticator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.standard.AuthorizationManagerStandard;
import uk.co.objectconnexions.expressiveobjects.core.runtime.fixtures.FixturesInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.fixtures.FixturesInstallerFromConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.PersistenceMechanismInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.services.ServicesInstallerFromConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystemException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.systemusinginstallers.ExpressiveObjectsSystemAbstract;
import uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facetdecorator.standard.StandardTransactionFacetDecorator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileStore;
import uk.co.objectconnexions.expressiveobjects.core.security.authentication.AuthenticatorBypass;
import uk.co.objectconnexions.expressiveobjects.progmodels.dflt.JavaReflectorHelper;
import uk.co.objectconnexions.expressiveobjects.progmodels.dflt.ProgrammingModelFacetsJava5;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ExpressiveObjectsSystemDefault extends ExpressiveObjectsSystemAbstract {

    private final ExpressiveObjectsConfigurationDefault configuration;
    private final List<Object> servicesIfAny;

    public ExpressiveObjectsSystemDefault(Object... servicesIfAny) {
        this(DeploymentType.SERVER, servicesIfAny);
    }

    public ExpressiveObjectsSystemDefault(List<Object> services) {
        this(DeploymentType.SERVER, services);
    }

    public ExpressiveObjectsSystemDefault(DeploymentType deploymentType, Object... servicesIfAny) {
        this(deploymentType, asList(servicesIfAny));
    }

    public ExpressiveObjectsSystemDefault(DeploymentType deploymentType, List<Object> services) {
        super(deploymentType);
        this.configuration = new ExpressiveObjectsConfigurationDefault(ResourceStreamSourceContextLoaderClassPath.create("config"));
        this.servicesIfAny = services;
    }

    private static List<Object> asList(Object... objects) {
        return objects != null? Collections.unmodifiableList(Lists.newArrayList(objects)): null;
    }

    /**
     * Reads <tt>expressive-objects.properties</tt> (and other optional property files) from the &quot;config&quot; package on the current classpath.
     */
    @Override
    public ExpressiveObjectsConfiguration getConfiguration() {
        return configuration;
    }


    /**
     * Either the services explicitly provided by a constructor, otherwise reads from the configuration.
     */
    @Override
    protected List<Object> obtainServices() {
        if(servicesIfAny != null) {
            return servicesIfAny;
        }
        // else
        final ServicesInstallerFromConfiguration servicesInstaller = new ServicesInstallerFromConfiguration();
        return servicesInstaller.getServices(getDeploymentType());
    }

    /**
     * Install fixtures from configuration.
     */
    @Override
    protected FixturesInstaller obtainFixturesInstaller() throws ExpressiveObjectsSystemException {
        final FixturesInstallerFromConfiguration fixturesInstallerFromConfiguration = new FixturesInstallerFromConfiguration();
        fixturesInstallerFromConfiguration.setConfiguration(getConfiguration());
        return fixturesInstallerFromConfiguration;
    }


    /**
     * Optional hook method, to create the reflector with defaults (Java5, with cglib, and only the transaction facet decorators)
     * 
     * <p>
     * Each of the subcomponents can be overridden if required.
     * 
     * @see #obtainReflectorFacetDecoratorSet()
     * @see #obtainReflectorMetaModelValidator()
     * @see #obtainReflectorProgrammingModel()
     */
    @Override
    protected SpecificationLoaderSpi obtainSpecificationLoaderSpi(DeploymentType deploymentType, ClassSubstitutorFactory classSubstitutorFactory, Collection<MetaModelRefiner> metaModelRefiners) throws ExpressiveObjectsSystemException {
        
        final ProgrammingModel programmingModel = obtainReflectorProgrammingModel();
        final Set<FacetDecorator> facetDecorators = obtainReflectorFacetDecoratorSet();
        final MetaModelValidator mmv = obtainReflectorMetaModelValidator();
        
        return JavaReflectorHelper.createObjectReflector(programmingModel, classSubstitutorFactory, metaModelRefiners, facetDecorators, mmv, getConfiguration());
    }

 
    /**
     * Optional hook method, called from {@link #obtainSpecificationLoaderSpi(DeploymentType, ClassSubstitutorFactory, MetaModelRefiner)}.
     * @return
     */
    protected CollectionTypeRegistry obtainReflectorCollectionTypeRegistry() {
        return new CollectionTypeRegistryDefault();
    }


    /**
     * Optional hook method, called from {@link #obtainSpecificationLoaderSpi(DeploymentType, ClassSubstitutorFactory, MetaModelRefiner)}.
     * @return
     */
    protected ProgrammingModel obtainReflectorProgrammingModel() {
        return new ProgrammingModelFacetsJava5();
    }

    /**
     * Optional hook method, called from {@link #obtainSpecificationLoaderSpi(DeploymentType, ClassSubstitutorFactory, MetaModelRefiner)}.
     * @return
     */
    protected Set<FacetDecorator> obtainReflectorFacetDecoratorSet() {
        return Sets.newHashSet((FacetDecorator)new StandardTransactionFacetDecorator(getConfiguration()));
    }

    /**
     * Optional hook method, called from {@link #obtainSpecificationLoaderSpi(DeploymentType, ClassSubstitutorFactory, MetaModelRefiner)}.
     * @return
     */
    protected MetaModelValidator obtainReflectorMetaModelValidator() {
        return new MetaModelValidatorDefault();
    }

    /**
     * The standard authentication manager, configured with the default authenticator (allows all requests through).
     */
    @Override
    protected AuthenticationManager obtainAuthenticationManager(DeploymentType deploymentType) throws ExpressiveObjectsSystemException {
        final AuthenticationManagerStandard authenticationManager = new AuthenticationManagerStandard(getConfiguration());
        Authenticator authenticator = new AuthenticatorBypass(configuration);
        authenticationManager.addAuthenticator(authenticator);
        return authenticationManager;
    }

    /**
     * The standard authorization manager, allowing all access.
     */
    @Override
    protected AuthorizationManager obtainAuthorizationManager(DeploymentType deploymentType) {
        return new AuthorizationManagerStandard(getConfiguration());
    }

    /**
     * The in-memory user profile store.
     */
    @Override
    protected UserProfileStore obtainUserProfileStore() {
        return new InMemoryUserProfileStore();
    }

    /**
     * The in-memory object store (unless overridden by {@link #obtainPersistenceMechanismInstaller(ExpressiveObjectsConfiguration)}).
     */
    @Override
    protected PersistenceSessionFactory obtainPersistenceSessionFactory(DeploymentType deploymentType) throws ExpressiveObjectsSystemException {
        PersistenceMechanismInstaller installer = obtainPersistenceMechanismInstaller(getConfiguration());
        if(installer == null) {
            final InMemoryPersistenceMechanismInstaller inMemoryPersistenceMechanismInstaller = new InMemoryPersistenceMechanismInstaller();
            inMemoryPersistenceMechanismInstaller.setConfiguration(getConfiguration());
            installer = inMemoryPersistenceMechanismInstaller;
        }
        return installer.createPersistenceSessionFactory(deploymentType);
    }


    /**
     * Optional hook; if returns <tt>null</tt> then the {@link #obtainPersistenceSessionFactory(DeploymentType)} is used.
     */
    protected PersistenceMechanismInstaller obtainPersistenceMechanismInstaller(ExpressiveObjectsConfiguration configuration) throws ExpressiveObjectsSystemException {
        InMemoryPersistenceMechanismInstaller installer = new InMemoryPersistenceMechanismInstaller();
        installer.setConfiguration(getConfiguration());
        return installer;
    }

}
