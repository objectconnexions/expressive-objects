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

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.Layout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.IconBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.LineBorder;

public class IconGridViewSpecification extends AbstractCollectionViewSpecification {

    public IconGridViewSpecification() {
        addViewDecorator(new IconBorder.Factory());
    }

    @Override
    protected ViewFactory createElementFactory() {
        return new ViewFactory() {
            @Override
            public View createView(final Content content, final Axes axes, final int sequence) {
                final View icon = new ImageViewSpecification().createView(content, axes, sequence);
                /*
                 * Icon icon = new Icon(content,
                 * IconGridViewSpecification.this); Text textStyle =
                 * Toolkit.getText(ColorsAndFonts.TEXT_NORMAL);
                 * icon.setTitle(new ObjectTitleText(icon, textStyle));
                 * icon.setSelectedGraphic(new IconGraphic(icon, 68));
                 * icon.setUnselectedGraphic(new IconGraphic(icon, 60));
                 * icon.setVertical(true);
                 * 
                 * // return icon;
                 */
                final LineBorder lineBorderedIcon = new LineBorder(icon);
                lineBorderedIcon.setPadding(4);
                lineBorderedIcon.setColor(Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY3));

                // return lineBorderedIcon;

                return new ReplaceViewBorder(lineBorderedIcon);
            }
        };
    }

    @Override
    public Layout createLayout(final Content content, final Axes axes) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.setSize(3);
        return gridLayout;
    }

    @Override
    public String getName() {
        return "Icon Grid";
    }

    /*
     * private static final ObjectSpecification BOOK_SPECIFICATION =
     * ExpressiveObjectsContext.getSpecificationLoader().loadSpecification(
     * "uk.co.objectconnexions.expressiveobjects.example.library.dom.Book"); public boolean
     * canDisplay(ViewRequirement requirement) { return
     * super.canDisplay(requirement) &&
     * requirement.getAdapter().getTypeOfFacet().valueSpec() ==
     * BOOK_SPECIFICATION; }
     */
}
