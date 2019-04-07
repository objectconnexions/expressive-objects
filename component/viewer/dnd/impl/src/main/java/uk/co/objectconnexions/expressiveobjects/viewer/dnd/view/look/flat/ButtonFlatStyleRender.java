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

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Text;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.control.ButtonRender;

public class ButtonFlatStyleRender implements ButtonRender {
    private static final int TEXT_PADDING = 12;
    private static final Text style = Toolkit.getText(ColorsAndFonts.TEXT_CONTROL);
    private final int buttonHeight;

    public ButtonFlatStyleRender() {
        this.buttonHeight = 2 + style.getTextHeight() + 2;
    }

    @Override
    public void draw(final Canvas canvas, final Size size, final boolean isDisabled, final boolean isDefault, final boolean hasFocus, final boolean isOver, final boolean isPressed, final String text) {
        final int buttonWidth = TEXT_PADDING + style.stringWidth(text) + TEXT_PADDING;

        final Color backgroundColor;
        Color textColor = Toolkit.getColor(ColorsAndFonts.COLOR_BLACK);
        if (isDisabled) {
            backgroundColor = textColor = Toolkit.getColor(ColorsAndFonts.COLOR_MENU_DISABLED);
        } else if (isDefault) {
            backgroundColor = Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY1);
        } else if (isOver || hasFocus) {
            backgroundColor = Toolkit.getColor(ColorsAndFonts.COLOR_BLACK);
        } else {
            backgroundColor = Toolkit.getColor(ColorsAndFonts.COLOR_BLACK);
        }
        canvas.drawSolidRectangle(0, 0, buttonWidth, buttonHeight, backgroundColor);
        canvas.drawText(text, TEXT_PADDING, buttonHeight / 2 + style.getMidPoint(), textColor, style);
    }

    @Override
    public Size getMaximumSize(final String text) {
        final int buttonWidth = TEXT_PADDING + Toolkit.getText(ColorsAndFonts.TEXT_CONTROL).stringWidth(text) + TEXT_PADDING;
        return new Size(buttonWidth, buttonHeight);
    }
}
