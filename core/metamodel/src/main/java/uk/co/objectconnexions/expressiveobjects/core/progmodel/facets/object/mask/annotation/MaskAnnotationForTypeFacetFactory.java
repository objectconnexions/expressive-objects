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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.mask.annotation;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Mask;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.Annotations;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.mask.MaskFacet;

public class MaskAnnotationForTypeFacetFactory extends FacetFactoryAbstract {

    public MaskAnnotationForTypeFacetFactory() {
        super(FeatureType.OBJECTS_ONLY);
    }

    /**
     * In readiness for supporting <tt>@Value</tt> in the future.
     */
    @Override
    public void process(final ProcessClassContext processClassContaxt) {
        final Mask annotation = Annotations.getAnnotation(processClassContaxt.getCls(), Mask.class);
        FacetUtil.addFacet(createMaskFacet(annotation, processClassContaxt.getFacetHolder()));
    }

    private MaskFacet createMaskFacet(final Mask annotation, final FacetHolder holder) {
        return annotation != null ? new MaskFacetAnnotationForType(annotation.value(), null, holder) : null;
    }

}
