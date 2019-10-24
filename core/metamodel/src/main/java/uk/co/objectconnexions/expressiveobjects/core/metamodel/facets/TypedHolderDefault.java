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
package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolderImpl;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;

public class TypedHolderDefault extends FacetHolderImpl implements TypedHolder {

    private final FeatureType featureType;
    private Class<?> type;

    public TypedHolderDefault(final FeatureType featureType, final Class<?> type) {
        this.featureType = featureType;
        this.type = type;
    }

    @Override
    public FeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    /**
     * For {@link FeatureType#COLLECTION collection}s, represents the element
     * type.
     */
    @Override
    public void setType(final Class<?> type) {
        this.type = type;
    }

}
