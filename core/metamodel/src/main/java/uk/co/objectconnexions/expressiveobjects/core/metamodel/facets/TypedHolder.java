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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;

/**
 * A {@link FacetHolder} that also has a {@link ObjectSpecification type}.
 * 
 * <p>
 * Used to represent class members when building up the metamodel.
 */
public interface TypedHolder extends FacetHolder {

    public FeatureType getFeatureType();

    /**
     * The type of a {@link FeatureType#PROPERTY property}, the referenced type
     * of a {@link FeatureType#COLLECTION collection}, the return type of an
     * {@link FeatureType#ACTION action} and the type of a
     * {@link FeatureType#ACTION_PARAMETER action parameter}.
     */
    public Class<?> getType();

    /**
     * Type may not be known initially (eg {@link FeatureType#COLLECTION}s).
     * 
     * <p>
     * For example, the accessor might return a raw type such as
     * <tt>java.util.List</tt>, rather than a generic one such as
     * <tt>java.util.List&lt;Customer&gt;</tt>.
     */
    public void setType(Class<?> type);

}
