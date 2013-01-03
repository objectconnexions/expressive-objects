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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetdecorator.FacetDecorator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModel;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.traverser.SpecificationTraverser;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.traverser.SpecificationTraverserDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidator;

public final class ReflectorConstants {

    /**
     * Key used to lookup implementation of {@link ProgrammingModel} in
     * {@link ExpressiveObjectsConfiguration}.
     * 
     * @see #FACET_FACTORY_INCLUDE_CLASS_NAME_LIST
     * @see #FACET_FACTORY_EXCLUDE_CLASS_NAME_LIST
     */
    public static final String PROGRAMMING_MODEL_FACETS_CLASS_NAME = ConfigurationConstants.ROOT + "reflector.facets";
    public static final String PROGRAMMING_MODEL_FACETS_CLASS_NAME_DEFAULT = "uk.co.objectconnexions.expressiveobjects.progmodels.dflt.ProgrammingModelFacetsJava5";

    /**
     * Key used to lookup implementation of {@link MetaModelValidator} in
     * {@link ExpressiveObjectsConfiguration}.
     */
    public static final String META_MODEL_VALIDATOR_CLASS_NAME = ConfigurationConstants.ROOT + "reflector.validator";
    public static final String META_MODEL_VALIDATOR_CLASS_NAME_DEFAULT = "uk.co.objectconnexions.expressiveobjects.core.progmodel.metamodelvalidator.dflt.MetaModelValidatorDefault";


    /**
     * Key used to lookup comma-separated list of {@link FacetFactory}s to
     * include (over and above those specified by
     * {@link #PROGRAMMING_MODEL_FACETS_CLASS_NAME}.
     * 
     * @see #PROGRAMMING_MODEL_FACETS_CLASS_NAME
     * @see #FACET_FACTORY_EXCLUDE_CLASS_NAME_LIST
     */
    public static final String FACET_FACTORY_INCLUDE_CLASS_NAME_LIST = ConfigurationConstants.ROOT + "reflector.facets.include";

    /**
     * Key used to lookup comma-separated list of {@link FacetFactory}s to
     * exclude (that might otherwise be included specified by the
     * {@link #PROGRAMMING_MODEL_FACETS_CLASS_NAME}.
     * 
     * @see #PROGRAMMING_MODEL_FACETS_CLASS_NAME
     * @see #FACET_FACTORY_INCLUDE_CLASS_NAME_LIST
     */
    public static final String FACET_FACTORY_EXCLUDE_CLASS_NAME_LIST = ConfigurationConstants.ROOT + "reflector.facets.exclude";

    /**
     * Key used to lookup comma-separated list of {@link FacetDecorator}s.
     */
    public static final String FACET_DECORATOR_CLASS_NAMES = ConfigurationConstants.ROOT + "reflector.facet-decorators";

    private ReflectorConstants() {
    }

}
