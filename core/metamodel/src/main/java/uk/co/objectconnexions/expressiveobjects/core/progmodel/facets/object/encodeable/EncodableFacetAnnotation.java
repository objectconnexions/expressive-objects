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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.encodeable;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Encodable;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.StringUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;

public class EncodableFacetAnnotation extends EncodableFacetAbstract {

    private static String encoderDecoderName(final Class<?> annotatedClass, final ExpressiveObjectsConfiguration configuration) {
        final Encodable annotation = annotatedClass.getAnnotation(Encodable.class);
        final String encoderDecoderName = annotation.encoderDecoderName();
        if (!StringUtils.isNullOrEmpty(encoderDecoderName)) {
            return encoderDecoderName;
        }
        return EncoderDecoderUtil.encoderDecoderNameFromConfiguration(annotatedClass, configuration);
    }

    private static Class<?> encoderDecoderClass(final Class<?> annotatedClass) {
        final Encodable annotation = annotatedClass.getAnnotation(Encodable.class);
        return annotation.encoderDecoderClass();
    }

    public EncodableFacetAnnotation(final Class<?> annotatedClass, final ExpressiveObjectsConfiguration configuration, final FacetHolder holder, final AdapterManager adapterManager, final ServicesInjector dependencyInjector) {
        this(encoderDecoderName(annotatedClass, configuration), encoderDecoderClass(annotatedClass), holder, adapterManager, dependencyInjector);
    }

    private EncodableFacetAnnotation(final String candidateEncoderDecoderName, final Class<?> candidateEncoderDecoderClass, final FacetHolder holder, final AdapterManager adapterManager, final ServicesInjector dependencyInjector) {
        super(candidateEncoderDecoderName, candidateEncoderDecoderClass, holder, adapterManager, dependencyInjector);
    }

}
