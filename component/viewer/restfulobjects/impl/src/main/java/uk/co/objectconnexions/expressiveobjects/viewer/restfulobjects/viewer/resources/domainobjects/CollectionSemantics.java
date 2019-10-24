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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domainobjects;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToManyAssociation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.ResourceContext;

public enum CollectionSemantics {

    SET("addToSet"), LIST("addToList");

    private final String addToKey;

    private CollectionSemantics(final String addToKey) {
        this.addToKey = addToKey;

    }

    public String getAddToKey() {
        return addToKey;
    }

    public String getRemoveFromKey() {
        return "removeFrom";
    }

    public static CollectionSemantics determine(final ResourceContext resourceContext, final OneToManyAssociation collection) {
        return collection.getCollectionSemantics().isSet() ? CollectionSemantics.SET : CollectionSemantics.LIST;
    }

}
