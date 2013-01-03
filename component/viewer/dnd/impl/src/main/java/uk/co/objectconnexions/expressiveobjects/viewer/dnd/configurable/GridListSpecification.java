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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.configurable;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.GlobalViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.Layout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CollectionElementBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CompositeViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.GridLayout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.GridLayoutControlBorder;

public class GridListSpecification extends CompositeViewSpecification implements ViewFactory {

    protected static class ElementFactory implements ViewFactory {
        @Override
        public View createView(final Content content, final Axes axes, final int sequence) {
            final GlobalViewFactory factory = Toolkit.getViewFactory();

            final ViewSpecification elementSpecification = axes.getAxis(ConfigurationAxis.class).getElementSpecification();
            if (elementSpecification == null) {
                final int defaultRequirement = ViewRequirement.CLOSED | ViewRequirement.SUBVIEW;
                final ViewRequirement viewRequirement = new ViewRequirement(content, defaultRequirement);
                return factory.createView(viewRequirement);
            } else {
                return elementSpecification.createView(content, axes, sequence);
            }
        }
    }

    public GridListSpecification() {
        final ElementFactory factory = new ElementFactory();
        builder = new CollectionElementBuilder(factory);
        // TODO allow to be switched on so that user can change the view for a
        // single element. This type of
        // view used for an element would not be stored.
        if (false) {
            addSubviewDecorator(new ConfigurableFieldBorder.Factory());
        }

        addViewDecorator(new ConfigurableCompositeViewBorder.Factory(factory));
        addViewDecorator(new GridLayoutControlBorder.Factory());
    }

    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return requirement.isCollection() && requirement.isOpen() && !requirement.isSubview() && requirement.isDesign();
    }

    @Override
    public String getName() {
        return "Grid List";
    }

    @Override
    public Layout createLayout(final Content content, final Axes axes) {
        return new GridLayout();
    }
}
