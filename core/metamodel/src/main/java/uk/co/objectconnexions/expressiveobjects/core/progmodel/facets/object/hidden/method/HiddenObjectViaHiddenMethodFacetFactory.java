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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.hidden.method;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.hide.HiddenObjectFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.methodutils.MethodScope;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectMember;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodFinderUtils;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixBasedFacetFactoryAbstract;

/**
 * Installs the {@link HiddenObjectFacetViaHiddenMethod} on the
 * {@link ObjectSpecification}, and copies this facet onto each
 * {@link ObjectMember}.
 * 
 * <p>
 * This two-pass design is required because, at the time that the
 * {@link #process(uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext)
 * class is being processed}, the {@link ObjectMember member}s for the
 * {@link ObjectSpecification spec} are not known.
 */
public class HiddenObjectViaHiddenMethodFacetFactory extends MethodPrefixBasedFacetFactoryAbstract {

    private static final String HIDDEN_PREFIX = "hidden";

    private static final String[] PREFIXES = { HIDDEN_PREFIX, };

    public HiddenObjectViaHiddenMethodFacetFactory() {
        super(FeatureType.EVERYTHING_BUT_PARAMETERS, OrphanValidation.VALIDATE, PREFIXES);
    }

    @Override
    public void process(final ProcessClassContext processClassContext) {
        for (final Class<?> returnType : new Class<?>[] { Boolean.class, boolean.class }) {
            if (addFacetIfMethodFound(processClassContext, returnType)) {
                return;
            }
        }
        return;
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        final FacetedMethod member = processMethodContext.getFacetHolder();
        final Class<?> owningClass = processMethodContext.getCls();
        final ObjectSpecification owningSpec = getSpecificationLoader().loadSpecification(owningClass);
        final HiddenObjectFacet facet = owningSpec.getFacet(HiddenObjectFacet.class);
        if (facet != null) {
            facet.copyOnto(member);
        }
    }

    private boolean addFacetIfMethodFound(final ProcessClassContext processClassContext, final Class<?> returnType) {
        final Class<?> cls = processClassContext.getCls();
        final FacetHolder facetHolder = processClassContext.getFacetHolder();

        final Method method = MethodFinderUtils.findMethod(cls, MethodScope.OBJECT, HIDDEN_PREFIX, returnType, NO_PARAMETERS_TYPES);
        if (method == null) {
            return false;
        }
        FacetUtil.addFacet(new HiddenObjectFacetViaHiddenMethod(method, facetHolder));
        processClassContext.removeMethod(method);
        return true;
    }

}
