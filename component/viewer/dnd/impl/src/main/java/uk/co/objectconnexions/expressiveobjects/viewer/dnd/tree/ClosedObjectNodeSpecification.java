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
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.bounded.BoundedFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ObjectContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.SubviewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.SelectObjectBorder;

/**
 * Specification for a tree node that will display a closed object as a root
 * node or within an object. This will indicate that the created view can be
 * opened if: one of it fields is a collection; it is set up to show objects
 * within objects and one of the fields is an object but it is not a lookup.
 * 
 * @see uk.co.objectconnexions.expressiveobjects.viewer.dnd.tree.OpenObjectNodeSpecification for
 *      displaying an open collection as part of an object.
 */
class ClosedObjectNodeSpecification extends NodeSpecification {
    private final boolean showObjectContents;
    private final SubviewDecorator decorator = new SelectObjectBorder.Factory();

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final Where where = Where.ANYWHERE;

    public ClosedObjectNodeSpecification(final boolean showObjectContents) {
        this.showObjectContents = showObjectContents;
    }

    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return requirement.isObject() && requirement.hasReference();
    }

    @Override
    public int canOpen(final Content content) {
        final ObjectAdapter object = ((ObjectContent) content).getObject();
        final List<ObjectAssociation> fields = object.getSpecification().getAssociations(ObjectAssociationFilters.dynamicallyVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where));
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).isOneToManyAssociation()) {
                return CAN_OPEN;
            }

            if (showObjectContents && fields.get(i).isOneToOneAssociation() && !(BoundedFacetUtils.isBoundedSet(object.getSpecification()))) {
                return CAN_OPEN;
            }
        }
        return CANT_OPEN;
    }

    @Override
    protected View createNodeView(final Content content, final Axes axes) {
        View treeLeafNode = new LeafNodeView(content, this);
        treeLeafNode = decorator.decorate(axes, treeLeafNode);
        return treeLeafNode;
    }

    @Override
    public String getName() {
        return "Object tree node - closed";
    }
}
