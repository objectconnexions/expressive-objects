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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.multiline.annotation;

import java.lang.annotation.Annotation;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.MultiLine;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.Annotations;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.multiline.MultiLineFacet;

public class MultiLineAnnotationOnParameterFacetFactory extends FacetFactoryAbstract {

    public MultiLineAnnotationOnParameterFacetFactory() {
        super(FeatureType.PARAMETERS_ONLY);
    }

    @Override
    public void processParams(final ProcessParameterContext processParameterContext) {
        final Class<?>[] parameterTypes = processParameterContext.getMethod().getParameterTypes();
        if (processParameterContext.getParamNum() >= parameterTypes.length) {
            // ignore
            return;
        }
        if (!Annotations.isString(parameterTypes[processParameterContext.getParamNum()])) {
            return;
        }
        final Annotation[] parameterAnnotations = Annotations.getParameterAnnotations(processParameterContext.getMethod())[processParameterContext.getParamNum()];
        for (final Annotation parameterAnnotation : parameterAnnotations) {
            if (parameterAnnotation instanceof MultiLine) {
                final MultiLine annotation = (MultiLine) parameterAnnotation;
                FacetUtil.addFacet(create(annotation, processParameterContext.getFacetHolder()));
                return;
            }
        }
    }

    private MultiLineFacet create(final MultiLine annotation, final FacetHolder holder) {
        return (annotation != null) ? new MultiLineFacetAnnotationOnParameter(annotation.numberOfLines(), annotation.preventWrapping(), holder) : null;
    }

}
