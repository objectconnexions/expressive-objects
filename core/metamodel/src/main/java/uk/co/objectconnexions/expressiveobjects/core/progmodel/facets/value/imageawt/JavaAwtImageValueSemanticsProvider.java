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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.imageawt;

import java.awt.Image;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.image.ImageValueSemanticsProviderAbstract;

public class JavaAwtImageValueSemanticsProvider extends ImageValueSemanticsProviderAbstract<Image> {

    public JavaAwtImageValueSemanticsProvider(final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(holder, Image.class, configuration, context);
    }

    @Override
    public int getHeight(final ObjectAdapter object) {
        return image(object).getHeight(null);
    }

    private Image image(final ObjectAdapter object) {
        return (Image) object.getObject();
    }

    @Override
    public Image getImage(final ObjectAdapter object) {
        return image(object);
    }

    @Override
    protected int[][] getPixels(final Object object) {
        return grabPixels((Image) object);
    }

    public Class<?> getValueClass() {
        return Image.class;
    }

    @Override
    public int getWidth(final ObjectAdapter object) {
        return image(object).getWidth(null);
    }

    @Override
    protected Image setPixels(final int[][] pixels) {
        return createImage(pixels);
    }

    @Override
    public boolean isNoop() {
        return false;
    }

    @Override
    public String toString() {
        return "JavaAwtImageValueSemanticsProvider: ";
    }

    @Override
    public ObjectAdapter createValue(final Image image) {
        return getAdapterManager().adapterFor(image);
    }

}
