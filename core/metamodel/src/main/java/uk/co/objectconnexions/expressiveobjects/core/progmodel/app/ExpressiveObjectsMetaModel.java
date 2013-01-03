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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.app;

import static uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Ensure.ensureThatArg;
import static uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Ensure.ensureThatState;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;

import uk.co.objectconnexions.expressiveobjects.applib.DomainObjectContainer;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.ApplicationScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetdecorator.FacetDecorator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.layout.MemberLayoutArranger;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModel;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.ServicesInjectorSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.container.DomainObjectContainerDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.ObjectReflectorDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor.ClassSubstitutor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor.ClassSubstitutorAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistry;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistryDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.traverser.SpecificationTraverser;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.traverser.SpecificationTraverserDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.ValidationFailures;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.layout.dflt.MemberLayoutArrangerDefault;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.metamodelvalidator.dflt.MetaModelValidatorDefault;

/**
 * Facade for the entire Expressive Objects metamodel and supporting components.
 * 
 * <p>
 * Currently this is <i>not</i> used by Expressive Objects proper, but is available for use by integration tests.
 * The intention is to factor it into <tt>ExpressiveObjectsSystem</tt>.
 */
public class ExpressiveObjectsMetaModel implements ApplicationScopedComponent {

    private static enum State {
        NOT_INITIALIZED, INITIALIZED, SHUTDOWN;
    }

    private final List<Object> services = Lists.newArrayList();

    private State state = State.NOT_INITIALIZED;

    private ObjectReflectorDefault reflector;
    private RuntimeContext runtimeContext;

    private ExpressiveObjectsConfiguration configuration;
    private ClassSubstitutor classSubstitutor;
    private CollectionTypeRegistry collectionTypeRegistry;
    private ProgrammingModel programmingModel;
    private SpecificationTraverser specificationTraverser;
    private MemberLayoutArranger memberLayoutArranger;
    private Set<FacetDecorator> facetDecorators;
    private MetaModelValidator metaModelValidator;

    private DomainObjectContainer container;

    private ValidationFailures validationFailures;

    
    public static class Builder {
        private final RuntimeContext runtimeContext;
        private final ProgrammingModel programmingModel;
        private DomainObjectContainer container = new DomainObjectContainerDefault();
        private List<Object> services = Lists.newArrayList();
        
        private Builder(RuntimeContext embeddedContext, ProgrammingModel programmingModel) {
            this.runtimeContext = embeddedContext;
            this.programmingModel = programmingModel;
        }
        
        public Builder with(DomainObjectContainer container) {
            this.container = container;
            return this;
        }
        
        public Builder withServices(Object... services) {
            return withServices(Arrays.asList(services));
        }
        
        public Builder withServices(List<Object> services) {
            this.services = services;
            return this;
        }
        
        public ExpressiveObjectsMetaModel build() {
            final ExpressiveObjectsMetaModel expressiveObjectsMetaModel = new ExpressiveObjectsMetaModel(runtimeContext, programmingModel, services);
            if(container != null) {
                expressiveObjectsMetaModel.setContainer(container);
            }
            return expressiveObjectsMetaModel;
        }
    }
    
    public static Builder builder(RuntimeContext runtimeContext, ProgrammingModel programmingModel) {
        return new Builder(runtimeContext, programmingModel);
    }

    public ExpressiveObjectsMetaModel(final RuntimeContext runtimeContext, ProgrammingModel programmingModel, final List<Object> services) {
        this(runtimeContext, programmingModel, services.toArray());
    }
    
    public ExpressiveObjectsMetaModel(final RuntimeContext runtimeContext, ProgrammingModel programmingModel, final Object... services) {
        this.runtimeContext = runtimeContext;

        setContainer(new DomainObjectContainerDefault());
        this.services.addAll(Arrays.asList(services));
        setConfiguration(new ExpressiveObjectsConfigurationDefault());
        setClassSubstitutor(new ClassSubstitutorAbstract() {});
        setCollectionTypeRegistry(new CollectionTypeRegistryDefault());
        setSpecificationTraverser(new SpecificationTraverserDefault());
        setMemberLayoutArranger(new MemberLayoutArrangerDefault());
        setFacetDecorators(new TreeSet<FacetDecorator>());
        setProgrammingModelFacets(programmingModel);

        setMetaModelValidator(new MetaModelValidatorDefault());
    }

    private void setContainer(DomainObjectContainer container) {
        this.container = container;
    }

    /**
     * The list of classes representing services, as specified in the
     * {@link #ExpressiveObjectsMetaModel(EmbeddedContext, Class...) constructor}.
     * 
     * <p>
     * To obtain the instantiated services, use the
     * {@link ServicesInjectorSpi#getRegisteredServices()} (available from
     * {@link #getServicesInjector()}).
     */
    public List<Object> getServices() {
        return Collections.unmodifiableList(services);
    }

    // ///////////////////////////////////////////////////////
    // init, shutdown
    // ///////////////////////////////////////////////////////

    @Override
    public void init() {
        ensureNotInitialized();
        reflector = new ObjectReflectorDefault(configuration, classSubstitutor, collectionTypeRegistry, specificationTraverser, memberLayoutArranger, programmingModel, facetDecorators, metaModelValidator);

        runtimeContext.injectInto(container);
        runtimeContext.setContainer(container);
        runtimeContext.injectInto(reflector);
        reflector.injectInto(runtimeContext);

        validationFailures = reflector.initAndValidate();
        runtimeContext.init();

        for (final Object service : services) {
            final ObjectSpecification serviceSpec = reflector.loadSpecification(service.getClass());
            serviceSpec.markAsService();
        }
        state = State.INITIALIZED;
    }
    
    public ValidationFailures getValidationFailures() {
        return validationFailures;
    }

    @Override
    public void shutdown() {
        ensureInitialized();
        state = State.SHUTDOWN;
    }

    // ///////////////////////////////////////////////////////
    // SpecificationLoader
    // ///////////////////////////////////////////////////////

    /**
     * Available once {@link #init() initialized}.
     */
    public SpecificationLoaderSpi getSpecificationLoader() {
        return reflector;
    }

    // ///////////////////////////////////////////////////////
    // DomainObjectContainer
    // ///////////////////////////////////////////////////////

    /**
     * Available once {@link #init() initialized}.
     */
    public DomainObjectContainer getDomainObjectContainer() {
        ensureInitialized();
        return container;
    }

    // ///////////////////////////////////////////////////////
    // DependencyInjector
    // ///////////////////////////////////////////////////////

    /**
     * Available once {@link #init() initialized}.
     */
    public ServicesInjector getDependencyInjector() {
        ensureInitialized();
        return runtimeContext.getDependencyInjector();
    }

    // ///////////////////////////////////////////////////////
    // Override defaults
    // ///////////////////////////////////////////////////////

    /**
     * The {@link ExpressiveObjectsConfiguration} in force, either defaulted or specified
     * {@link #setConfiguration(ExpressiveObjectsConfiguration) explicitly.}
     */
    public ExpressiveObjectsConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Optionally specify the {@link ExpressiveObjectsConfiguration}.
     * 
     * <p>
     * Call prior to {@link #init()}.
     */
    public void setConfiguration(final ExpressiveObjectsConfiguration configuration) {
        ensureNotInitialized();
        ensureThatArg(configuration, is(notNullValue()));
        this.configuration = configuration;
    }

    /**
     * The {@link ClassSubstitutor} in force, either defaulted or specified
     * {@link #setClassSubstitutor(ClassSubstitutor) explicitly}.
     */
    public ClassSubstitutor getClassSubstitutor() {
        return classSubstitutor;
    }

    /**
     * Optionally specify the {@link ClassSubstitutor}.
     * 
     * <p>
     * Call prior to {@link #init()}.
     */
    public void setClassSubstitutor(final ClassSubstitutor classSubstitutor) {
        ensureNotInitialized();
        ensureThatArg(classSubstitutor, is(notNullValue()));
        this.classSubstitutor = classSubstitutor;
    }

    /**
     * The {@link CollectionTypeRegistry} in force, either defaulted or
     * specified {@link #setCollectionTypeRegistry(CollectionTypeRegistry)
     * explicitly.}
     */
    public CollectionTypeRegistry getCollectionTypeRegistry() {
        return collectionTypeRegistry;
    }

    /**
     * Optionally specify the {@link CollectionTypeRegistry}.
     * 
     * <p>
     * Call prior to {@link #init()}.
     */
    public void setCollectionTypeRegistry(final CollectionTypeRegistry collectionTypeRegistry) {
        ensureNotInitialized();
        ensureThatArg(collectionTypeRegistry, is(notNullValue()));
        this.collectionTypeRegistry = collectionTypeRegistry;
    }

    /**
     * The {@link SpecificationTraverser} in force, either defaulted or
     * specified {@link #setSpecificationTraverser(SpecificationTraverser)
     * explicitly}.
     */
    public SpecificationTraverser getSpecificationTraverser() {
        return specificationTraverser;
    }

    /**
     * Optionally specify the {@link SpecificationTraverser}.
     */
    public void setSpecificationTraverser(final SpecificationTraverser specificationTraverser) {
        this.specificationTraverser = specificationTraverser;
    }

    /**
     * Optionally specify the {@link MemberLayoutArranger}.
     */
    public void setMemberLayoutArranger(final MemberLayoutArranger memberLayoutArranger) {
        this.memberLayoutArranger = memberLayoutArranger;
    }

    /**
     * The {@link ProgrammingModel} in force, either defaulted or specified
     * {@link #setProgrammingModelFacets(ProgrammingModel) explicitly}.
     */
    public ProgrammingModel getProgrammingModelFacets() {
        return programmingModel;
    }

    /**
     * Optionally specify the {@link ProgrammingModel}.
     * 
     * <p>
     * Call prior to {@link #init()}.
     */
    public void setProgrammingModelFacets(final ProgrammingModel programmingModel) {
        ensureNotInitialized();
        ensureThatArg(programmingModel, is(notNullValue()));
        this.programmingModel = programmingModel;
    }

    /**
     * The {@link FacetDecorator}s in force, either defaulted or specified
     * {@link #setFacetDecorators(Set) explicitly}.
     */
    public Set<FacetDecorator> getFacetDecorators() {
        return Collections.unmodifiableSet(facetDecorators);
    }

    /**
     * Optionally specify the {@link FacetDecorator}s.
     * 
     * <p>
     * Call prior to {@link #init()}.
     */
    public void setFacetDecorators(final Set<FacetDecorator> facetDecorators) {
        ensureNotInitialized();
        ensureThatArg(facetDecorators, is(notNullValue()));
        this.facetDecorators = facetDecorators;
    }

    /**
     * The {@link MetaModelValidator} in force, either defaulted or specified
     * {@link #setMetaModelValidator(MetaModelValidator) explicitly}.
     */
    public MetaModelValidator getMetaModelValidator() {
        return metaModelValidator;
    }

    /**
     * Optionally specify the {@link MetaModelValidator}.
     */
    public void setMetaModelValidator(final MetaModelValidator metaModelValidator) {
        this.metaModelValidator = metaModelValidator;
    }

    // ///////////////////////////////////////////////////////
    // State management
    // ///////////////////////////////////////////////////////

    private State ensureNotInitialized() {
        return ensureThatState(state, is(State.NOT_INITIALIZED));
    }

    private State ensureInitialized() {
        return ensureThatState(state, is(State.INITIALIZED));
    }

}
