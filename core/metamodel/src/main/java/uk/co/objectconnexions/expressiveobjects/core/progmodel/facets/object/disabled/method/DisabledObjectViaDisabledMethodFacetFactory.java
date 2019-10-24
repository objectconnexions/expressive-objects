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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.disabled.method;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.applib.Identifier;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.methodutils.MethodScope;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectMember;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodFinderUtils;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixBasedFacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.disabled.DisabledObjectFacet;

/**
 * Installs the {@link DisabledObjectFacetViaDisabledMethod} on the
 * {@link ObjectSpecification}, and copies this facet onto each
 * {@link ObjectMember}.
 * 
 * <p>
 * This two-pass design is required because, at the time that the
 * {@link #process(uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext)
 * class is being processed}, the {@link ObjectMember member}s for the
 * {@link ObjectSpecification spec} are not known.
 */
public class DisabledObjectViaDisabledMethodFacetFactory extends MethodPrefixBasedFacetFactoryAbstract {

    private static final String DISABLED_PREFIX = "disabled";

    private static final String[] PREFIXES = { DISABLED_PREFIX, };

    public DisabledObjectViaDisabledMethodFacetFactory() {
        super(FeatureType.EVERYTHING_BUT_PARAMETERS, OrphanValidation.VALIDATE, PREFIXES);
    }

    @Override
    public void process(final ProcessClassContext processClassContext) {
        final Class<?> cls = processClassContext.getCls();
        final FacetHolder facetHolder = processClassContext.getFacetHolder();
        final Class<?>[] params = new Class<?>[1];
        params[0] = Identifier.Type.class;// String.class;

        final Method method = MethodFinderUtils.findMethod(cls, MethodScope.OBJECT, DISABLED_PREFIX, String.class, params);
        if (method == null) {
            return;
        }
        FacetUtil.addFacet(new DisabledObjectFacetViaDisabledMethod(method, facetHolder));
        processClassContext.removeMethod(method);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        final FacetedMethod member = processMethodContext.getFacetHolder();
        final Class<?> owningClass = processMethodContext.getCls();
        final ObjectSpecification owningSpec = getSpecificationLoader().loadSpecification(owningClass);
        final DisabledObjectFacet facet = owningSpec.getFacet(DisabledObjectFacet.class);
        if (facet != null) {
            facet.copyOnto(member);
        }
    }

}
