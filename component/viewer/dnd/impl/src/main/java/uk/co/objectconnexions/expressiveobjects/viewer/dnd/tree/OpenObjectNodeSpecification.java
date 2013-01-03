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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.tree;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.SubviewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.Layout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.SelectObjectBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.ObjectFieldBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.StackLayout;

/**
 * Specification for a tree node that will display an open object as a root node
 * or within an object.
 * 
 * @see uk.co.objectconnexions.expressiveobjects.viewer.dnd.tree.ClosedObjectNodeSpecification for
 *      displaying a closed collection within an object.
 */
public class OpenObjectNodeSpecification extends CompositeNodeSpecification {

    private final SubviewDecorator decorator = new SelectObjectBorder.Factory();

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final Where where = Where.ANYWHERE;

    public OpenObjectNodeSpecification() {
        builder = new ObjectFieldBuilder(this);
    }

    @Override
    public Layout createLayout(final Content content, final Axes axes) {
        return new StackLayout();
    }

    /**
     * This is only used to control root nodes. Therefore a object tree can only
     * be displayed for an object with fields that are collections.
     */
    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        if (requirement.isObject() && requirement.hasReference()) {
            final ObjectAdapter object = requirement.getAdapter();
            final List<ObjectAssociation> fields = object.getSpecification().getAssociations(ObjectAssociationFilters.dynamicallyVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where));
            for (int i = 0; i < fields.size(); i++) {
                if (fields.get(i).isOneToManyAssociation()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected View createNodeView(final Content content, final Axes axes) {
        return decorator.decorate(axes, super.createNodeView(content, axes));
    }

    @Override
    public int canOpen(final Content content) {
        return CAN_OPEN;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isSubView() {
        return false;
    }

    @Override
    public String getName() {
        return "Object tree node - open";
    }
}
