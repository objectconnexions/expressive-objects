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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.paged;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Paged;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.Annotations;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.paged.PagedFacet;

public class PagedAnnotationOnCollectionFacetFactory extends FacetFactoryAbstract implements ExpressiveObjectsConfigurationAware {

    private ExpressiveObjectsConfiguration configuration;

    public PagedAnnotationOnCollectionFacetFactory() {
        super(FeatureType.COLLECTIONS_ONLY);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        final Paged annotation = Annotations.getAnnotation(processMethodContext.getMethod(), Paged.class);
        FacetUtil.addFacet(create(annotation, processMethodContext.getFacetHolder()));
    }

    private PagedFacet create(final Paged annotation, final FacetHolder holder) {
        return annotation != null ? new PagedFacetAnnotation(holder, annotation.value()) : null;
    }

    public ExpressiveObjectsConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(ExpressiveObjectsConfiguration configuration) {
        this.configuration = configuration;
    }

}
