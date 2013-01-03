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

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.AbstractViewBuilder;

public class SelectionListBuilder extends AbstractViewBuilder {
    private final ViewFactory subviewSpecification;

    public SelectionListBuilder(final ViewFactory subviewSpecification) {
        this.subviewSpecification = subviewSpecification;
    }

    @Override
    public void build(final View view, final Axes axes) {
        final Content content = view.getContent();
        final ObjectAdapter[] options = content.getOptions();

        // TODO sort list
        for (int i = 0; i < options.length; i++) {
            final Content subContent = new OptionContent(options[i]);
            final View subview = subviewSpecification.createView(subContent, axes, i);
            view.addView(subview);
        }
    }

    @Override
    public boolean canDragView() {
        return false;
    }

}
