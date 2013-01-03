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

package uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi;

import static uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Ensure.ensureThatArg;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.applib.DomainObjectContainer;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.InstallerAbstract;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.factory.InstanceUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapterFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModel;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.ServicesInjectorDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.ServicesInjectorSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.container.DomainObjectContainerDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor.ClassSubstitutor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidatorComposite;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookup;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookupAware;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.PersistenceConstants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.PersistenceSessionFactoryDelegating;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adapter.PojoAdapterFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adaptermanager.AdapterManagerDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adaptermanager.PojoRecreator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adaptermanager.PojoRecreatorDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.internal.RuntimeContextFromSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.ExpressiveObjectsObjectStoreLogger;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.ObjectStoreSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.algorithm.PersistAlgorithm;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.algorithm.PersistAlgorithmDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.TransactionalResource;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.AdapterManagerSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.IdentifierGenerator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.IdentifierGeneratorDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.ObjectFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.OidGenerator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.EnlistedObjectDirtying;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.systemdependencyinjector.SystemDependencyInjector;

/**
 * An abstract implementation of {@link PersistenceMechanismInstaller} that will
 * lookup the {@link ObjectAdapterFactory} and {@link ObjectFactory} from the
 * supplied {@link ExpressiveObjectsConfiguration}.
 * 
 * <p>
 * If none can be found, then will default to the {@link PojoAdapterFactory} and
 * {@link PersistenceConstants#OBJECT_FACTORY_CLASS_NAME_DEFAULT default}link
 * ObjectFactory} (cglib at time of writing). respectively.
 */
public abstract class PersistenceMechanismInstallerAbstract extends InstallerAbstract implements PersistenceMechanismInstaller, InstallerLookupAware {


    private static final String LOGGING_PROPERTY = uk.co.objectconnexions.expressiveobjects.core.runtime.logging.Logger.PROPERTY_ROOT + "persistenceSession";
    private static final Logger LOG = Logger.getLogger(PersistenceMechanismInstallerAbstract.class);

    private SystemDependencyInjector installerLookup;

    public PersistenceMechanismInstallerAbstract(final String name) {
        super(PersistenceMechanismInstaller.TYPE, name);
    }

    /**
     * For subclasses that need to specify a different type.
     */
    public PersistenceMechanismInstallerAbstract(final String type, final String name) {
        super(type, name);
    }


    @Override
    public PersistenceSessionFactory createPersistenceSessionFactory(final DeploymentType deploymentType) {
        return new PersistenceSessionFactoryDelegating(deploymentType, getConfiguration(), this);
    }


    /**
     * Creates a {@link PersistenceSession} with internal (thread-safe) components obtained from the provided {@link PersistenceSessionFactory}.
     * 
     * <p>
     * Typically should not be overridden.
     */
    @Override
    public PersistenceSession createPersistenceSession(final PersistenceSessionFactory persistenceSessionFactory) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("installing " + this.getClass().getName());
        }

        ObjectAdapterFactory adapterFactory = persistenceSessionFactory.getAdapterFactory();
        ObjectFactory objectFactory = persistenceSessionFactory.getObjectFactory();
        PojoRecreator pojoRecreator = persistenceSessionFactory.getPojoRecreator();
        IdentifierGenerator identifierGenerator = persistenceSessionFactory.getIdentifierGenerator();
        ServicesInjectorSpi servicesInjector = persistenceSessionFactory.getServicesInjector();
        
        final PersistAlgorithm persistAlgorithm = createPersistAlgorithm(getConfiguration());
        final AdapterManagerDefault adapterManager = new AdapterManagerDefault(pojoRecreator);
        
        ObjectStoreSpi objectStore = createObjectStore(getConfiguration(), adapterFactory, adapterManager);
        
        ensureThatArg(persistAlgorithm, is(not(nullValue())));
        ensureThatArg(objectStore, is(not(nullValue())));
        
        if (getConfiguration().getBoolean(LOGGING_PROPERTY, false)) {
            final String level = getConfiguration().getString(LOGGING_PROPERTY + ".level", "debug");
            objectStore = new ExpressiveObjectsObjectStoreLogger(objectStore, level);
        }
        
        final PersistenceSession persistenceSession = 
                new PersistenceSession(persistenceSessionFactory, adapterFactory, objectFactory, servicesInjector, identifierGenerator, adapterManager, persistAlgorithm, objectStore);
        
        final ExpressiveObjectsTransactionManager transactionManager = createTransactionManager(persistenceSession, objectStore);
        
        ensureThatArg(persistenceSession, is(not(nullValue())));
        ensureThatArg(transactionManager, is(not(nullValue())));
        
        persistenceSession.setDirtiableSupport(true);
        persistenceSession.setTransactionManager(transactionManager);
        
        // ... and finally return
        return persistenceSession;
    }

    
    // ///////////////////////////////////////////
    // Mandatory hook methods
    // ///////////////////////////////////////////

    /**
     * Hook method to return {@link ObjectStoreSpi}.
     */
    protected abstract ObjectStoreSpi createObjectStore(ExpressiveObjectsConfiguration configuration, ObjectAdapterFactory adapterFactory, AdapterManagerSpi adapterManager);
    

    // ///////////////////////////////////////////
    // Optional hook methods
    // ///////////////////////////////////////////

    /**
     * Hook method to create {@link PersistAlgorithm}.
     * 
     * <p>
     * By default returns a {@link PersistAlgorithmDefault}.
     */
    protected PersistAlgorithm createPersistAlgorithm(final ExpressiveObjectsConfiguration configuration) {
        return new PersistAlgorithmDefault();
    }

    /**
     * Hook method to return an {@link ExpressiveObjectsTransactionManager}.
     * 
     * <p>
     * By default returns a {@link ExpressiveObjectsTransactionManager}.
     */
    protected ExpressiveObjectsTransactionManager createTransactionManager(final EnlistedObjectDirtying persistor, final TransactionalResource objectStore) {
        return new ExpressiveObjectsTransactionManager(persistor, objectStore);
    }


    @Override
    public ClassSubstitutor createClassSubstitutor(ExpressiveObjectsConfiguration configuration) {
        return InstanceUtil.createInstance(PersistenceConstants.CLASS_SUBSTITUTOR_CLASS_NAME_DEFAULT, ClassSubstitutor.class);
    }

    /**
     * Hook method to refine the {@link ProgrammingModel}.
     * 
     * <p>
     * By default, just returns the provided {@link ProgrammingModel}.
     */
    @Override
    public void refineProgrammingModel(ProgrammingModel baseProgrammingModel, ExpressiveObjectsConfiguration configuration) {
        // no-op
    }
    
    /**
     * Hook method to refine the {@link MetaModelValidator}.
     * 
     * <p>
     * By default, just returns the provided {@link MetaModelValidatorComposite}.
     * 
     * <p>Note that this methods deals in terms of {@link MetaModelValidatorComposite} (rather than plain {@link MetaModelValidator}}s) 
     * in order to allow new {@link MetaModelValidator}s to be easily {@link MetaModelValidatorComposite#add(MetaModelValidator) added}.
     */
    @Override
    public void refineMetaModelValidator(MetaModelValidatorComposite baseMetaModelValidator, ExpressiveObjectsConfiguration configuration) {
        // no-op
    }

    /**
     * Hook method to allow subclasses to specify a different implementation of
     * {@link ObjectAdapterFactory}.
     * 
     * <p>
     * By default, returns {@link PojoAdapterFactory};
     */
    public ObjectAdapterFactory createAdapterFactory(final ExpressiveObjectsConfiguration configuration) {
        return new PojoAdapterFactory();
    }
    
    /**
     * Hook method to allow subclasses to specify a different implementation of
     * {@link ObjectFactory}.
     * 
     * <p>
     * By default, returns <tt>uk.co.objectconnexions.expressiveobjects.runtimes.dflt.bytecode.dflt.objectfactory.CglibObjectFactory</tt>.  Note that this requires that
     * the <tt>uk.co.objectconnexions.expressiveobjects.runtimes.dflt.bytecode:dflt</tt> module is added to the classpath. 
     */
    public ObjectFactory createObjectFactory(final ExpressiveObjectsConfiguration configuration) {
        return InstanceUtil.createInstance(PersistenceConstants.OBJECT_FACTORY_CLASS_NAME_DEFAULT, ObjectFactory.class);
    }
    
    /**
     * Hook method to allow subclasses to specify a different implementation of
     * {@link ServicesInjectorSpi}
     * 
     * <p>
     * By default, returns {@link ServicesInjectorDefault};
     */
    public ServicesInjectorSpi createServicesInjector(final ExpressiveObjectsConfiguration configuration) {
        return new ServicesInjectorDefault();
    }

    /**
     * Hook method to allow subclasses to specify a different implementation of
     * {@link IdentifierGenerator}
     * 
     * <p>
     * By default, returns {@link IdentifierGeneratorDefault}.
     */
    public IdentifierGenerator createIdentifierGenerator(final ExpressiveObjectsConfiguration configuration) {
        return new IdentifierGeneratorDefault();
    }

    /**
     * Hook method to return {@link PojoRecreator}.
     * 
     * <p>
     * By default, returns {@link PojoRecreatorDefault}.
     */
    public PojoRecreator createPojoRecreator(final ExpressiveObjectsConfiguration configuration) {
        return new PojoRecreatorDefault();
    }

    /**
     * Hook method to return a {@link DomainObjectContainer}.
     * 
     * <p>
    * By default, looks up implementation from provided
    * {@link ExpressiveObjectsConfiguration} using
    * {@link PersistenceConstants#DOMAIN_OBJECT_CONTAINER_CLASS_NAME}. If no
    * implementation is specified, then defaults to
    * {@value PersistenceConstants#DOMAIN_OBJECT_CONTAINER_NAME_DEFAULT}.
     */
    public DomainObjectContainer createContainer(final ExpressiveObjectsConfiguration configuration) {
        final String configuredClassName = configuration.getString(PersistenceConstants.DOMAIN_OBJECT_CONTAINER_CLASS_NAME, PersistenceConstants.DOMAIN_OBJECT_CONTAINER_NAME_DEFAULT);
        return InstanceUtil.createInstance(configuredClassName, PersistenceConstants.DOMAIN_OBJECT_CONTAINER_NAME_DEFAULT, DomainObjectContainer.class);
    }
    

    
    // ///////////////////////////////////////////
    // Non overridable.
    // ///////////////////////////////////////////

    /**
     * Returns a {@link RuntimeContext}, with all application-specific properties
     * from the provided {@link ExpressiveObjectsConfiguration} copied over.
     */
    public final RuntimeContext createRuntimeContext(final ExpressiveObjectsConfiguration configuration) {
        final RuntimeContextFromSession runtimeContext = new RuntimeContextFromSession();
        final Properties properties = applicationPropertiesFrom(configuration);
        runtimeContext.setProperties(properties);
        return runtimeContext;
    }

    private static Properties applicationPropertiesFrom(final ExpressiveObjectsConfiguration configuration) {
        final Properties properties = new Properties();
        final ExpressiveObjectsConfiguration applicationConfiguration = configuration.getProperties("application");
        for (final String key : applicationConfiguration) {
            final String value = applicationConfiguration.getString(key);
            final String newKey = key.substring("application.".length());
            properties.setProperty(newKey, value);
        }
        return properties;
    }


    // /////////////////////////////////////////////////////
    // Dependencies (from setters)
    // /////////////////////////////////////////////////////

    /**
     * By virtue of being {@link InstallerLookupAware}.
     */
    @Override
    public void setInstallerLookup(final InstallerLookup installerLookup) {
        this.installerLookup = installerLookup;
    }

    /**
     * @see #setInstallerLookup(InstallerLookup)
     */
    protected SystemDependencyInjector getInstallerLookup() {
        return installerLookup;
    }

    
    // /////////////////////////////////////////////////////
    // Dependencies (from context)
    // /////////////////////////////////////////////////////

    protected SpecificationLoaderSpi getSpecificationLoader() {
        return ExpressiveObjectsContext.getSpecificationLoader();
    }
    
    
    // /////////////////////////////////////////////////////
    // Guice
    // /////////////////////////////////////////////////////

    @Override
    public List<Class<?>> getTypes() {
        return listOf(PersistenceSessionFactory.class);
    }


    
    
    
}
