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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Image;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;

public class AwtImage implements Image {
    java.awt.Image iconImage;

    public AwtImage(final java.awt.Image iconImage) {
        if (iconImage == null) {
            throw new NullPointerException();
        }

        this.iconImage = iconImage;
    }

    @Override
    public int getHeight() {
        return iconImage.getHeight(null);
    }

    @Override
    public int getWidth() {
        return iconImage.getWidth(null);
    }

    @Override
    public Size getSize() {
        return new Size(getWidth(), getHeight());
    }

    public java.awt.Image getAwtImage() {
        return iconImage;
    }
}
