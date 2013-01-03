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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.lookup;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Bounds;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Click;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewConstants;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.AbstractViewDecorator;

class SelectionItemSelector extends AbstractViewDecorator {

    private final SelectionListAxis axis;

    protected SelectionItemSelector(final View wrappedView, final SelectionListAxis axis) {
        super(wrappedView);
        this.axis = axis;
    }

    @Override
    public void draw(final Canvas canvas) {
        if (getState().isViewIdentified()) {
            final Color color = Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY3);
            final Bounds bounds = getBounds();
            canvas.drawSolidRectangle(0, 0, bounds.getWidth(), bounds.getHeight(), color);
        }
        canvas.offset(ViewConstants.HPADDING, 0);
        super.draw(canvas);
    }

    @Override
    public void entered() {
        getState().setContentIdentified();
        getState().setViewIdentified();
        wrappedView.entered();
        markDamaged();
    }

    @Override
    public void exited() {
        getState().clearObjectIdentified();
        getState().clearViewIdentified();
        wrappedView.exited();
        markDamaged();
    }

    @Override
    public void firstClick(final Click click) {
        axis.setSelection((OptionContent) getContent());
        final View view = axis.getOriginalView();
        view.getParent().updateView();
        view.getParent().invalidateContent();
    }

    @Override
    public Size getRequiredSize(final Size maximumSize) {
        final Size size = super.getRequiredSize(maximumSize);
        size.extendWidth(ViewConstants.HPADDING * 2);
        return size;
    }
}
