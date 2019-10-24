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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.choices.enums;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.CastUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueUsingValueSemanticsProviderFacetFactory;

public class EnumFacetFactory<T extends Enum<T>> extends ValueUsingValueSemanticsProviderFacetFactory<T> {

    public EnumFacetFactory() {
        super();
    }

    @Override
    public void process(final ProcessClassContext processClassContext) {
        final Class<?> cls = processClassContext.getCls();
        final FacetHolder holder = processClassContext.getFacetHolder();

        if (!cls.isEnum()) {
            return;
        }

        addFacets(new EnumValueSemanticsProvider<T>(holder, asT(cls), getConfiguration(), getContext()));
        FacetUtil.addFacet(new ChoicesFacetEnum(holder, cls.getEnumConstants()));
    }

    protected Class<T> asT(final Class<?> cls) {
        return CastUtils.cast(cls);
    }

}
