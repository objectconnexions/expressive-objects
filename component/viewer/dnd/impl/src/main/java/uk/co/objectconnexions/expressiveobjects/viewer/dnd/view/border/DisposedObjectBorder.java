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

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.DragEvent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.DragStart;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.AbstractBorder;

public class DisposedObjectBorder extends AbstractBorder {

    public DisposedObjectBorder(final int size, final View wrappedView) {
        super(wrappedView);
        top = size;
        left = size;
        bottom = size;
        right = size;
    }

    public DisposedObjectBorder(final View wrappedView) {
        this(2, wrappedView);
    }

    @Override
    protected void debugDetails(final DebugBuilder debug) {
        debug.append("DisposedObjectBorder " + top + " pixels");
    }

    @Override
    public DragEvent dragStart(final DragStart drag) {
        return super.dragStart(drag);
    }

    @Override
    public void draw(final Canvas canvas) {
        super.draw(canvas);

        Color color = null;
        color = Toolkit.getColor(ColorsAndFonts.COLOR_INVALID);
        final Size s = getSize();

        final int w = s.getWidth();
        final int xExtent = s.getWidth() - left;
        for (int i = 0; i < left; i++) {
            canvas.drawRectangle(i, i, xExtent - 2 * i, s.getHeight() - 2 * i, color);
        }
        for (int i = 0; i < 15; i++) {
            canvas.drawLine(left, top + i, left + i, top, color);
            canvas.drawLine(w - left - right - 1, s.getHeight() - top - i - 1, w - left - right - i - 1, s.getHeight() - top - 1, color);
        }
    }

    @Override
    public void entered() {
        wrappedView.entered();
        getFeedbackManager().setError("Destroyed objects cannot be used");
        markDamaged();
    }

    @Override
    public void exited() {
        wrappedView.exited();
        getFeedbackManager().setError("");
        markDamaged();
    }

    @Override
    public String toString() {
        return wrappedView.toString() + "/DisposedObjectBorder [" + getSpecification() + "]";
    }
}
