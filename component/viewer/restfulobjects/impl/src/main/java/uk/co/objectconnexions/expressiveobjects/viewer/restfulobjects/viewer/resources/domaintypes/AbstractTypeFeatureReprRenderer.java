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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domaintypes;

import com.google.common.base.Strings;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectFeature;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.ResourceContext;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.LinkFollower;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.ReprRendererAbstract;

public abstract class AbstractTypeFeatureReprRenderer<R extends ReprRendererAbstract<R, ParentSpecAndFeature<T>>, T extends ObjectFeature> extends ReprRendererAbstract<R, ParentSpecAndFeature<T>> {

    protected ObjectSpecification objectSpecification;
    protected T objectFeature;

    public AbstractTypeFeatureReprRenderer(final ResourceContext resourceContext, final LinkFollower linkFollower, final RepresentationType representationType, final JsonRepresentation representation) {
        super(resourceContext, linkFollower, representationType, representation);
    }

    public ObjectSpecification getParentSpecification() {
        return objectSpecification;
    }

    public T getObjectFeature() {
        return objectFeature;
    }

    @Override
    public R with(final ParentSpecAndFeature<T> specAndFeature) {
        objectSpecification = specAndFeature.getParentSpec();
        objectFeature = specAndFeature.getObjectFeature();

        return cast(this);
    }

    @Override
    public JsonRepresentation render() {

        addLinkSelfIfRequired();
        addLinkUpToParent();

        addPropertiesSpecificToFeature();

        addLinksSpecificToFeature();
        putExtensionsSpecificToFeature();

        return representation;
    }

    /**
     * Optional hook method.
     */
    protected void addPropertiesSpecificToFeature() {
    }

    /**
     * Mandatory hook method.
     */
    protected abstract void addLinkSelfIfRequired();

    /**
     * Mandatory hook method.
     */
    protected abstract void addLinkUpToParent();

    /**
     * Optional hook method.
     */
    protected void addLinksSpecificToFeature() {
    }

    /**
     * Mandatory hook method.
     */
    protected abstract void putExtensionsSpecificToFeature();

    protected void putExtensionsName() {
        final String friendlyName = getObjectFeature().getName();
        getExtensions().mapPut("friendlyName", friendlyName);
    }

    protected void putExtensionsDescriptionIfAvailable() {
        final String description = getObjectFeature().getDescription();
        if (!Strings.isNullOrEmpty(description)) {
            getExtensions().mapPut("description", description);
        }
    }

}