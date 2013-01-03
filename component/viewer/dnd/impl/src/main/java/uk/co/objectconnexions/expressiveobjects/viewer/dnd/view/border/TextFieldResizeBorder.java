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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border;

import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Shape;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.util.Properties;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;

public class TextFieldResizeBorder extends ResizeBorder {
    public static final int BORDER_WIDTH = ExpressiveObjectsContext.getConfiguration().getInteger(Properties.PROPERTY_BASE + "field-resize-border", 5);

    public TextFieldResizeBorder(final View view) {
        super(view, RIGHT + DOWN, 1, 1);
    }

    @Override
    protected void drawResizeBorder(final Canvas canvas, final Size size) {
        if (resizing) {
            final Shape shape = new Shape(0, 0);
            final int resizeMarkerSize = 10;
            shape.addVector(resizeMarkerSize, 0);
            shape.addVector(0, resizeMarkerSize);
            shape.addVector(-resizeMarkerSize, -resizeMarkerSize);
            final Color color = Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY3);
            final int height = size.getHeight();
            final int width = size.getWidth();
            canvas.drawSolidShape(shape, width - resizeMarkerSize, height, color);
            canvas.drawRectangle(0, 0, width, height, color);
        }
    }
}
