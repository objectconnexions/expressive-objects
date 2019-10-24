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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.properties.defaults;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks.CreatedCallbackFacet;

/**
 * Provides a default value for a property of a newly created object.
 * 
 * <p>
 * In the standard Expressive Objects Programming Model, corresponds to the
 * <tt>defaultXxx</tt> supporting method for the property with accessor
 * <tt>getXxx</tt>.
 * 
 * <p>
 * Note: an alternative mechanism may be to specify the value in the created
 * callback.
 * 
 * @see CreatedCallbackFacet
 */
public interface PropertyDefaultFacet extends Facet {

    /**
     * The default value for this property in a newly created object.
     */
    public ObjectAdapter getDefault(ObjectAdapter inObject);
}
