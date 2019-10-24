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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.accessor;

import java.lang.reflect.Method;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MethodRemover;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.methodutils.MethodScope;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixConstants;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.PropertyOrCollectionIdentifyingFacetFactoryAbstract;

public class PropertyAccessorFacetFactory extends PropertyOrCollectionIdentifyingFacetFactoryAbstract {

    private static final String[] PREFIXES = { MethodPrefixConstants.GET_PREFIX, MethodPrefixConstants.IS_PREFIX };

    public PropertyAccessorFacetFactory() {
        super(FeatureType.PROPERTIES_ONLY, PREFIXES);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        attachPropertyAccessFacetForAccessorMethod(processMethodContext);
    }

    private static void attachPropertyAccessFacetForAccessorMethod(final ProcessMethodContext processMethodContext) {

        final Method accessorMethod = processMethodContext.getMethod();

        processMethodContext.removeMethod(accessorMethod);

        final FacetHolder property = processMethodContext.getFacetHolder();
        FacetUtil.addFacet(new PropertyAccessorFacetViaAccessor(accessorMethod, property));
    }

    // ///////////////////////////////////////////////////////
    // PropertyOrCollectionIdentifying
    // ///////////////////////////////////////////////////////

    @Override
    public boolean isPropertyOrCollectionAccessorCandidate(final Method method) {
        final String methodName = method.getName();
        if (methodName.startsWith(MethodPrefixConstants.GET_PREFIX)) {
            return true;
        }
        if (methodName.startsWith(MethodPrefixConstants.IS_PREFIX) && method.getReturnType() == boolean.class) {
            return true;
        }
        return false;
    }

    /**
     * The method way well represent a collection, but this facet factory does
     * not have any opinion on the matter.
     */
    @Override
    public boolean isCollectionAccessor(final Method method) {
        return false;
    }

    @Override
    public boolean isPropertyAccessor(final Method method) {
        if (!isPropertyOrCollectionAccessorCandidate(method)) {
            return false;
        }
        final Class<?> methodReturnType = method.getReturnType();
        return isCollectionOrArray(methodReturnType);
    }

    @Override
    public void findAndRemovePropertyAccessors(final MethodRemover methodRemover, final List<Method> methodListToAppendTo) {
        appendMatchingMethods(methodRemover, MethodPrefixConstants.IS_PREFIX, boolean.class, methodListToAppendTo);
        appendMatchingMethods(methodRemover, MethodPrefixConstants.GET_PREFIX, Object.class, methodListToAppendTo);
    }

    private static void appendMatchingMethods(final MethodRemover methodRemover, final String prefix, final Class<?> returnType, final List<Method> methodListToAppendTo) {
        methodListToAppendTo.addAll(methodRemover.removeMethods(MethodScope.OBJECT, prefix, returnType, false, 0));
    }

    @Override
    public void findAndRemoveCollectionAccessors(final MethodRemover methodRemover, final List<Method> methodListToAppendTo) {
        // does nothing
    }

}
