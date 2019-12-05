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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.help;

import uk.co.objectconnexions.expressiveobjects.core.runtime.about.AboutExpressiveObjects;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Image;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ImageFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Text;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Click;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.FocusManager;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.AbstractView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.NullContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.text.TextUtils;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.window.SubviewFocusManager;

public class AboutView extends AbstractView {
    private static final int MAX_WIDTH = 300;
    private final int linePadding = -2;
    private final int noticePadding = 45;
    private final int margin = 14;
    private final Image image;
    private final int left;
    private final FocusManager focusManager;

    public AboutView() {
        super(new NullContent());
        image = ImageFactory.getInstance().loadImage(AboutExpressiveObjects.getImageName());
        left = noticePadding;
        setContent(new NullContent(AboutExpressiveObjects.getFrameworkName()));

        focusManager = new SubviewFocusManager(this);
    }

    @Override
    public FocusManager getFocusManager() {
        return focusManager;
    }

    @Override
    public void draw(final Canvas canvas) {
        super.draw(canvas);

        final Text titleStyle = Toolkit.getText(ColorsAndFonts.TEXT_TITLE);
        final Text normalStyle = Toolkit.getText(ColorsAndFonts.TEXT_LABEL);
        final Color color = Toolkit.getColor(ColorsAndFonts.COLOR_BLACK);

        clearBackground(canvas, Toolkit.getColor(ColorsAndFonts.COLOR_WHITE));
        canvas.drawRectangleAround(getBounds(), Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY3));

        if (showingImage()) {
            canvas.drawImage(image, margin, margin);
        }

        int line = margin + image.getHeight() + noticePadding + normalStyle.getAscent();

        // application details
        String text = AboutExpressiveObjects.getApplicationName();
        if (text != null) {
            canvas.drawText(text, left, line, MAX_WIDTH, color, titleStyle);
            line += TextUtils.stringHeight(text, titleStyle, MAX_WIDTH) + titleStyle.getLineSpacing() + linePadding;
        }
        text = AboutExpressiveObjects.getApplicationCopyrightNotice();
        if (text != null) {
            canvas.drawText(text, left, line, MAX_WIDTH, color, normalStyle);
            line += TextUtils.stringHeight(text, normalStyle, MAX_WIDTH) + normalStyle.getLineSpacing() + linePadding;
        }
        text = AboutExpressiveObjects.getApplicationVersion();
        if (text != null) {
            canvas.drawText(text, left, line, MAX_WIDTH, color, normalStyle);
            line += TextUtils.stringHeight(text, normalStyle, MAX_WIDTH) + normalStyle.getLineSpacing() + linePadding;
            line += 2 * normalStyle.getLineHeight();
        }

        // framework details
        text = AboutExpressiveObjects.getFrameworkName();
        canvas.drawText(text, left, line, MAX_WIDTH, color, titleStyle);
        line += TextUtils.stringHeight(text, titleStyle, MAX_WIDTH) + titleStyle.getLineSpacing() + linePadding;

        text = AboutExpressiveObjects.getFrameworkCopyrightNotice();
        canvas.drawText(text, left, line, MAX_WIDTH, color, normalStyle);
        line += TextUtils.stringHeight(text, normalStyle, MAX_WIDTH) + normalStyle.getLineSpacing() + linePadding;

        canvas.drawText(frameworkVersion(), left, line, MAX_WIDTH, color, normalStyle);

    }

    private String frameworkVersion() {
        return AboutExpressiveObjects.getFrameworkVersion();
    }

    private boolean showingImage() {
        return image != null;
    }

    @Override
    public Size getRequiredSize(final Size availableSpace) {
        final Text titleStyle = Toolkit.getText(ColorsAndFonts.TEXT_TITLE);
        final Text normalStyle = Toolkit.getText(ColorsAndFonts.TEXT_LABEL);

        int height = 0;

        String text = AboutExpressiveObjects.getFrameworkName();
        height += TextUtils.stringHeight(text, titleStyle, MAX_WIDTH) + titleStyle.getLineSpacing() + linePadding;
        int width = TextUtils.stringWidth(text, titleStyle, MAX_WIDTH);

        text = AboutExpressiveObjects.getFrameworkCopyrightNotice();
        height += TextUtils.stringHeight(text, normalStyle, MAX_WIDTH) + normalStyle.getLineSpacing() + linePadding;
        width = Math.max(width, TextUtils.stringWidth(text, normalStyle, MAX_WIDTH));

        text = frameworkVersion();
        height += TextUtils.stringHeight(text, normalStyle, MAX_WIDTH) + normalStyle.getLineSpacing() + linePadding;
        width = Math.max(width, TextUtils.stringWidth(text, normalStyle, MAX_WIDTH));

        text = AboutExpressiveObjects.getApplicationName();
        if (text != null) {
            height += TextUtils.stringHeight(text, titleStyle, MAX_WIDTH) + titleStyle.getLineSpacing() + linePadding;
            width = Math.max(width, TextUtils.stringWidth(text, titleStyle, MAX_WIDTH));
        }
        text = AboutExpressiveObjects.getApplicationCopyrightNotice();
        if (text != null) {
            height += TextUtils.stringHeight(text, normalStyle, MAX_WIDTH) + normalStyle.getLineSpacing() + linePadding;
            width = Math.max(width, TextUtils.stringWidth(text, normalStyle, MAX_WIDTH));
        }
        text = AboutExpressiveObjects.getApplicationVersion();
        if (text != null) {
            height += TextUtils.stringHeight(text, normalStyle, MAX_WIDTH) + normalStyle.getLineSpacing() + linePadding;
            width = Math.max(width, TextUtils.stringWidth(text, normalStyle, MAX_WIDTH));
        }

        height += noticePadding;

        if (showingImage()) {
            height += image.getHeight();
            width = Math.max(image.getWidth(), width);
        }

        return new Size(margin + width + margin, margin + height + margin);
    }

    @Override
    public void firstClick(final Click click) {
        // dispose();
    }
}
