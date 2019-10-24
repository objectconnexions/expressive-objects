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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.facets;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Facets;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.MultipleValueFacet;

/**
 * Indicates that this class has additional arbitrary facets, to be processed.
 * 
 * <p>
 * Corresponds to the {@link Facets} annotation in the applib.
 * 
 * <p>
 * <i>This</i> {@link Facet} allows the {@link FacetFactory}(s) that will create
 * those {@link Facet}s to be accessed. Which, admittedly, is rather confusing.
 */
public interface FacetsFacet extends MultipleValueFacet {

    /**
     * Returns the fully qualified class of the facet factory, which should be
     * {@link Class#isAssignableFrom(Class)} {@link FacetFactory}.
     * 
     * <p>
     * Includes both the named facet factories and those identified directly by
     * class. However, all are guaranteed to implement {@link FacetFactory}.
     */
    public Class<? extends FacetFactory>[] facetFactories();
}
