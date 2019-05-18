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

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ToString;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Bounds;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Image;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ImageFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Text;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;

/*
 *  TODO why does this pass out the baseline, and then expect it back when doing the drawing?
 */
public class IconGraphic {
    private final int baseline;
    protected final Content content;
    protected Image icon;
    protected final int iconHeight;
    private String lastIconName;

    public IconGraphic(final View view, final int height, final int baseline) {
        content = view.getContent();
        iconHeight = height;
        this.baseline = baseline;
    }

    public IconGraphic(final View view, final int height) {
        content = view.getContent();
        iconHeight = height;
        baseline = height * 80 / 100;
    }

    public IconGraphic(final View view, final Text style) {
        content = view.getContent();
        iconHeight = (int) (style.getTextHeight() * 0.9);
        this.baseline = style.getAscent();
    }

    public void draw(final Canvas canvas, final int x, final int baseline) {
        final int y = baseline - this.baseline;
        if (Toolkit.debug) {
            canvas.drawDebugOutline(new Bounds(new Location(x, y), getSize()), getBaseline(), Toolkit.getColor(ColorsAndFonts.COLOR_DEBUG_BOUNDS_DRAW));
        }
        final Image icon = icon();
        if (icon == null) {
            canvas.drawSolidOval(x + 1, y, iconHeight, iconHeight, Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY3));
        } else {
            canvas.drawImage(icon, x + 1, y);
        }
    }

    public int getBaseline() {
        return baseline;
    }

    public Size getSize() {
        final Image icon = icon();
        final int iconWidth = icon == null ? iconHeight : icon.getWidth();
        return new Size(iconWidth + 2, iconHeight + 2);
    }

    protected Image icon() {
        final String iconName = content.getIconName();
        /*
         * If the graphic is based on a name provided by the object then the
         * icon could be changed at any time, so we won't lazily load it.
         */
        if (icon != null && (iconName == null || iconName.equals(lastIconName))) {
            return icon;
        }
        lastIconName = iconName;
        if (iconName != null) {
            icon = ImageFactory.getInstance().loadIcon(iconName, iconHeight, null);
        }
        if (icon == null) {
            icon = content.getIconPicture(iconHeight);
        }
        if (icon == null) {
            icon = ImageFactory.getInstance().loadDefaultIcon(iconHeight, null);
        }
        return icon;
    }

    @Override
    public String toString() {
        final ToString str = new ToString(this);
        str.append("baseline", baseline);
        str.append("icon", icon);
        return str.toString();
    }
}
