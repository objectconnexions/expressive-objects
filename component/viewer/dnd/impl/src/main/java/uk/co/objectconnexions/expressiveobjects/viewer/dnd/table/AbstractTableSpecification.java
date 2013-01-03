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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.table;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.Layout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.collection.CollectionContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CollectionElementBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CompositeViewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CompositeViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.StackLayout;

public abstract class AbstractTableSpecification extends CompositeViewSpecification {

    public AbstractTableSpecification() {
        builder = new CollectionElementBuilder(new ViewFactory() {
            TableRowSpecification rowSpecification = new TableRowSpecification();

            // TODO do directly without specification
            @Override
            public View createView(final Content content, final Axes axes, final int sequence) {
                // ViewSpecification rowSpecification = new
                // SubviewIconSpecification();
                return rowSpecification.createView(content, axes, -1);
            }
        });

        addSubviewDecorator(new TableRowBorder.Factory());

        addViewDecorator(new CompositeViewDecorator() {
            @Override
            public View decorate(final View view, final Axes axes) {
                axes.getAxis(TableAxis.class).setRoot(view);
                return view;
            }
        });
    }

    @Override
    public Layout createLayout(final Content content, final Axes axes) {
        return new StackLayout();
    }

    @Override
    public void createAxes(final Content content, final Axes axes) {
        axes.add(new TableAxisImpl((CollectionContent) content), TableAxis.class);
    }

    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        if (!requirement.isCollection() || !requirement.isOpen()) {
            return false;
        } else {
            final CollectionContent collectionContent = (CollectionContent) requirement.getContent();
            final ObjectSpecification elementSpecification = collectionContent.getElementSpecification();
            final List<ObjectAssociation> fields = elementSpecification.getAssociations(ObjectAssociationFilters.WHEN_VISIBLE_IRRESPECTIVE_OF_WHERE);
            for (int i = 0; i < fields.size(); i++) {
                if (fields.get(i).isOneToOneAssociation()) {
                    return true;
                }
            }
            return false;
        }
    }

    /*
     * protected View decorateView(View view) { TableAxis tableAxis =
     * (TableAxis) view.getViewAxisForChildren(); tableAxis.setRoot(view);
     * return view; // return doCreateView(table, content, axis); } protected
     * abstract View doCreateView(final View table, final Content content, final
     * ViewAxis axis);
     */

    @Override
    public String getName() {
        return "Standard Table";
    }

    @Override
    public boolean isReplaceable() {
        return false;
    }

    @Override
    public boolean isResizeable() {
        return true;
    }
}
