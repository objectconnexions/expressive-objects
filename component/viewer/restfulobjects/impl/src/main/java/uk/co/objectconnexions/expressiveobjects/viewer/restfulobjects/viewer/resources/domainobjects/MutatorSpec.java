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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domainobjects;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.HttpMethod;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.links.Rel;

public class MutatorSpec {

    public static MutatorSpec of(final Rel rel, final Class<? extends Facet> validationFacetType, final Class<? extends Facet> mutatorFacetType, final HttpMethod httpMethod, final BodyArgs argSpec) {
        return of(rel, validationFacetType, mutatorFacetType, httpMethod, argSpec, null);
    }

    public static MutatorSpec of(final Rel rel, final Class<? extends Facet> validationFacetType, final Class<? extends Facet> mutatorFacetType, final HttpMethod httpMethod, final BodyArgs argSpec, final String suffix) {
        return new MutatorSpec(rel, validationFacetType, mutatorFacetType, httpMethod, argSpec, suffix);
    }

    public final Rel rel;
    public final Class<? extends Facet> validationFacetType;
    public final Class<? extends Facet> mutatorFacetType;
    public final HttpMethod httpMethod;
    public final String suffix;
    public final BodyArgs arguments;

    private MutatorSpec(final Rel rel, final Class<? extends Facet> validationFacetType, final Class<? extends Facet> mutatorFacetType, final HttpMethod httpMethod, final BodyArgs bodyArgs, final String suffix) {
        this.rel = rel;
        this.validationFacetType = validationFacetType;
        this.mutatorFacetType = mutatorFacetType;
        this.httpMethod = httpMethod;
        this.arguments = bodyArgs;
        this.suffix = suffix;
    }

}