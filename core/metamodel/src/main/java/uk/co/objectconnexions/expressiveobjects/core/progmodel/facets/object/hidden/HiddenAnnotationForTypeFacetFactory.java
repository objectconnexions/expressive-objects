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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.hidden;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Hidden;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.Annotations;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.hide.HiddenFacet;

public class HiddenAnnotationForTypeFacetFactory extends FacetFactoryAbstract {

    public HiddenAnnotationForTypeFacetFactory() {
        super(FeatureType.OBJECTS_ONLY);
    }

    @Override
    public void process(final ProcessClassContext processClassContext) {
        final Hidden annotation = Annotations.getAnnotation(processClassContext.getCls(), Hidden.class);
        FacetUtil.addFacet(create(annotation, processClassContext.getFacetHolder()));
    }

    private static HiddenFacet create(final Hidden annotation, final FacetHolder holder) {
        return annotation == null ? null : new HiddenFacetForTypeAnnotation(annotation.when(), holder);
    }

}
