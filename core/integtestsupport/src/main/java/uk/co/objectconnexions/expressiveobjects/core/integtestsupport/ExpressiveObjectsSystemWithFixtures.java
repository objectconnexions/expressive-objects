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

import java.util.Arrays;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.DomainObjectContainer;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures.Fixtures.Initialization;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModel;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidator;
import uk.co.objectconnexions.expressiveobjects.core.objectstore.InMemoryPersistenceMechanismInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequest;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.PersistenceMechanismInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.ObjectStoreSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.Persistor;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransaction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransaction.State;
import uk.co.objectconnexions.expressiveobjects.core.security.authentication.AuthenticationRequestNameOnly;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.AggregatedEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.ParentEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.ParentEntityRepository;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.ReferencingEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.SimpleEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.scalars.ApplibValuedEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.scalars.JdkValuedEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.scalars.PrimitiveValuedEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.scalars.WrapperValuedEntity;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.google.common.collect.Lists;

/**
 * Wraps a plain {@link ExpressiveObjectsSystemDefault}, and provides a number of features to assist with testing.
 *
 * <p>
 * TODO: need to make inherit from the {@link ExpressiveObjectsSystemForTest}.
 */
public class ExpressiveObjectsSystemWithFixtures implements org.junit.rules.TestRule {

    public interface Listener {

        void init(ExpressiveObjectsConfiguration configuration) throws Exception;
        
        void preSetupSystem(boolean firstTime) throws Exception;
        void postSetupSystem(boolean firstTime) throws Exception;
        
        void preBounceSystem() throws Exception;
        void postBounceSystem() throws Exception;

        void preTeardownSystem() throws Exception;
        void postTeardownSystem() throws Exception;
        
    }
    
    public static abstract class ListenerAdapter implements Listener {
        
        private ExpressiveObjectsConfiguration configuration;

        public void init(ExpressiveObjectsConfiguration configuration) throws Exception {
            this.configuration = configuration;
        }
        
        protected ExpressiveObjectsConfiguration getConfiguration() {
            return configuration;
        }

        @Override
        public void preSetupSystem(boolean firstTime) throws Exception {
        }

        @Override
        public void postSetupSystem(boolean firstTime) throws Exception {
        }

        @Override
        public void preBounceSystem() throws Exception {
        }

        @Override
        public void postBounceSystem() throws Exception {
        }

        @Override
        public void preTeardownSystem() throws Exception {
        }

        @Override
        public void postTeardownSystem() throws Exception {
        }

    }



    /**
     * A precanned set of fixtures for use by tests if desired.
     */
    public static class Fixtures {

        public enum Initialization {
            INIT,
            NO_INIT
        }

        public ParentEntityRepository associatedEntitiesRepository = new ParentEntityRepository();

        public ApplibValuedEntity ave1, ave2; 
        public JdkValuedEntity jve1, jve2;
        public PrimitiveValuedEntity pve1, pve2;
        public WrapperValuedEntity wve1, wve2;
        
        public SimpleEntity smpl1, smpl2, smpl3, smpl4, smpl5, smpl6;
        public ReferencingEntity rfcg1, rfcg2, rfcg3, rfcg4, rfcg5, rfcg6;
        public ParentEntity prnt1, prnt2, prnt3, prnt4, prnt5, prnt6;
        public AggregatedEntity rfcg1_a1, rfcg1_a2, rfcg1_a3, prnt1_a1, prnt1_a2, prnt1_a3;

        private void init(DomainObjectContainer container) {
            ave1 = container.newTransientInstance(ApplibValuedEntity.class);
            ave2 = container.newTransientInstance(ApplibValuedEntity.class);
            
            jve1 = container.newTransientInstance(JdkValuedEntity.class);
            jve2 = container.newTransientInstance(JdkValuedEntity.class);
            
            pve1 = container.newTransientInstance(PrimitiveValuedEntity.class);
            pve2 = container.newTransientInstance(PrimitiveValuedEntity.class);

            wve1 = container.newTransientInstance(WrapperValuedEntity.class);
            wve2 = container.newTransientInstance(WrapperValuedEntity.class);
            
            smpl1 = container.newTransientInstance(SimpleEntity.class);
            smpl2 = container.newTransientInstance(SimpleEntity.class);
            smpl3 = container.newTransientInstance(SimpleEntity.class);
            smpl4 = container.newTransientInstance(SimpleEntity.class);
            smpl5 = container.newTransientInstance(SimpleEntity.class);
            smpl6 = container.newTransientInstance(SimpleEntity.class);
            rfcg1 = container.newTransientInstance(ReferencingEntity.class);
            rfcg2 = container.newTransientInstance(ReferencingEntity.class);
            rfcg3 = container.newTransientInstance(ReferencingEntity.class);
            rfcg4 = container.newTransientInstance(ReferencingEntity.class);
            rfcg5 = container.newTransientInstance(ReferencingEntity.class);
            rfcg6 = container.newTransientInstance(ReferencingEntity.class);
            prnt1 = container.newTransientInstance(ParentEntity.class);
            prnt2 = container.newTransientInstance(ParentEntity.class);
            prnt3 = container.newTransientInstance(ParentEntity.class);
            prnt4 = container.newTransientInstance(ParentEntity.class);
            prnt5 = container.newTransientInstance(ParentEntity.class);
            prnt6 = container.newTransientInstance(ParentEntity.class);
            rfcg1_a1 = container.newAggregatedInstance(rfcg1, AggregatedEntity.class);
            rfcg1_a2 = container.newAggregatedInstance(rfcg1, AggregatedEntity.class);
            rfcg1_a3 = container.newAggregatedInstance(rfcg1, AggregatedEntity.class);
            prnt1_a1 = container.newAggregatedInstance(prnt1, AggregatedEntity.class);
            prnt1_a2 = container.newAggregatedInstance(prnt1, AggregatedEntity.class);
            prnt1_a3 = container.newAggregatedInstance(prnt1, AggregatedEntity.class);
        }
    }

    private ExpressiveObjectsSystemDefault expressiveObjectsSystem;
    private AuthenticationSession authenticationSession;

    // public visibility just to reduce noise in tests
    public DomainObjectContainer container;
    // public visibility just to reduce noise in tests
    public final Fixtures fixtures;
    
    private Initialization fixturesInitialization;
    private final ExpressiveObjectsConfiguration configuration;
    private final PersistenceMechanismInstaller persistenceMechanismInstaller;
    private final AuthenticationRequest authenticationRequest;
    private final List<Object> services;
    private List <Listener> listeners;
    private final MetaModelValidator metaModelValidator;
    private final ProgrammingModel programmingModel;

    
    ////////////////////////////////////////////////////////////
    // constructor
    ////////////////////////////////////////////////////////////

    public static class Builder {

        private AuthenticationRequest authenticationRequest = new AuthenticationRequestNameOnly("tester");
        
        private Initialization fixturesInitialization = Initialization.INIT;
        private ExpressiveObjectsConfiguration configuration;
        private PersistenceMechanismInstaller persistenceMechanismInstaller = new InMemoryPersistenceMechanismInstaller();
        private MetaModelValidator metaModelValidator;
        private ProgrammingModel programmingModel;

        private final List <Listener> listeners = Lists.newArrayList();
        private Object[] services;


        public Builder with(ExpressiveObjectsConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }
        
        public Builder with(Initialization initialization) {
            this.fixturesInitialization = initialization;
            return this;
        }
        
        public Builder with(PersistenceMechanismInstaller persistenceMechanismInstaller) {
            this.persistenceMechanismInstaller = persistenceMechanismInstaller;
            return this;
        }
        
        public Builder with(AuthenticationRequest authenticationRequest) {
            this.authenticationRequest = authenticationRequest;
            return this;
        }

        public Builder withServices(Object... services) {
            this.services = services;
            return this;
        }
        
        public ExpressiveObjectsSystemWithFixtures build() {
            final List<Object> servicesIfAny = services != null? Arrays.asList(services): null;
            return new ExpressiveObjectsSystemWithFixtures(fixturesInitialization, configuration, programmingModel, metaModelValidator, persistenceMechanismInstaller, authenticationRequest, servicesIfAny, listeners);
        }

        public Builder with(Listener listener) {
            if(listener != null) {
                listeners.add(listener);
            }
            return this;
        }

        public Builder with(MetaModelValidator metaModelValidator) {
            this.metaModelValidator = metaModelValidator;
            return this;
        }

        public Builder with(ProgrammingModel programmingModel) {
            this.programmingModel = programmingModel;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private ExpressiveObjectsSystemWithFixtures(Initialization fixturesInitialization, ExpressiveObjectsConfiguration configuration, ProgrammingModel programmingModel, MetaModelValidator metaModelValidator, PersistenceMechanismInstaller persistenceMechanismInstaller, AuthenticationRequest authenticationRequest, List<Object> services, List<Listener> listeners) {
        this.fixturesInitialization = fixturesInitialization;
        this.configuration = configuration;
        this.programmingModel = programmingModel;
        this.metaModelValidator = metaModelValidator;
        this.persistenceMechanismInstaller = persistenceMechanismInstaller;
        this.authenticationRequest = authenticationRequest;
        this.fixtures = new Fixtures();
        if(services == null) {
            services = Arrays.asList((Object)fixtures.associatedEntitiesRepository);
        }
        this.services = services;
        this.listeners = listeners;
    }


    ////////////////////////////////////////////////////////////
    // setup, teardown
    ////////////////////////////////////////////////////////////
    

    /**
     * Intended to be called from a test's {@link Before} method.
     */
    public void setUpSystem() throws Exception {
        setUpSystem(FireListeners.FIRE);
    }

    private void setUpSystem(FireListeners fireListeners) throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);

        boolean firstTime = expressiveObjectsSystem == null;
        if(fireListeners.shouldFire()) {
            fireInitAndPreSetupSystem(firstTime);
        }
        
        if(firstTime) {
            expressiveObjectsSystem = createExpressiveObjectsSystem(services);
            expressiveObjectsSystem.init();
            ExpressiveObjectsContext.closeSession();
        }

        final AuthenticationManager authenticationManager = expressiveObjectsSystem.getSessionFactory().getAuthenticationManager();
        authenticationSession = authenticationManager.authenticate(authenticationRequest);

        ExpressiveObjectsContext.openSession(authenticationSession);
        container = getContainer();
        if(firstTime && fixturesInitialization == Fixtures.Initialization.INIT) {
            fixtures.init(container);
        }
        if(fireListeners.shouldFire()) {
            firePostSetupSystem(firstTime);
        }
    }

    private enum FireListeners {
        FIRE,
        DONT_FIRE;
        public boolean shouldFire() {
            return this == FIRE;
        }
    }
    
    private DomainObjectContainer getContainer() {
        return getPersistenceSession().getServicesInjector().getContainer();
    }

    /**
     * Intended to be called from a test's {@link After} method.
     */
    public void tearDownSystem() throws Exception {
        tearDownSystem(FireListeners.FIRE);
    }

    private void tearDownSystem(final FireListeners fireListeners) throws Exception {
        if(fireListeners.shouldFire()) {
            firePreTeardownSystem();
        }
        ExpressiveObjectsContext.closeSession();
        if(fireListeners.shouldFire()) {
            firePostTeardownSystem();
        }
    }

    public void bounceSystem() throws Exception {
        firePreBounceSystem();
        tearDownSystem(FireListeners.DONT_FIRE);
        setUpSystem(FireListeners.DONT_FIRE);
        firePostBounceSystem();
    }


    private ExpressiveObjectsSystemDefault createExpressiveObjectsSystem(List<Object> services) {
        final ExpressiveObjectsSystemDefault system = new ExpressiveObjectsSystemDefault(DeploymentType.UNIT_TESTING, services) {
            @Override
            public ExpressiveObjectsConfiguration getConfiguration() {
                if(ExpressiveObjectsSystemWithFixtures.this.configuration != null) {
                    return ExpressiveObjectsSystemWithFixtures.this.configuration;
                } else {
                    return super.getConfiguration();
                }
            }
            @Override
            protected ProgrammingModel obtainReflectorProgrammingModel() {
                if(ExpressiveObjectsSystemWithFixtures.this.programmingModel != null) {
                    return ExpressiveObjectsSystemWithFixtures.this.programmingModel;
                } else {
                    return super.obtainReflectorProgrammingModel();
                }
            }
            @Override
            protected MetaModelValidator obtainReflectorMetaModelValidator() {
                if(ExpressiveObjectsSystemWithFixtures.this.metaModelValidator != null) {
                    return ExpressiveObjectsSystemWithFixtures.this.metaModelValidator;
                } else {
                    return super.obtainReflectorMetaModelValidator();
                }
            }
            @Override
            protected PersistenceMechanismInstaller obtainPersistenceMechanismInstaller(ExpressiveObjectsConfiguration configuration) {
                final PersistenceMechanismInstaller installer = ExpressiveObjectsSystemWithFixtures.this.persistenceMechanismInstaller;
                configuration.injectInto(installer);
                return installer;
            }
        };
        return system;
    }



    ////////////////////////////////////////////////////////////
    // listeners
    ////////////////////////////////////////////////////////////

    private void fireInitAndPreSetupSystem(boolean firstTime) throws Exception {
        if(firstTime) {
            for(Listener listener: listeners) {
                listener.init(configuration);
            }
        }
        for(Listener listener: listeners) {
            listener.preSetupSystem(firstTime);
        }
    }

    private void firePostSetupSystem(boolean firstTime) throws Exception {
        for(Listener listener: listeners) {
            listener.postSetupSystem(firstTime);
        }
    }

    private void firePreTeardownSystem() throws Exception {
        for(Listener listener: listeners) {
            listener.preTeardownSystem();
        }
    }

    private void firePostTeardownSystem() throws Exception {
        for(Listener listener: listeners) {
            listener.postTeardownSystem();
        }
    }

    private void firePreBounceSystem() throws Exception {
        for(Listener listener: listeners) {
            listener.preBounceSystem();
        }
    }

    private void firePostBounceSystem() throws Exception {
        for(Listener listener: listeners) {
            listener.postBounceSystem();
        }
    }

    
    ////////////////////////////////////////////////////////////
    // properties
    ////////////////////////////////////////////////////////////

    /**
     * The {@link ExpressiveObjectsSystemDefault} created during {@link #setUpSystem()}.
     * 
     * <p>
     * Can fine-tune the actual implementation using the hook {@link #createExpressiveObjectsSystem()}.
     */
    public ExpressiveObjectsSystemDefault getExpressiveObjectsSystem() {
        return expressiveObjectsSystem;
    }

    /**
     * The {@link AuthenticationSession} created during {@link #setUpSystem()}.
     * 
     * <p>
     * Can fine-tune before hand using {@link #createAuthenticationRequest()}.
     */
    public AuthenticationSession getAuthenticationSession() {
        return authenticationSession;
    }



    ////////////////////////////////////////////////////////////
    // Convenience for tests
    ////////////////////////////////////////////////////////////

    public ObjectSpecification loadSpecification(Class<?> cls) {
        return getExpressiveObjectsSystem().getSessionFactory().getSpecificationLoader().loadSpecification(cls);
    }

    public ObjectAdapter persist(Object domainObject) {
        ensureSessionInProgress();
        ensureObjectIsNotPersistent(domainObject);
        container.persist(domainObject);
        return adapterFor(domainObject);
    }

    public ObjectAdapter destroy(Object domainObject ) {
        ensureSessionInProgress();
        ensureObjectIsPersistent(domainObject);
        container.remove(domainObject);
        return adapterFor(domainObject);
    }

    public ObjectAdapter adapterFor(Object domainObject) {
        ensureSessionInProgress();
        return getAdapterManager().adapterFor(domainObject);
    }

    public ObjectAdapter reload(RootOid oid) {
        ensureSessionInProgress();
        final Persistor persistenceSession = getPersistenceSession();
        return persistenceSession.loadObject(oid);
    }

    public ObjectAdapter recreateAdapter(RootOid oid) {
        ensureSessionInProgress();
        return getAdapterManager().adapterFor(oid);
    }

    public ObjectAdapter remapAsPersistent(Object pojo, RootOid persistentOid) {
        ensureSessionInProgress();
        ensureObjectIsNotPersistent(pojo);
        final ObjectAdapter adapter = adapterFor(pojo);
        getPersistenceSession().remapAsPersistent(adapter, persistentOid);
        return adapter;
    }

    @SuppressWarnings("unchecked")
    public <T extends ObjectStoreSpi> T getObjectStore(Class<T> cls) {
        final PersistenceSession persistenceSession = getPersistenceSession();
        return (T) persistenceSession.getObjectStore();
    }

    private static void ensureSessionInProgress() {
        if(!ExpressiveObjectsContext.inSession()) {
            throw new IllegalStateException("Session must be in progress");
        }
    }

    private void ensureObjectIsNotPersistent(Object domainObject) {
        if(container.isPersistent(domainObject)) {
            throw new IllegalArgumentException("domain object is already persistent");
        }
    }

    private void ensureObjectIsPersistent(Object domainObject) {
        if(!container.isPersistent(domainObject)) {
            throw new IllegalArgumentException("domain object is not persistent");
        }
    }

    ////////////////////////////////////////////////////////////
    // JUnit @Rule integration
    ////////////////////////////////////////////////////////////

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                setUpSystem();
                try {
                    base.evaluate();
                    tearDownSystem();
                } catch(Throwable ex) {
                    try {
                        tearDownSystem();
                    } catch(Exception ex2) {
                        // ignore, since already one pending
                    }
                    throw ex;
                }
            }
        };
    }


    
    public void beginTran() {
        final ExpressiveObjectsTransactionManager transactionManager = getTransactionManager();
        final ExpressiveObjectsTransaction transaction = transactionManager.getTransaction();

        if(transaction == null) {
            transactionManager.startTransaction();
            return;
        } 

        final State state = transaction.getState();
        switch(state) {
            case COMMITTED:
            case ABORTED:
                transactionManager.startTransaction();
                break;
            case IN_PROGRESS:
                // nothing to do
                break;
            case MUST_ABORT:
                Assert.fail("Transaction is in state of '" + state + "'");
                break;
            default:
                Assert.fail("Unknown transaction state '" + state + "'");
        }
        
    }

    public void commitTran() {
        final ExpressiveObjectsTransactionManager transactionManager = getTransactionManager();
        final ExpressiveObjectsTransaction transaction = transactionManager.getTransaction();
        if(transaction == null) {
            Assert.fail("No transaction exists");
            return;
        } 
        final State state = transaction.getState();
        switch(state) {
            case COMMITTED:
            case ABORTED:
            case MUST_ABORT:
                Assert.fail("Transaction is in state of '" + state + "'");
                break;
            case IN_PROGRESS:
                transactionManager.endTransaction();
                break;
            default:
                Assert.fail("Unknown transaction state '" + state + "'");
        }
    }

    public void abortTran() {
        final ExpressiveObjectsTransactionManager transactionManager = getTransactionManager();
        final ExpressiveObjectsTransaction transaction = transactionManager.getTransaction();
        if(transaction == null) {
            Assert.fail("No transaction exists");
            return;
        } 
        final State state = transaction.getState();
        switch(state) {
            case ABORTED:
                break;
            case COMMITTED:
                Assert.fail("Transaction is in state of '" + state + "'");
                break;
            case MUST_ABORT:
            case IN_PROGRESS:
                transactionManager.abortTransaction();
                break;
            default:
                Assert.fail("Unknown transaction state '" + state + "'");
        }
    }

    protected ExpressiveObjectsTransactionManager getTransactionManager() {
        return getPersistenceSession().getTransactionManager();
    }
    
    public Persistor getPersistor() {
    	return getPersistenceSession();
    }
    
    public AdapterManager getAdapterManager() {
        return getPersistor().getAdapterManager();
    }

    protected PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }


    
}
