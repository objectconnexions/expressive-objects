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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Bounds;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.form.FormSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Click;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.AbstractBorder;

public class ReplaceViewBorder extends AbstractBorder {

    protected ReplaceViewBorder(final View view) {
        super(view);
    }

    @Override
    public void draw(final Canvas canvas) {
        super.draw(canvas);

        final Bounds b = getButtonBounds();
        canvas.drawRoundedRectangle(b.getX(), b.getY(), b.getWidth(), b.getHeight(), 6, 6, Toolkit.getColor(0xfff));
    }

    @Override
    public void firstClick(final Click click) {
        if (getButtonBounds().contains(click.getLocation())) {
            final View view = new FormSpecification().createView(getContent(), new Axes(), 0);
            getWorkspace().replaceView(getParent(), view);
        }
    }

    private Bounds getButtonBounds() {
        final int x = getSize().getWidth() - 28;
        return new Bounds(x, 8, 20, 16);
    }
}
