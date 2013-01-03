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


package uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.table;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Click;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Drag;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.DragStart;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.InternalDrag;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.SimpleInternalDrag;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.simple.AbstractView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.AwtColor;


public class TestHeaderView extends AbstractView {

    private int requiredWidth;
    private int requiredHeight;

    public TestHeaderView(final ViewAxis axis, final int width, final int height) {
        super(null, null, axis);
        setMaximumSize(new Size(width, height));
    }

    public void draw(final Canvas canvas) {
        super.draw(canvas);
        int width = getSize().getWidth();
        int height = getSize().getHeight();
        canvas.clearBackground(this, AwtColor.GRAY);
        canvas.drawRectangle(0, 0, width - 1, height - 1, AwtColor.BLUE);
        canvas.drawLine(0, 0, width - 1, height - 1, AwtColor.ORANGE);
        canvas.drawLine(width - 1, 0, 0, height - 1, AwtColor.ORANGE);
    }

    public Size getRequiredSize(final Size maximumSize) {
        return new Size(requiredWidth, requiredHeight);
    }

    public void setMaximumSize(final Size size) {
        requiredHeight = size.getHeight();
        requiredWidth = size.getWidth();

        setSize(size);
    }

    public void firstClick(final Click click) {
        debug("first click " + click);
    }

    public void secondClick(final Click click) {
        debug("second click " + click);
    }

    public void mouseMoved(final Location location) {
        debug("mouse moved " + location);
    }

    private void debug(final String str) {
        getViewManager().getSpy().addAction(str);
    }

    public Drag dragStart(final DragStart drag) {
        debug("drag start in header " + drag);
        return new SimpleInternalDrag(getParent(), drag.getLocation());
    }

    public void drag(final InternalDrag drag) {
        debug("drag in header " + drag);
        super.drag(drag);
    }

    public void dragTo(final InternalDrag drag) {
        debug("drag to in header " + drag);
    }
}
