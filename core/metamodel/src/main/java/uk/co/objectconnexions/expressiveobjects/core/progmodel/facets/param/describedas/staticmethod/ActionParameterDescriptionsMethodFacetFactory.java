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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.describedas.staticmethod;

import java.lang.reflect.Method;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.NameUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.util.InvokeUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.exceptions.MetaModelException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethodParameter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.methodutils.MethodScope;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodFinderUtils;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixBasedFacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixConstants;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.describedas.staticmethod.DescribedAsFacetViaMethod;

public class ActionParameterDescriptionsMethodFacetFactory extends MethodPrefixBasedFacetFactoryAbstract {

    private static final String[] PREFIXES = { MethodPrefixConstants.DESCRIPTION_PREFIX };

    /**
     * Note that the {@link Facet}s registered are the generic ones from
     * noa-architecture (where they exist)
     */
    public ActionParameterDescriptionsMethodFacetFactory() {
        super(FeatureType.ACTIONS_ONLY, OrphanValidation.VALIDATE, PREFIXES);
    }

    // ///////////////////////////////////////////////////////
    // Actions
    // ///////////////////////////////////////////////////////

    @Override
    public void process(final ProcessMethodContext processMethodContext) {

        final FacetedMethod facetedMethod = processMethodContext.getFacetHolder();
        final List<FacetedMethodParameter> holderList = facetedMethod.getParameters();

        attachDescribedAsFacetForParametersIfParameterDescriptionsMethodIsFound(processMethodContext, holderList);
    }

    private static void attachDescribedAsFacetForParametersIfParameterDescriptionsMethodIsFound(final ProcessMethodContext processMethodContext, final List<FacetedMethodParameter> parameters) {

        if (parameters.isEmpty()) {
            return;
        }

        final Method actionMethod = processMethodContext.getMethod();

        final String capitalizedName = NameUtils.capitalizeName(actionMethod.getName());

        final Class<?> cls = processMethodContext.getCls();
        final Method descriptionMethod = MethodFinderUtils.findMethod(cls, MethodScope.CLASS, MethodPrefixConstants.DESCRIPTION_PREFIX + capitalizedName, String[].class, new Class[0]);
        if (descriptionMethod == null) {
            return;
        }
        processMethodContext.removeMethod(descriptionMethod);

        final String[] descriptions = invokeDescriptionsMethod(descriptionMethod, parameters.size());
        for (int i = 0; i < descriptions.length; i++) {
            // add facets directly to parameters, not to actions
            FacetUtil.addFacet(new DescribedAsFacetViaMethod(descriptions[i], descriptionMethod, parameters.get(i)));
        }
    }

    private static String[] invokeDescriptionsMethod(final Method descriptionMethod, final int numElementsRequired) {
        String[] descriptions = null;
        try {
            descriptions = (String[]) InvokeUtils.invokeStatic(descriptionMethod, new Object[0]);
        } catch (final ClassCastException ex) {
            // ignore
        }
        if (descriptions == null || descriptions.length != numElementsRequired) {
            throw new MetaModelException(descriptionMethod + " must return an String[] array of same size as number of parameters of action");
        }
        return descriptions;
    }

}
