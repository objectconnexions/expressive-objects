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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.fallback;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.TypedHolder;

/**
 * Central point for providing some kind of default for any {@link Facet}s
 * required by the Expressive Objects framework itself.
 * 
 */
public class FallbackFacetFactory extends FacetFactoryAbstract implements ExpressiveObjectsConfigurationAware {

    public final static int PAGE_SIZE_STANDALONE_DEFAULT = 25;
    public final static int PAGE_SIZE_PARENTED_DEFAULT = 12;

    private ExpressiveObjectsConfiguration configuration;

    @SuppressWarnings("unused")
    private final static Map<Class<?>, Integer> TYPICAL_LENGTHS_BY_CLASS = new HashMap<Class<?>, Integer>() {
        private static final long serialVersionUID = 1L;
        {
            putTypicalLength(byte.class, Byte.class, 3);
            putTypicalLength(short.class, Short.class, 5);
            putTypicalLength(int.class, Integer.class, 10);
            putTypicalLength(long.class, Long.class, 20);
            putTypicalLength(float.class, Float.class, 20);
            putTypicalLength(double.class, Double.class, 20);
            putTypicalLength(char.class, Character.class, 1);
            putTypicalLength(boolean.class, Boolean.class, 1);
        }

        private void putTypicalLength(final Class<?> primitiveClass, final Class<?> wrapperClass, final int length) {
            put(primitiveClass, Integer.valueOf(length));
            put(wrapperClass, Integer.valueOf(length));
        }
    };

    public FallbackFacetFactory() {
        super(FeatureType.EVERYTHING);
    }

    public boolean recognizes(final Method method) {
        return false;
    }

    @Override
    public void process(final ProcessClassContext processClassContaxt) {
        final FacetHolder facetHolder = processClassContaxt.getFacetHolder();

        final DescribedAsFacetNone describedAsFacet = new DescribedAsFacetNone(facetHolder);
        final NotPersistableFacetNull notPersistableFacet = new NotPersistableFacetNull(facetHolder);
        final TitleFacetNone titleFacet = new TitleFacetNone(facetHolder);
        final PagedFacetDefault pagedFacet = new PagedFacetDefault(facetHolder, getConfiguration().getInteger("expressive-objects.viewers.paged.standalone", PAGE_SIZE_STANDALONE_DEFAULT));
        
        final Facet[] facets = new Facet[] { describedAsFacet,
                // commenting these out, think this whole isNoop business is a little bogus
                // new ImmutableFacetNever(holder),
                notPersistableFacet, titleFacet, pagedFacet};
        FacetUtil.addFacets(facets);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        final List<Facet> facets = new ArrayList<Facet>();

        final FacetedMethod facetedMethod = processMethodContext.getFacetHolder();
        
        
        facets.add(new NamedFacetNone(facetedMethod));
        facets.add(new DescribedAsFacetNone(facetedMethod));
        facets.add(new HelpFacetNone(facetedMethod));

        
        final FeatureType featureType = facetedMethod.getFeatureType();
        if (featureType.isProperty()) {
            facets.add(new MaxLengthFacetUnlimited(facetedMethod));
            facets.add(new MultiLineFacetNone(true, facetedMethod));
        }
        if (featureType.isAction()) {
            facets.add(new ActionDefaultsFacetNone(facetedMethod));
            facets.add(new ActionChoicesFacetNone(facetedMethod));
        }
        if (featureType.isCollection()) {
            facets.add(new PagedFacetDefault(facetedMethod, getConfiguration().getInteger("expressive-objects.viewers.paged.parented", PAGE_SIZE_PARENTED_DEFAULT)));
        }

        FacetUtil.addFacets(facets);
    }

    @Override
    public void processParams(final ProcessParameterContext processParameterContext) {
        final List<Facet> facets = new ArrayList<Facet>();

        final TypedHolder typedHolder = processParameterContext.getFacetHolder();
        if (typedHolder.getFeatureType().isActionParameter()) {
            facets.add(new NamedFacetNone(typedHolder));
            facets.add(new DescribedAsFacetNone(typedHolder));
            facets.add(new HelpFacetNone(typedHolder));
            facets.add(new MultiLineFacetNone(false, typedHolder));

            facets.add(new MaxLengthFacetUnlimited(typedHolder));
        }

        FacetUtil.addFacets(facets);
    }

    @Override
    public void setConfiguration(ExpressiveObjectsConfiguration configuration) {
        this.configuration = configuration;
    }
    
    public ExpressiveObjectsConfiguration getConfiguration() {
        return configuration;
    }

}
