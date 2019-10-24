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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.defaults.method;

import java.lang.reflect.Method;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.NameUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.methodutils.MethodScope;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodFinderUtils;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixBasedFacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixConstants;

/**
 * Sets up all the {@link Facet}s for an action in a single shot.
 */
public class ActionDefaultsFacetFactory extends MethodPrefixBasedFacetFactoryAbstract {

    private static final String[] PREFIXES = { MethodPrefixConstants.DEFAULT_PREFIX };

    /**
     * Note that the {@link Facet}s registered are the generic ones from
     * noa-architecture (where they exist)
     */
    public ActionDefaultsFacetFactory() {
        super(FeatureType.ACTIONS_ONLY, OrphanValidation.VALIDATE, PREFIXES);
    }

    // ///////////////////////////////////////////////////////
    // Actions
    // ///////////////////////////////////////////////////////

    @Override
    public void process(final ProcessMethodContext processMethodContext) {

        attachActionDefaultsFacetIfParameterDefaultsMethodIsFound(processMethodContext);
    }

    private static void attachActionDefaultsFacetIfParameterDefaultsMethodIsFound(final ProcessMethodContext processMethodContext) {

        Method defaultsMethod = findDefaultsMethodReturning(processMethodContext, Object[].class);
        if (defaultsMethod == null) {
            defaultsMethod = findDefaultsMethodReturning(processMethodContext, List.class);
        }
        if (defaultsMethod == null) {
            return;
        }

        processMethodContext.removeMethod(defaultsMethod);

        final FacetHolder facetedMethod = processMethodContext.getFacetHolder();
        final ActionDefaultsFacetViaMethod facet = new ActionDefaultsFacetViaMethod(defaultsMethod, facetedMethod);
        FacetUtil.addFacet(facet);
    }

    private static Method findDefaultsMethodReturning(final ProcessMethodContext processMethodContext, final Class<?> returnType) {

        final Method actionMethod = processMethodContext.getMethod();
        final String capitalizedName = NameUtils.capitalizeName(actionMethod.getName());
        final String name = MethodPrefixConstants.DEFAULT_PREFIX + capitalizedName;

        final Class<?> cls = processMethodContext.getCls();
        return MethodFinderUtils.findMethod(cls, MethodScope.OBJECT, name, returnType, new Class[0]);
    }

}
