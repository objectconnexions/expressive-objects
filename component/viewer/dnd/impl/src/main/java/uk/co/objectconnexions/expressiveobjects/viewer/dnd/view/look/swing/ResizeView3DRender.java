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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.look.swing;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.DrawingUtil;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.ResizeViewRender;

public class ResizeView3DRender implements ResizeViewRender {

    @Override
    public void draw(final Canvas canvas, final int x, final int width, final int height, final boolean hasFocus) {
        final Color borderColor = hasFocus ? Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY3) : Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY3);
        canvas.drawSolidRectangle(x, 0, width, height, borderColor);

        final Color secondary2 = Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY2);
        canvas.drawRectangle(x, -1, width, height + 2, secondary2);

        int h1 = 30;
        int h2 = 10;
        if (height < h1 + h2 * 2) {
            h1 = Math.min(0, height - h2 * 2);
        } else {
            h2 = (height - h1) / 2;
        }

        final Color color = hasFocus ? Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY2) : secondary2;
        DrawingUtil.drawHatching(canvas, x + 1, h2, width - 2, h1, color, Toolkit.getColor(ColorsAndFonts.COLOR_WHITE));
    }

}
