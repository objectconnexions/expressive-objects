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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.autocomplete.annotation;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.AutoComplete;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManagerAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.Annotations;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.autocomplete.AutoCompleteFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjectorAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderAware;

public class AutoCompleteAnnotationFacetFactory extends FacetFactoryAbstract implements AdapterManagerAware, ServicesInjectorAware, SpecificationLoaderAware {

    private AdapterManager adapterManager;
    private ServicesInjector servicesInjector;

    public AutoCompleteAnnotationFacetFactory() {
        super(FeatureType.OBJECTS_POST_PROCESSING_ONLY);
    }

    @Override
    public void process(final ProcessClassContext processClassContext) {
        final AutoComplete annotation = Annotations.getAnnotation(processClassContext.getCls(), AutoComplete.class);
        FacetUtil.addFacet(create(annotation, processClassContext.getFacetHolder()));
    }

    private AutoCompleteFacet create(final AutoComplete annotation, final FacetHolder holder) {
        if(annotation == null) { return null; }

        Class<?> repositoryClass = annotation.repository();
        String actionName = annotation.action();
        
        return new AutoCompleteFacetAnnotation(holder, repositoryClass, actionName, getSpecificationLoader(), adapterManager, servicesInjector);
    }

    @Override
    public void setAdapterManager(AdapterManager adapterManager) {
        this.adapterManager = adapterManager;
    }

    @Override
    public void setServicesInjector(ServicesInjector servicesInjector) {
        this.servicesInjector = servicesInjector;
    }

}
