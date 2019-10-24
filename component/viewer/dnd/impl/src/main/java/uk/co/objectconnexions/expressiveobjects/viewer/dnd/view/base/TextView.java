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

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Text;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewConstants;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;

public class TextView extends AbstractView {
    private final Text style = Toolkit.getText(ColorsAndFonts.TEXT_NORMAL);
    private final Color color = Toolkit.getColor(ColorsAndFonts.COLOR_BLACK);
    private final String text;

    public TextView(final Content content, final ViewSpecification specification) {
        super(content, specification);
        final ObjectAdapter object = content.getAdapter();
        text = object == null ? "" : object.titleString();
    }

    @Override
    public boolean canFocus() {
        return false;
    }

    @Override
    public void draw(final Canvas canvas) {
        canvas.drawText(text, ViewConstants.HPADDING, getBaseline(), color, style);
    }

    @Override
    public int getBaseline() {
        return style.getAscent() + ViewConstants.VPADDING;
    }

    @Override
    public Size getRequiredSize(final Size maximumSize) {
        final int width = style.stringWidth(text) + ViewConstants.HPADDING * 2;
        final int height = style.getTextHeight() + ViewConstants.VPADDING * 2;
        return new Size(width, height);
    }
}
