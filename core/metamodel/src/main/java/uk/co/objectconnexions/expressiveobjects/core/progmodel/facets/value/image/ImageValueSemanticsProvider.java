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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.image;

import uk.co.objectconnexions.expressiveobjects.applib.value.Image;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;

public class ImageValueSemanticsProvider extends ImageValueSemanticsProviderAbstract<Image> {

    public ImageValueSemanticsProvider(final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(holder, Image.class, configuration, context);
    }

    @Override
    public int getHeight(final ObjectAdapter object) {
        return image(object).getHeight();
    }

    private Image image(final ObjectAdapter object) {
        return (Image) object.getObject();
    }

    @Override
    public java.awt.Image getImage(final ObjectAdapter object) {
        return createImage(image(object).getImage());
    }

    @Override
    protected int[][] getPixels(final Object object) {
        return ((Image) object).getImage();
    }

    public Class<?> getValueClass() {
        return Image.class;
    }

    @Override
    public int getWidth(final ObjectAdapter object) {
        return image(object).getWidth();
    }

    @Override
    protected Image setPixels(final int[][] pixels) {
        return new Image(pixels);
    }

    @Override
    public Facet getUnderlyingFacet() {
        return null;
    }

    /**
     * Not required because {@link #alwaysReplace()} is <tt>false</tt>.
     */
    @Override
    public void setUnderlyingFacet(final Facet underlyingFacet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean alwaysReplace() {
        return false;
    }

    @Override
    public boolean isNoop() {
        return false;
    }

    @Override
    public String toString() {
        return "ImageValueSemanticsProvider: ";
    }

    @Override
    public ObjectAdapter createValue(final java.awt.Image image) {
        return getAdapterManager().adapterFor(new Image(grabPixels(image)));
    }

}
