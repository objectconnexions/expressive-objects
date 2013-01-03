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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.invoke;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.NameUtils;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.StringUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManagerAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.actions.debug.DebugFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.actions.exploration.ExplorationFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.actions.invoke.ActionInvocationFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacetInferred;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixBasedFacetFactoryAbstract;

/**
 * Sets up {@link ActionInvocationFacet}, along with a number of supporting
 * facets that are based on the action's name.
 * 
 * <p>
 * The supporting methods are: {@link ExecutedFacet}, {@link ExplorationFacet}
 * and {@link DebugFacet}. In addition a {@link NamedFacet} is inferred from the
 * name (taking into account the above well-known prefixes).
 */
public class ActionInvocationFacetFactory extends MethodPrefixBasedFacetFactoryAbstract implements AdapterManagerAware {

    private static final String EXPLORATION_PREFIX = "Exploration";
    private static final String DEBUG_PREFIX = "Debug";

    private static final String[] PREFIXES = { EXPLORATION_PREFIX, DEBUG_PREFIX };

    private AdapterManager adapterManager;

    /**
     * Note that the {@link Facet}s registered are the generic ones from
     * noa-architecture (where they exist)
     */
    public ActionInvocationFacetFactory() {
        super(FeatureType.ACTIONS_ONLY, OrphanValidation.VALIDATE, PREFIXES);
    }

    // ///////////////////////////////////////////////////////
    // Actions
    // ///////////////////////////////////////////////////////

    @Override
    public void process(final ProcessMethodContext processMethodContext) {

        // InvocationFacet
        attachInvocationFacet(processMethodContext);

        // DebugFacet, ExplorationFacet
        attachDebugFacetIfActionMethodNamePrefixed(processMethodContext);
        attachExplorationFacetIfActionMethodNamePrefixed(processMethodContext);

        // inferred name
        // (must be called after the attachinvocationFacet methods)
        attachNamedFacetInferredFromMethodName(processMethodContext); 
    }

    private void attachInvocationFacet(final ProcessMethodContext processMethodContext) {

        final Method actionMethod = processMethodContext.getMethod();

        try {
            final Class<?> returnType = actionMethod.getReturnType();
            final ObjectSpecification returnSpec = getSpecificationLoader().loadSpecification(returnType);
            if (returnSpec == null) {
                return;
            }

            final Class<?> cls = processMethodContext.getCls();
            final ObjectSpecification typeSpec = getSpecificationLoader().loadSpecification(cls);
            final FacetHolder holder = processMethodContext.getFacetHolder();

            FacetUtil.addFacet(new ActionInvocationFacetViaMethod(actionMethod, typeSpec, returnSpec, holder, getAdapterManager()));
        } finally {
            processMethodContext.removeMethod(actionMethod);
        }
    }

    private void attachDebugFacetIfActionMethodNamePrefixed(final ProcessMethodContext processMethodContext) {

        final Method actionMethod = processMethodContext.getMethod();
        final String capitalizedName = NameUtils.capitalizeName(actionMethod.getName());
        if (!capitalizedName.startsWith(DEBUG_PREFIX)) {
            return;
        }
        final FacetHolder facetedMethod = processMethodContext.getFacetHolder();
        FacetUtil.addFacet(new DebugFacetViaNamingConvention(facetedMethod));
    }

    private void attachExplorationFacetIfActionMethodNamePrefixed(final ProcessMethodContext processMethodContext) {

        final Method actionMethod = processMethodContext.getMethod();
        final String capitalizedName = NameUtils.capitalizeName(actionMethod.getName());
        if (!capitalizedName.startsWith(EXPLORATION_PREFIX)) {
            return;
        }
        final FacetHolder facetedMethod = processMethodContext.getFacetHolder();
        FacetUtil.addFacet(new ExplorationFacetViaNamingConvention(facetedMethod));
    }

    /**
     * Must be called after added the debug, exploration etc facets.
     * 
     * <p>
     * TODO: remove this hack
     */
    private void attachNamedFacetInferredFromMethodName(final ProcessMethodContext processMethodContext) {

        final Method method = processMethodContext.getMethod();
        final String capitalizedName = NameUtils.capitalizeName(method.getName());

        // this is nasty...
        String name = capitalizedName;
        name = StringUtils.removePrefix(name, DEBUG_PREFIX);
        name = StringUtils.removePrefix(name, EXPLORATION_PREFIX);
        name = NameUtils.naturalName(name);

        final FacetHolder facetedMethod = processMethodContext.getFacetHolder();
        FacetUtil.addFacet(new NamedFacetInferred(name, facetedMethod));
    }

    // ///////////////////////////////////////////////////////////////
    // Dependencies
    // ///////////////////////////////////////////////////////////////

    @Override
    public void setAdapterManager(final AdapterManager adapterManager) {
        this.adapterManager = adapterManager;
    }

    private AdapterManager getAdapterManager() {
        return adapterManager;
    }

}
