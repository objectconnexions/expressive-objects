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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.look.flat;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Text;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewConstants;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewState;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.BorderDrawing;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.text.TextUtils;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.window.WindowControl;

public class FlatStyleWindowBorder implements BorderDrawing {
    private final static Text TITLE_STYLE = Toolkit.getText(ColorsAndFonts.TEXT_TITLE_SMALL);
    private final int titlebarHeight = Math.max(WindowControl.HEIGHT + ViewConstants.VPADDING + TITLE_STYLE.getDescent(), TITLE_STYLE.getTextHeight());

    @Override
    public void debugDetails(final DebugBuilder debug) {
    }

    @Override
    public void draw(final Canvas canvas, final Size s, final boolean hasFocus, final ViewState state, final View[] controls, final String title) {
        final Color backgroundColor = hasFocus ? Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY1) : Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY2);
        canvas.drawSolidRectangle(0, 0, s.getWidth(), s.getHeight(), backgroundColor);
        final int y = getTop();
        canvas.drawLine(0, y, s.getWidth(), y, backgroundColor);
        final int controlWidth = ViewConstants.HPADDING + (WindowControl.WIDTH + ViewConstants.HPADDING) * controls.length;
        final String text = TextUtils.limitText(title, TITLE_STYLE, s.getWidth() - controlWidth - ViewConstants.VPADDING);
        canvas.drawText(text, 6, TITLE_STYLE.getLineHeight(), backgroundColor, Toolkit.getText(ColorsAndFonts.TEXT_TITLE_SMALL));
    }

    // TODO transiency should be flagged elsewhere and dealt with in the draw
    // method.
    @Override
    public void drawTransientMarker(final Canvas canvas, final Size size) {
    }

    @Override
    public int getBottom() {
        return 1;
    }

    @Override
    public int getLeft() {
        return 1;
    }

    @Override
    public void getRequiredSize(final Size size, final String title, final View[] controls) {
        final int width = getLeft() + ViewConstants.HPADDING + TITLE_STYLE.stringWidth(title) + ViewConstants.HPADDING + controls.length * (WindowControl.WIDTH + ViewConstants.HPADDING) + ViewConstants.HPADDING + getRight();
        size.ensureWidth(width);
    }

    @Override
    public int getRight() {
        return 1;
    }

    @Override
    public int getTop() {
        return titlebarHeight + 5;
    }

    @Override
    public void layoutControls(final Size size, final View[] controls) {
        int x = size.getWidth() - 1 - (WindowControl.WIDTH + ViewConstants.HPADDING) * controls.length;
        final int y = 2 + ViewConstants.VPADDING;
        for (final View control : controls) {
            control.setSize(control.getRequiredSize(new Size()));
            control.setLocation(new Location(x, y));
            x += control.getSize().getWidth() + ViewConstants.HPADDING;
        }
    }

}
