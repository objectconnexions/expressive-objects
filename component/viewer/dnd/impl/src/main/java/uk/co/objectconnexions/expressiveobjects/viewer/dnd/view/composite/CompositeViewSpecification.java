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

import java.util.ArrayList;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.Options;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.util.Properties;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ObjectContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.SubviewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.Layout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.window.SubviewFocusManager;

public abstract class CompositeViewSpecification implements ViewSpecification {
    protected ViewBuilder builder;
    private final List<CompositeViewDecorator> viewDecorators = new ArrayList<CompositeViewDecorator>();

    @Override
    public final View createView(final Content content, final Axes axes, final int sequence) {
        resolveObject(content);

        createAxes(content, axes);
        builder.createAxes(axes, content);
        final Layout layout = createLayout(content, axes);
        if (layout instanceof ViewAxis) {
            axes.add((ViewAxis) layout);
        }

        final CompositeViewUsingBuilder view = new CompositeViewUsingBuilder(content, this, axes, layout, builder);
        view.setCanDragView(builder.canDragView());
        final View decoratedView = decorateView(view, view.getViewAxes());
        final Options options = Properties.getViewConfigurationOptions(this);
        decoratedView.loadOptions(options);
        return decoratedView;
    }

    private void resolveObject(final Content content) {
        if (content instanceof ObjectContent) {
            final ObjectAdapter object = ((ObjectContent) content).getObject();
            if (object != null && !object.isResolved()) {
                ExpressiveObjectsContext.getPersistenceSession().resolveImmediately(object);
            }
        }
    }

    private View decorateView(final View view, final Axes axes) {
        view.setFocusManager(new SubviewFocusManager(view));
        View decorated = view;
        for (final CompositeViewDecorator decorator : viewDecorators) {
            decorated = decorator.decorate(decorated, axes);
        }
        return decorated;
    }

    public void addViewDecorator(final CompositeViewDecorator decorator) {
        viewDecorators.add(decorator);
    }

    public void addSubviewDecorator(final SubviewDecorator decorator) {
        builder.addSubviewDecorator(decorator);
    }

    protected abstract Layout createLayout(Content content, Axes axes);

    protected void createAxes(final Content content, final Axes axes) {
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

    @Override
    public boolean isAligned() {
        return false;
    }

    @Override
    public boolean isResizeable() {
        return false;
    }

}
