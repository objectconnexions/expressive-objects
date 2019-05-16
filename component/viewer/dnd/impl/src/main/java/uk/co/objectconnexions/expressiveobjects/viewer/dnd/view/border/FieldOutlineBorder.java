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
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Text;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.AbstractBorder;

/**
 * A line border draws a simple box around a view of a given width and color.
 */
public class FieldOutlineBorder extends AbstractBorder {
    private Color color;
    private final int arcRadius;
    private int width;
    private int padding;
	private String title;
	private final int topInset;

    public FieldOutlineBorder(final String title, final View wrappedView) {
        this(title, 1, wrappedView);
    }

    public FieldOutlineBorder(final String title, final int width, final View wrappedView) {
        this(title, width, 0, Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY2), wrappedView);
    }

    public FieldOutlineBorder(final String title, final int width, final int arcRadius, final View wrappedView) {
        this(title, width, arcRadius, Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY2), wrappedView);
    }

    public FieldOutlineBorder(final String title, final Color color, final View wrappedView) {
        this(title, 1, 0, color, wrappedView);
    }

    public FieldOutlineBorder(final String title, final int width, final Color color, final View wrappedView) {
        this(title, width, 0, color, wrappedView);
    }

    public FieldOutlineBorder(final String title, final int width, final int arcRadius, final Color color, final View wrappedView) {
        super(wrappedView);
		this.title = title;
		this.width = width;
        this.arcRadius = arcRadius;
        this.color = color;
        
        padding = 5;
        
        Text font = Toolkit.getText(ColorsAndFonts.TEXT_LABEL);
        top = topInset = font.getAscent();
        
        calculateBorderWidth();
    }

    @Override
    protected void debugDetails(final DebugBuilder debug) {
        debug.append("FieldOutlineBorder " + top + " pixels\n");
    }

    @Override
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        final Size s = getSize();
        final int width = s.getWidth();
        final Text font = Toolkit.getText(ColorsAndFonts.TEXT_LABEL);
        final Color background = Toolkit.getColor(ColorsAndFonts.COLOR_WINDOW);
        int y = font.getAscent() / 2;
        int textWidth = font.stringWidth(title);
        int lineX1 = padding + this.width - 2;
        int lineX2 = padding + this.width + textWidth + 4;
        for (int x = 0; x < this.width; x++) {
        	int rectangleWidth = width - 2 * x;
            int rectangleHeight = s.getHeight() - x - y;
			canvas.drawRoundedRectangle(x, y, rectangleWidth, rectangleHeight, arcRadius, arcRadius, color);
			canvas.drawLine(lineX1, y, lineX2, y, background);
            y++;
        }
        canvas.drawText(title, padding + this.width, topInset, color, font);
    }

    @Override
    public String toString() {
        return wrappedView.toString() + "/LineBorder";
    }

    public void setWidth(final int width) {
        this.width = width;
        calculateBorderWidth();
    }

    public void setPadding(final int padding) {
        this.padding = padding;
        calculateBorderWidth();
    }

    private void calculateBorderWidth() {
    	final Text font = Toolkit.getText(ColorsAndFonts.TEXT_LABEL);
        top = width + padding + font.getTextHeight();
        left = width + padding;
        bottom = width + padding;
        right = width + padding;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

}
