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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.ValueSemanticsProvider;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.JavaClassUtils;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.StringUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;

public final class ValueSemanticsProviderUtil {

    private ValueSemanticsProviderUtil() {
    }

    public static final String SEMANTICS_PROVIDER_NAME_KEY_PREFIX = "expressive-objects.core.progmodel.value.";
    public static final String SEMANTICS_PROVIDER_NAME_KEY_SUFFIX = ".semanticsProviderName";

    public static String semanticsProviderNameFromConfiguration(final Class<?> type, final ExpressiveObjectsConfiguration configuration) {
        final String key = SEMANTICS_PROVIDER_NAME_KEY_PREFIX + type.getCanonicalName() + SEMANTICS_PROVIDER_NAME_KEY_SUFFIX;
        final String semanticsProviderName = configuration.getString(key);
        return !StringUtils.isNullOrEmpty(semanticsProviderName) ? semanticsProviderName : null;
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends ValueSemanticsProvider<?>> valueSemanticsProviderOrNull(final Class<?> candidateClass, final String classCandidateName) {
        @SuppressWarnings("rawtypes")
        final Class clazz = candidateClass != null ? JavaClassUtils.implementingClassOrNull(candidateClass.getName(), ValueSemanticsProvider.class, FacetHolder.class) : null;
        return clazz != null ? clazz : JavaClassUtils.implementingClassOrNull(classCandidateName, ValueSemanticsProvider.class, FacetHolder.class);
    }

}
