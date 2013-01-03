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

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacetUtils;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.collection.CollectionContent;

/**
 * Specification for a tree node that will display a closed collection as a root
 * node or within an object.
 * 
 * @see uk.co.objectconnexions.expressiveobjects.viewer.dnd.tree.OpenCollectionNodeSpecification for
 *      displaying an open collection within an object.
 */
public class ClosedCollectionNodeSpecification extends NodeSpecification {
    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return requirement.isCollection() && requirement.hasReference();
    }

    @Override
    public int canOpen(final Content content) {
        final ObjectAdapter collection = ((CollectionContent) content).getCollection();
        if (collection.isGhost()) {
            return UNKNOWN;
        } else {
            final CollectionFacet facet = CollectionFacetUtils.getCollectionFacetFromSpec(collection);
            return facet.size(collection) > 0 ? CAN_OPEN : CANT_OPEN;
        }
    }

    @Override
    protected View createNodeView(final Content content, final Axes axes) {
        final View treeLeafNode = new LeafNodeView(content, this);
        return treeLeafNode;
    }

    @Override
    public String getName() {
        return "Collection tree node - closed";
    }
}
