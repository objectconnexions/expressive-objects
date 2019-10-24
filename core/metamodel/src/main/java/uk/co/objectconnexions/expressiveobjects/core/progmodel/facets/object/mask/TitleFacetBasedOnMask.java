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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.mask;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.title.TitleFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.title.TitleFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.title.TitleFacetUsingParser;

public class TitleFacetBasedOnMask extends TitleFacetAbstract {
    private final MaskFacet maskFacet;
    private final TitleFacet underlyingTitleFacet;

    public TitleFacetBasedOnMask(final MaskFacet maskFacet, final TitleFacet underlyingTitleFacet) {
        super(maskFacet.getFacetHolder());
        this.maskFacet = maskFacet;
        this.underlyingTitleFacet = underlyingTitleFacet;
    }

    @Override
    public String title(final ObjectAdapter object, final Localization localization) {
        final String mask = maskFacet.value();
        final TitleFacetUsingParser titleFacetUsingParser = (TitleFacetUsingParser) underlyingTitleFacet.getUnderlyingFacet();
        if (titleFacetUsingParser != null) {
            final String titleString = titleFacetUsingParser.title(object, mask);
            return titleString;
        } else {
            return underlyingTitleFacet.title(object, localization);
        }
    }

}
