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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.grid;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.form.AbstractFormSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.SubviewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.AbstractBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.Layout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.EmptyBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CollectionElementBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.ColumnLayout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CompositeViewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CompositeViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.FieldContent;

public class GridSpecification extends CompositeViewSpecification {

    public GridSpecification() {
        builder = new CollectionElementBuilder(new AbstractFormSpecification() {
            @Override
            public String getName() {
                return "";
            }

            @Override
            protected void init() {
                super.init();
                addSubviewDecorator(new SubviewDecorator() {
                    @Override
                    public ViewAxis createAxis(final Content content) {
                        return null;
                    }

                    @Override
                    public View decorate(final Axes axes, final View view) {
                        return new EmptyBorder(0, 0, 5, 0, view);
                    }
                });
            }
        });

        addViewDecorator(new ColumnLabelBorder.Factory());
    }

    @Override
    public Layout createLayout(final Content content, final Axes axes) {
        return new ColumnLayout(true);
    }

    @Override
    public String getName() {
        return "Grid (experimental)";
    }

    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return requirement.isCollection() && requirement.isOpen();
    }
}

class ColumnLabelBorder extends AbstractBorder {

    public static class Factory implements CompositeViewDecorator {
        @Override
        public View decorate(final View view, final Axes axes) {
            return new ColumnLabelBorder(view);
        }
    }

    protected ColumnLabelBorder(final View view) {
        super(view);
        left = 100;
    }

    @Override
    public void draw(final Canvas canvas) {
        final View subview = getSubviews()[0];

        final int top = subview.getPadding().getTop();
        for (final View view : subview.getSubviews()) {
            final String fieldName = ((FieldContent) view.getContent()).getFieldName();
            canvas.drawText(fieldName + ":", 0, view.getLocation().getY() + top + view.getBaseline(), Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY1), Toolkit.getText(ColorsAndFonts.TEXT_LABEL));
            // canvas.drawRectangle(0, view.getLocation().getY() + top, 80, 10,
            // Toolkit.getColor("primary1"));
        }

        super.draw(canvas);
    }
}
