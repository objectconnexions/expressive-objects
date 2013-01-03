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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.named.staticmethod;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.NameUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.util.InvokeUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.exceptions.MetaModelException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.methodutils.MethodScope;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodFinderUtils;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixBasedFacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixConstants;

/**
 * Sets up a {@link NamedFacet} if a {@value MethodPrefixConstants#NAME_PREFIX}
 * -prefixed method is present.
 */
public class NamedFacetViaNameMethodFacetFactory extends MethodPrefixBasedFacetFactoryAbstract {

    private static final String[] PREFIXES = { MethodPrefixConstants.NAME_PREFIX };

    /**
     * Note that the {@link Facet}s registered are the generic ones from
     * noa-architecture (where they exist)
     */
    public NamedFacetViaNameMethodFacetFactory() {
        super(FeatureType.MEMBERS, OrphanValidation.VALIDATE, PREFIXES);
    }

    // ///////////////////////////////////////////////////////
    // Actions
    // ///////////////////////////////////////////////////////

    @Override
    public void process(final ProcessMethodContext processMethodContext) {

        // namedXxx()
        attachNamedFacetIfNamedMethodIsFound(processMethodContext);

    }

    public static void attachNamedFacetIfNamedMethodIsFound(final ProcessMethodContext processMethodContext) {
        final Method method = processMethodContext.getMethod();
        final String capitalizedName = NameUtils.javaBaseNameStripAccessorPrefixIfRequired(method.getName());

        final Class<?> cls = processMethodContext.getCls();
        final Method nameMethod = MethodFinderUtils.findMethod(cls, MethodScope.CLASS, MethodPrefixConstants.NAME_PREFIX + capitalizedName, String.class, new Class[0]);

        if (nameMethod == null) {
            return;
        }

        processMethodContext.removeMethod(nameMethod);
        final String name = invokeNameMethod(nameMethod);

        final FacetHolder facetHolder = processMethodContext.getFacetHolder();
        FacetUtil.addFacet(new NamedFacetViaMethod(name, nameMethod, facetHolder));
    }

    private static String invokeNameMethod(final Method nameMethod) {
        String name = null;
        try {
            name = (String) InvokeUtils.invokeStatic(nameMethod);
        } catch (final ClassCastException e) {
            // ignore
        }
        if (name == null) {
            throw new MetaModelException("method " + nameMethod + "must return a string");
        }
        return name;
    }

}
