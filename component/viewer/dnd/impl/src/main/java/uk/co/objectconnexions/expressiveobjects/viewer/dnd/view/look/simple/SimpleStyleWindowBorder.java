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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.look.simple;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Alignment;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Image;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ImageFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Text;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.VerticalAlignment;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewConstants;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewState;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.BorderDrawing;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.text.TextUtils;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.window.WindowControl;

public class SimpleStyleWindowBorder implements BorderDrawing {
	private final static int BORDER_THICKNESS = 6;
    private final static Text TITLE_STYLE = Toolkit.getText(ColorsAndFonts.TEXT_TITLE_SMALL);
    
    private final int titlebarHeight;

    public SimpleStyleWindowBorder() {
    	int controlHeight = WindowControl.HEIGHT + TITLE_STYLE.getDescent();
		int textHeight = TITLE_STYLE.getTextHeight();
		titlebarHeight = Math.max(controlHeight, textHeight) + 2 * ViewConstants.VPADDING;
	}
    
    @Override
    public void debugDetails(final DebugBuilder debug) {
        debug.appendln("titlebar ", titlebarHeight);
    }

    @Override
    public void layoutControls(final Size size, final View[] controls) {
        final int y = titlebarHeight / 2;
        int x = BORDER_THICKNESS + ViewConstants.HPADDING; 
        for (final View control : controls) {
            Size controlSize = control.getRequiredSize(new Size());
			control.setSize(controlSize);
            control.setLocation(new Location(x, y));
            x += control.getSize().getWidth();
        }
    }

    @Override
    public void draw(final Canvas canvas, final Size s, final boolean hasFocus, final ViewState state, final View[] controls, final String title) {
    	final Color titleBarTextColor = hasFocus ? Toolkit.getColor(ColorsAndFonts.COLOR_BLACK) : Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY1);
    	final Color titleBarBackgroundColor = hasFocus ? Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY3) : Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY3);
    	final Color borderColor = hasFocus ? Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY1) : Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY1);
    	
        final int width = s.getWidth();
        final int height = s.getHeight();
        
        int innerWidth = width - BORDER_THICKNESS * 2;
        int innerHeight = height - BORDER_THICKNESS * 2;
        int left = BORDER_THICKNESS;
        int right = left + innerWidth;
        int titleTop = BORDER_THICKNESS;
        int titleBottom = titleTop + titlebarHeight;

        // border
        canvas.drawRoundedRectangle(0, 0, width, height, 8, 8, borderColor);
        canvas.drawRoundedRectangle(BORDER_THICKNESS - 1, BORDER_THICKNESS - 1, innerWidth + 2, innerHeight + 2, 4, 4, borderColor);

        // title bar
        canvas.drawSolidRectangle(left, titleTop, innerWidth, titlebarHeight, titleBarBackgroundColor);
        canvas.drawLine(left, titleBottom, right, titleBottom, borderColor);

        // text
		int baseline = TextUtils.getBaseline(TITLE_STYLE, VerticalAlignment.CENTER, titleTop, titlebarHeight);
        final int controlWidth = ViewConstants.HPADDING + (WindowControl.WIDTH + ViewConstants.HPADDING) * controls.length + ViewConstants.HPADDING;
        final String text = TextUtils.limitText(title, TITLE_STYLE, innerWidth - controlWidth);
        final int textPosition = TextUtils.align(text, TITLE_STYLE, Alignment.LEFT, left + controlWidth, right);
		canvas.drawText(text, textPosition, baseline, titleBarTextColor, TITLE_STYLE);
    }

    @Override
    public void drawTransientMarker(final Canvas canvas, final Size size) {
        final int height = titlebarHeight - 4;
        final int x = size.getWidth() - 50;
        final Image icon = ImageFactory.getInstance().loadIcon("transient", height, null);
        if (icon == null) {
            Text style = Toolkit.getText(ColorsAndFonts.TEXT_NORMAL);
			int baseline = TextUtils.getBaseline(style , VerticalAlignment.CENTER, BORDER_THICKNESS + 1, BORDER_THICKNESS + titlebarHeight);
			canvas.drawText("*", x, baseline, Toolkit.getColor(ColorsAndFonts.COLOR_BLACK), style);
        } else {
            canvas.drawImage(icon, x, BORDER_THICKNESS + 1, height, height);
            // canvas.drawRectangle(x, LINE_THICKNESS + 1, height, height,
            // Color.RED);
        }
    }

    @Override
    public void getRequiredSize(final Size size, final String title, final View[] controls) {
        final int width = getLeft() + ViewConstants.HPADDING + TITLE_STYLE.stringWidth(title) + ViewConstants.HPADDING + 
        		controls.length * (WindowControl.WIDTH + ViewConstants.HPADDING) + ViewConstants.HPADDING + getRight();
        size.ensureWidth(width);
    }

    @Override
    public int getLeft() {
        return BORDER_THICKNESS;
    }

    @Override
    public int getRight() {
        return BORDER_THICKNESS;
    }

    @Override
    public int getTop() {
        return BORDER_THICKNESS + titlebarHeight + 1;
    }

    @Override
    public int getBottom() {
        return BORDER_THICKNESS;
    }

}
