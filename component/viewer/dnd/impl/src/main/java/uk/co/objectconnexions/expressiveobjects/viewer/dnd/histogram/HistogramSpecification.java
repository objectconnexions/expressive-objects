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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.histogram;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.Layout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.collection.CollectionContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CollectionElementBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CompositeViewSpecification;

public class HistogramSpecification extends CompositeViewSpecification {

    static List<? extends ObjectAssociation> availableFields(final CollectionContent content) {
        List<? extends ObjectAssociation> associationList;
        associationList = content.getElementSpecification().getAssociations(new Filter<ObjectAssociation>() {
            @Override
            public boolean accept(final ObjectAssociation t) {
                return NumberAdapters.contains(t);
            }
        });
        return associationList;
    }

    public HistogramSpecification() {
        builder = new CollectionElementBuilder(new ViewFactory() {
            @Override
            public View createView(final Content content, final Axes axes, final int sequence) {
                return new HistogramBar(content, axes.getAxis(HistogramAxis.class), HistogramSpecification.this);
            }
        });
    }

    @Override
    public Layout createLayout(final Content content, final Axes axes) {
        return new HistogramLayout();
    }

    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return requirement.isCollection() && requirement.isOpen() && availableFields((CollectionContent) requirement.getContent()).size() > 0;
    }

    @Override
    public void createAxes(final Content content, final Axes axes) {
        super.createAxes(content, axes);
        axes.add(new HistogramAxis(content));
    }

    @Override
    public String getName() {
        return "Histogram (experimental)";
    }

    @Override
    public boolean isAligned() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isReplaceable() {
        return true;
    }

    @Override
    public boolean isSubView() {
        return false;
    }

}
