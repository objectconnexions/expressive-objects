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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.spec;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.components.ApplicationScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebuggableWithTitle;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.ValidationFailures;

public interface SpecificationLoaderSpi extends ApplicationScopedComponent, DebuggableWithTitle, SpecificationLoader {


    /**
     * Specify the classes of the services to pro-actively prime the cache.
     */
    void setServiceClasses(List<Class<?>> serviceClasses);

    /**
     * Populated as a result of running {@link MetaModelValidator#validate() validation} after all specs have been loaded. 
     * @param validationFailures 
     */
    void validateSpecifications(ValidationFailures validationFailures);

}
