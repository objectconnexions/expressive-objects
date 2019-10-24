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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.typicallen.annotation;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.TypicalLength;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.Annotations;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.typicallen.TypicalLengthFacet;

public class TypicalLengthAnnotationOnPropertyFacetFactory extends FacetFactoryAbstract {

    public TypicalLengthAnnotationOnPropertyFacetFactory() {
        super(FeatureType.PROPERTIES_ONLY);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        final TypicalLength annotation = Annotations.getAnnotation(processMethodContext.getMethod(), TypicalLength.class);
        final TypicalLengthFacet facet = create(annotation, processMethodContext.getFacetHolder());

        FacetUtil.addFacet(facet);
    }

    private TypicalLengthFacet create(final TypicalLength annotation, final FacetHolder holder) {
        return annotation != null ? new TypicalLengthFacetAnnotationOnProperty(annotation.value(), holder) : null;
    }

}
