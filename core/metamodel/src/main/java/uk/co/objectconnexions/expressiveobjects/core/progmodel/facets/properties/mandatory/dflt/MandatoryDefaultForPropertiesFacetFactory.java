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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.mandatory.dflt;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.mandatory.MandatoryFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.mandatory.MandatoryFacetDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.facetprocessor.FacetProcessor;

/**
 * Simply installs a {@link MandatoryFacetDefault} onto all properties and
 * parameters.
 * 
 * <p>
 * The idea is that this {@link FacetFactory} is included early on in the
 * {@link FacetProcessor}, but other {@link MandatoryFacet} implementations
 * which don't require mandatory semantics will potentially replace these where
 * the property or parameter is annotated or otherwise indicated as being
 * optional.
 */
public class MandatoryDefaultForPropertiesFacetFactory extends FacetFactoryAbstract {

    public MandatoryDefaultForPropertiesFacetFactory() {
        super(FeatureType.PROPERTIES_ONLY);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        FacetUtil.addFacet(create(processMethodContext.getFacetHolder()));
    }

    private MandatoryFacet create(final FacetHolder holder) {
        return new MandatoryFacetDefault(holder);
    }

}
