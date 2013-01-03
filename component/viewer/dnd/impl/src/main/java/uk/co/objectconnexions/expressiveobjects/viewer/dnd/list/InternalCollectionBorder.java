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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.list;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToManyAssociation;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ObjectContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Placement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.UserActionSet;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewState;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.AbstractBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.IconGraphic;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CompositeViewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.field.OneToManyField;

public class InternalCollectionBorder extends AbstractBorder {
    public static class Factory implements CompositeViewDecorator {
        @Override
        public View decorate(final View child, final Axes axes) {
            return new InternalCollectionBorder(child);
        }
    }

    private final IconGraphic icon;

    protected InternalCollectionBorder(final View wrappedView) {
        super(wrappedView);

        icon = new InternalCollectionIconGraphic(this, Toolkit.getText(ColorsAndFonts.TEXT_NORMAL));
        left = icon.getSize().getWidth();
    }

    @Override
    protected void debugDetails(final DebugBuilder debug) {
        debug.append("InternalCollectionBorder ");
    }

    @Override
    public Size getRequiredSize(final Size maximumSize) {
        final Size size = super.getRequiredSize(maximumSize);
        size.ensureWidth(left + 45 + right);
        size.ensureHeight(24);
        return size;
    }

    @Override
    public void draw(final Canvas canvas) {
        icon.draw(canvas, 0, getBaseline());

        final ObjectAdapter collection = getContent().getAdapter();
        final CollectionFacet facet = CollectionFacetUtils.getCollectionFacetFromSpec(collection);
        final ViewState state = getState();
        final Color color;
        if (state.canDrop()) {
            color = Toolkit.getColor(ColorsAndFonts.COLOR_VALID);
        } else if (state.cantDrop()) {
            color = Toolkit.getColor(ColorsAndFonts.COLOR_INVALID);
        } else {
            color = Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY2);
        }
        if (collection == null || facet.size(collection) == 0) {
            canvas.drawText("empty", left, getBaseline(), color, Toolkit.getText(ColorsAndFonts.TEXT_NORMAL));
        } else {
            final int x = icon.getSize().getWidth() / 2;
            final int x2 = x + 4;
            final int y = icon.getSize().getHeight() + 1;
            final int y2 = getSize().getHeight() - 5;
            canvas.drawLine(x, y, x, y2, color);
            canvas.drawLine(x, y2, x2, y2, color);
        }
        super.draw(canvas);
    }

    @Override
    public void contentMenuOptions(final UserActionSet options) {
        super.contentMenuOptions(options);
        // final ObjectSpecification specification = ((OneToManyField)
        // getContent()).getSpecification();
        // OptionFactory.addCreateOptions(specification, options);
    }

    @Override
    public void objectActionResult(final ObjectAdapter result, final Placement placement) {
        // same as in TreeNodeBorder
        final OneToManyField internalCollectionContent = (OneToManyField) getContent();
        final OneToManyAssociation field = internalCollectionContent.getOneToManyAssociation();
        final ObjectAdapter target = ((ObjectContent) getParent().getContent()).getObject();

        final Consent valid = field.isValidToAdd(target, result);
        if (valid.isAllowed()) {
            field.addElement(target, result);
        }
        super.objectActionResult(result, placement);
    }

    @Override
    public String toString() {
        return "InternalCollectionBorder/" + wrappedView;
    }
}
