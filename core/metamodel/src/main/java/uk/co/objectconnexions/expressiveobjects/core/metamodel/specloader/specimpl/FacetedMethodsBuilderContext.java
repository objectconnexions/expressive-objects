/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.specimpl;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor.ClassSubstitutor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.facetprocessor.FacetProcessor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.traverser.SpecificationTraverser;

public class FacetedMethodsBuilderContext {
    public final SpecificationLoaderSpi specificationLoader;
    public final SpecificationTraverser specificationTraverser;
    public final FacetProcessor facetProcessor;
    public final ClassSubstitutor classSubstitutor;

    public FacetedMethodsBuilderContext(final SpecificationLoaderSpi specificationLoader, final ClassSubstitutor classSubstitutor, final SpecificationTraverser specificationTraverser, final FacetProcessor facetProcessor) {
        this.specificationLoader = specificationLoader;
        this.classSubstitutor = classSubstitutor;
        this.specificationTraverser = specificationTraverser;
        this.facetProcessor = facetProcessor;
    }
}