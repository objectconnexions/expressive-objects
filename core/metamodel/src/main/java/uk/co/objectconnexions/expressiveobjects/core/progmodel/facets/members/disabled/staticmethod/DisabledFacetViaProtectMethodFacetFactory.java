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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.disabled.staticmethod;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.NameUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.util.InvokeUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.exceptions.MetaModelException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.methodutils.MethodScope;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodFinderUtils;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixBasedFacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixConstants;

public class DisabledFacetViaProtectMethodFacetFactory extends MethodPrefixBasedFacetFactoryAbstract {

    private static final String[] PREFIXES = { MethodPrefixConstants.PROTECT_PREFIX };

    /**
     * Note that the {@link Facet}s registered are the generic ones from
     * noa-architecture (where they exist)
     */
    public DisabledFacetViaProtectMethodFacetFactory() {
        super(FeatureType.MEMBERS, OrphanValidation.VALIDATE, PREFIXES);
    }

    // ///////////////////////////////////////////////////////
    // Actions
    // ///////////////////////////////////////////////////////

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        attachDisabledFacetIfProtectMethodIsFound(processMethodContext);
    }

    public static void attachDisabledFacetIfProtectMethodIsFound(final ProcessMethodContext processMethodContext) {

        final Class<?>[] paramTypes = new Class[] {};

        final Class<?> type = processMethodContext.getCls();
        final Method method = processMethodContext.getMethod();

        final String capitalizedName = NameUtils.javaBaseNameStripAccessorPrefixIfRequired(method.getName());

        final Method protectMethod = MethodFinderUtils.findMethodWithOrWithoutParameters(type, MethodScope.CLASS, MethodPrefixConstants.PROTECT_PREFIX + capitalizedName, boolean.class, paramTypes);
        if (protectMethod == null) {
            return;
        }

        processMethodContext.removeMethod(protectMethod);

        final Boolean protectMethodReturnValue = invokeProtectMethod(protectMethod);
        if (!protectMethodReturnValue.booleanValue()) {
            return;
        }

        final FacetHolder facetedMethod = processMethodContext.getFacetHolder();
        FacetUtil.addFacet(new DisabledFacetAlwaysEverywhere(facetedMethod));
    }

    private static Boolean invokeProtectMethod(final Method protectMethod) {
        Boolean protectMethodReturnValue = null;
        try {
            protectMethodReturnValue = (Boolean) InvokeUtils.invokeStatic(protectMethod);
        } catch (final ClassCastException ex) {
            // ignore
        }
        if (protectMethodReturnValue == null) {
            throw new MetaModelException("method " + protectMethod + "must return a boolean");
        }
        return protectMethodReturnValue;
    }

}
