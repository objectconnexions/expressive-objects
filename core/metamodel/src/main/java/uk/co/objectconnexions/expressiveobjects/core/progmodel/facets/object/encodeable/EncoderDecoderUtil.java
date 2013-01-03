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

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.JavaClassUtils;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.StringUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;

public final class EncoderDecoderUtil {

    private EncoderDecoderUtil() {
    }

    public static final String ENCODER_DECODER_NAME_KEY_PREFIX = "expressive-objects.reflector.java.facets.encoderDecoder.";
    public static final String ENCODER_DECODER_NAME_KEY_SUFFIX = ".encoderDecoderName";

    static String encoderDecoderNameFromConfiguration(final Class<?> type, final ExpressiveObjectsConfiguration configuration) {
        final String key = ENCODER_DECODER_NAME_KEY_PREFIX + type.getCanonicalName() + ENCODER_DECODER_NAME_KEY_SUFFIX;
        final String encoderDecoderName = configuration.getString(key);
        return !StringUtils.isNullOrEmpty(encoderDecoderName) ? encoderDecoderName : null;
    }

    public static Class<?> encoderDecoderOrNull(final Class<?> candidateClass, final String classCandidateName) {
        final Class<?> type = candidateClass != null ? JavaClassUtils.implementingClassOrNull(candidateClass.getName(), EncoderDecoder.class, FacetHolder.class) : null;
        return type != null ? type : JavaClassUtils.implementingClassOrNull(classCandidateName, EncoderDecoder.class, FacetHolder.class);
    }

}
