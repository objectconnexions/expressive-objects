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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.facetprocessor;

import static uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Ensure.ensureThatState;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ListUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MethodRemover;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessParameterContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethodParameter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.MethodFilteringFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.MethodPrefixBasedFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.MethodRemoverConstants;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.PropertyOrCollectionIdentifyingFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModel;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContextAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistry;

public class FacetProcessor implements RuntimeContextAware {

    private final ExpressiveObjectsConfiguration configuration;
    private final CollectionTypeRegistry collectionTypeRegistry;
    private final ProgrammingModel programmingModel;

    private RuntimeContext runtimeContext;

    /**
     * Class<FacetFactory> => FacetFactory
     */
    private final Map<Class<? extends FacetFactory>, FacetFactory> factoryByFactoryType = Maps.newHashMap();

    /**
     * {@link FacetFactory Facet factories}, in order they were
     * {@link #registerFactory(FacetFactory) registered}.
     */
    private final List<FacetFactory> factories = Lists.newArrayList();

    /**
     * All method prefixes to check in {@link #recognizes(Method)}.
     * 
     * <p>
     * Derived from factories that implement
     * {@link MethodPrefixBasedFacetFactory}.
     * 
     * <p>
     * If <tt>null</tt>, indicates that the cache hasn't been built.
     */
    private List<String> cachedMethodPrefixes;

    /**
     * All registered {@link FacetFactory factories} that implement
     * {@link MethodFilteringFacetFactory}.
     * 
     * <p>
     * Used within {@link #recognizes(Method)}.
     * 
     * <p>
     * If <tt>null</tt>, indicates that the cache hasn't been built.
     */
    private List<MethodFilteringFacetFactory> cachedMethodFilteringFactories;

    /**
     * All registered {@link FacetFactory factories} that implement
     * {@link PropertyOrCollectionIdentifyingFacetFactory}.
     * 
     * <p>
     * Used within {@link #recognizes(Method)}.
     * 
     * <p>
     * If <tt>null</tt>, indicates that the cache hasn't been built.
     */
    private List<PropertyOrCollectionIdentifyingFacetFactory> cachedPropertyOrCollectionIdentifyingFactories;

    /**
     * ObjectFeatureType => List<FacetFactory>
     * 
     * <p>
     * Lazily initialized, then cached. The lists remain in the same order that
     * the factories were {@link #registerFactory(FacetFactory) registered}.
     */
    private Map<FeatureType, List<FacetFactory>> factoryListByFeatureType = null;

    public FacetProcessor(final ExpressiveObjectsConfiguration configuration, final CollectionTypeRegistry collectionTypeRegistry, final ProgrammingModel programmingModel) {
        ensureThatState(configuration, is(notNullValue()));
        ensureThatState(collectionTypeRegistry, is(notNullValue()));
        ensureThatState(programmingModel, is(notNullValue()));

        this.configuration = configuration;
        this.programmingModel = programmingModel;
        this.collectionTypeRegistry = collectionTypeRegistry;
    }

    // //////////////////////////////////////////////////
    // init, shutdown (application scoped)
    // //////////////////////////////////////////////////

    public void init() {
        ensureThatState(runtimeContext, is(notNullValue()));
        final List<FacetFactory> facetFactoryList = programmingModel.getList();
        for (final FacetFactory facetFactory : facetFactoryList) {
            registerFactory(facetFactory);
        }
    }

    public void shutdown() {
    }

    public void registerFactory(final FacetFactory factory) {
        clearCaches();
        factoryByFactoryType.put(factory.getClass(), factory);
        factories.add(factory);

        injectDependenciesInto(factory);
    }

    /**
     * This is <tt>public</tt> so that can be used for <tt>@Facets</tt>
     * processing.
     */
    public void injectDependenciesInto(final FacetFactory factory) {
        getCollectionTypeRepository().injectInto(factory);
        getExpressiveObjectsConfiguration().injectInto(factory);

        // cascades all the subcomponents also
        getRuntimeContext().injectInto(factory);
    }

    public FacetFactory getFactoryByFactoryType(final Class<? extends FacetFactory> factoryType) {
        return factoryByFactoryType.get(factoryType);
    }

    /**
     * Appends to the supplied {@link Set} all of the {@link Method}s that may
     * represent a property or collection.
     * 
     * <p>
     * Delegates to all known
     * {@link PropertyOrCollectionIdentifyingFacetFactory}s.
     */
    public Set<Method> findAssociationCandidateAccessors(final List<Method> methods, final Set<Method> candidates) {
        cachePropertyOrCollectionIdentifyingFacetFactoriesIfRequired();
        for (final Method method : methods) {
            if (method == null) {
                continue;
            }
            for (final PropertyOrCollectionIdentifyingFacetFactory facetFactory : cachedPropertyOrCollectionIdentifyingFactories) {
                if (facetFactory.isPropertyOrCollectionAccessorCandidate(method)) {
                    candidates.add(method);
                }
            }
        }
        return candidates;
    }

    /**
     * Use the provided {@link MethodRemover} to have all known
     * {@link PropertyOrCollectionIdentifyingFacetFactory}s to remove all
     * property accessors, and append them to the supplied methodList.
     * 
     * <p>
     * Intended to be called after
     * {@link #findAndRemoveValuePropertyAccessors(MethodRemover, List)} once
     * only reference properties remain.
     * 
     * @see PropertyOrCollectionIdentifyingFacetFactory#findAndRemoveValuePropertyAccessors(MethodRemover,
     *      List)
     */
    public void findAndRemovePropertyAccessors(final MethodRemover methodRemover, final List<Method> methodListToAppendTo) {
        cachePropertyOrCollectionIdentifyingFacetFactoriesIfRequired();
        for (final PropertyOrCollectionIdentifyingFacetFactory facetFactory : cachedPropertyOrCollectionIdentifyingFactories) {
            facetFactory.findAndRemovePropertyAccessors(methodRemover, methodListToAppendTo);
        }
    }

    /**
     * Use the provided {@link MethodRemover} to have all known
     * {@link PropertyOrCollectionIdentifyingFacetFactory}s to remove all
     * property accessors, and append them to the supplied methodList.
     * 
     * @see PropertyOrCollectionIdentifyingFacetFactory#findAndRemoveCollectionAccessors(MethodRemover,
     *      List)
     */
    public void findAndRemoveCollectionAccessors(final MethodRemover methodRemover, final List<Method> methodListToAppendTo) {
        cachePropertyOrCollectionIdentifyingFacetFactoriesIfRequired();
        for (final PropertyOrCollectionIdentifyingFacetFactory facetFactory : cachedPropertyOrCollectionIdentifyingFactories) {
            facetFactory.findAndRemoveCollectionAccessors(methodRemover, methodListToAppendTo);
        }
    }

    /**
     * Whether this {@link Method method} is recognized by any of the
     * {@link FacetFactory}s.
     * 
     * <p>
     * Typically this is when method has a specific prefix, such as
     * <tt>validate</tt> or <tt>hide</tt>. Specifically, it checks:
     * <ul>
     * <li>the method's prefix against the prefixes supplied by any
     * {@link MethodPrefixBasedFacetFactory}</li>
     * <li>the method against any {@link MethodFilteringFacetFactory}</li>
     * </ul>
     * 
     * <p>
     * The design of {@link MethodPrefixBasedFacetFactory} (whereby this facet
     * factory set does the work) is a slight performance optimization for when
     * there are multiple facet factories that search for the same prefix.
     */
    public boolean recognizes(final Method method) {
        cacheMethodPrefixesIfRequired();
        final String methodName = method.getName();
        for (final String prefix : cachedMethodPrefixes) {
            if (methodName.startsWith(prefix)) {
                return true;
            }
        }

        cacheMethodFilteringFacetFactoriesIfRequired();
        for (final MethodFilteringFacetFactory factory : cachedMethodFilteringFactories) {
            if (factory.recognizes(method)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Attaches all facets applicable to the provided {@link FeatureType#OBJECT
     * object}) to the supplied {@link FacetHolder}.
     * 
     * <p>
     * Delegates to {@link FacetFactory#process(Class, FacetHolder)} for each
     * appropriate factory.
     * 
     * @see FacetFactory#process(ProcessClassContext)
     * 
     * @param cls
     *            - class to process
     * @param facetHolder
     *            - holder to attach facets to.
     * 
     */
    public void process(final Class<?> cls, final MethodRemover methodRemover, final FacetHolder facetHolder) {
        final List<FacetFactory> factoryList = getFactoryListByFeatureType(FeatureType.OBJECT);
        for (final FacetFactory facetFactory : factoryList) {
            facetFactory.process(new ProcessClassContext(cls, removerElseNullRemover(methodRemover), facetHolder));
        }
    }

    public void processPost(final Class<?> cls, final MethodRemover methodRemover, final FacetHolder facetHolder) {
        final List<FacetFactory> factoryList = getFactoryListByFeatureType(FeatureType.OBJECT_POST_PROCESSING);
        for (final FacetFactory facetFactory : factoryList) {
            facetFactory.process(new ProcessClassContext(cls, removerElseNullRemover(methodRemover), facetHolder));
        }
    }

    /**
     * Attaches all facets applicable to the provided {@link FeatureType type of
     * feature} to the supplied {@link FacetHolder}.
     * 
     * <p>
     * Delegates to {@link FacetFactory#process(Method, FacetHolder)} for each
     * appropriate factory.
     * 
     * @see FacetFactory#process(Method, FacetHolder)
     * 
     * @param cls
     *            - class in which introspect; allowing the helper methods to be
     *            found is subclasses of that which the method was originally
     *            found.
     * @param method
     *            - method to process
     * @param facetedMethod
     *            - holder to attach facets to.
     * @param featureType
     *            - what type of feature the method represents (property,
     *            action, collection etc)
     */
    public void process(final Class<?> cls, final Method method, final MethodRemover methodRemover, final FacetedMethod facetedMethod, final FeatureType featureType) {
        final List<FacetFactory> factoryList = getFactoryListByFeatureType(featureType);
        for (final FacetFactory facetFactory : factoryList) {
            facetFactory.process(new ProcessMethodContext(cls, method, removerElseNullRemover(methodRemover), facetedMethod));
        }
    }

    /**
     * Attaches all facets applicable to the provided
     * {@link FeatureType#ACTION_PARAMETER parameter}), to the supplied
     * {@link FacetHolder}.
     * 
     * <p>
     * Delegates to {@link FacetFactory#processParams(ProcessParameterContext)}
     * for each appropriate factory.
     * 
     * @see FacetFactory#processParams(ProcessParameterContext)
     * 
     * @param method
     *            - action method to process
     * @param paramNum
     *            - 0-based
     * @param facetedMethodParameter
     *            - holder to attach facets to.
     */
    public void processParams(final Method method, final int paramNum, final FacetedMethodParameter facetedMethodParameter) {
        final List<FacetFactory> factoryList = getFactoryListByFeatureType(FeatureType.ACTION_PARAMETER);
        for (final FacetFactory facetFactory : factoryList) {
            facetFactory.processParams(new ProcessParameterContext(method, paramNum, facetedMethodParameter));
        }
    }

    private List<FacetFactory> getFactoryListByFeatureType(final FeatureType featureType) {
        cacheByFeatureTypeIfRequired();
        List<FacetFactory> list = factoryListByFeatureType.get(featureType);
        return list != null? list: Collections.<FacetFactory>emptyList();
    }

    private void clearCaches() {
        factoryListByFeatureType = null;
        cachedMethodPrefixes = null;
        cachedMethodFilteringFactories = null;
        cachedPropertyOrCollectionIdentifyingFactories = null;
    }

    private synchronized void cacheByFeatureTypeIfRequired() {
        if (factoryListByFeatureType != null) {
            return;
        }
        factoryListByFeatureType = Maps.newHashMap();
        for (final FacetFactory factory : factories) {
            final List<FeatureType> featureTypes = factory.getFeatureTypes();
            for (final FeatureType featureType : featureTypes) {
                final List<FacetFactory> factoryList = getList(factoryListByFeatureType, featureType);
                factoryList.add(factory);
            }
        }
    }

    private synchronized void cacheMethodPrefixesIfRequired() {
        if (cachedMethodPrefixes != null) {
            return;
        }
        cachedMethodPrefixes = Lists.newArrayList();
        for (final FacetFactory facetFactory : factories) {
            if (facetFactory instanceof MethodPrefixBasedFacetFactory) {
                final MethodPrefixBasedFacetFactory methodPrefixBasedFacetFactory = (MethodPrefixBasedFacetFactory) facetFactory;
                ListUtils.merge(cachedMethodPrefixes, methodPrefixBasedFacetFactory.getPrefixes());
            }
        }
    }

    private synchronized void cacheMethodFilteringFacetFactoriesIfRequired() {
        if (cachedMethodFilteringFactories != null) {
            return;
        }
        cachedMethodFilteringFactories = Lists.newArrayList();
        for (final FacetFactory factory : factories) {
            if (factory instanceof MethodFilteringFacetFactory) {
                final MethodFilteringFacetFactory methodFilteringFacetFactory = (MethodFilteringFacetFactory) factory;
                cachedMethodFilteringFactories.add(methodFilteringFacetFactory);
            }
        }
    }

    private synchronized void cachePropertyOrCollectionIdentifyingFacetFactoriesIfRequired() {
        if (cachedPropertyOrCollectionIdentifyingFactories != null) {
            return;
        }
        cachedPropertyOrCollectionIdentifyingFactories = Lists.newArrayList();
        final Iterator<FacetFactory> iter = factories.iterator();
        while (iter.hasNext()) {
            final FacetFactory factory = iter.next();
            if (factory instanceof PropertyOrCollectionIdentifyingFacetFactory) {
                final PropertyOrCollectionIdentifyingFacetFactory identifyingFacetFactory = (PropertyOrCollectionIdentifyingFacetFactory) factory;
                cachedPropertyOrCollectionIdentifyingFactories.add(identifyingFacetFactory);
            }
        }
    }

    private static <K, T> List<T> getList(final Map<K, List<T>> map, final K key) {
        List<T> list = map.get(key);
        if (list == null) {
            list = Lists.newArrayList();
            map.put(key, list);
        }
        return list;
    }

    private MethodRemover removerElseNullRemover(final MethodRemover methodRemover) {
        return methodRemover != null ? methodRemover : MethodRemoverConstants.NULL;
    }

    // ////////////////////////////////////////////////////////////////////
    // Dependencies (injected in constructor)
    // ////////////////////////////////////////////////////////////////////

    private ExpressiveObjectsConfiguration getExpressiveObjectsConfiguration() {
        return configuration;
    }

    private CollectionTypeRegistry getCollectionTypeRepository() {
        return collectionTypeRegistry;
    }

    // ////////////////////////////////////////////////////////////////////
    // Dependencies (injected via setter due to *Aware)
    // ////////////////////////////////////////////////////////////////////

    private RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    /**
     * Injected so can propogate to any {@link #registerFactory(FacetFactory)
     * registered} {@link FacetFactory} s that are also
     * {@link RuntimeContextAware}.
     */
    @Override
    public void setRuntimeContext(final RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

}
