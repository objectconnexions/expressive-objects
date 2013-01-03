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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.tree2;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.form.ExpandableViewBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.icon.IconElementFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.icon.SubviewIconSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.IconBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.SelectObjectBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.AbstractCollectionViewSpecification;

public class CollectionTreeNodeSpecification extends AbstractCollectionViewSpecification {

    public static ViewSpecification[] create() {
        final CollectionTreeNodeSpecification collectionNodeSpec = new CollectionTreeNodeSpecification();
        final ObjectTreeNodeSpecification objectNodeSpec = new ObjectTreeNodeSpecification();
        final SubviewIconSpecification iconSpec = new SubviewIconSpecification();

        collectionNodeSpec.addSubviewDecorator(new SelectObjectBorder.Factory());
        collectionNodeSpec.addSubviewDecorator(new ExpandableViewBorder.Factory(iconSpec, objectNodeSpec, null));
        collectionNodeSpec.addViewDecorator(new IconBorder.Factory(Toolkit.getText(ColorsAndFonts.TEXT_NORMAL)));
        objectNodeSpec.addSubviewDecorator(new SelectObjectBorder.Factory());
        objectNodeSpec.addSubviewDecorator(new ExpandableViewBorder.Factory(iconSpec, objectNodeSpec, collectionNodeSpec));
        // objectNodeSpec.addSubviewDecorator(new FieldLabelsDecorator());
        objectNodeSpec.addViewDecorator(new IconBorder.Factory(Toolkit.getText(ColorsAndFonts.TEXT_NORMAL)));
        return new ViewSpecification[] { collectionNodeSpec, objectNodeSpec };
    }

    @Override
    protected ViewFactory createElementFactory() {
        return new IconElementFactory();
    }

    @Override
    public String getName() {
        return "Collection tree (experimental)";
    }

    // TODO this should be available if an item can be given more space
    /*
     * @Override public boolean canDisplay(final Content content,
     * ViewRequirement requirement) { return content.isCollection() &&
     * requirement.is(ViewRequirement.CLOSED) &&
     * requirement.is(ViewRequirement.SUBVIEW) &&
     * requirement.is(ViewRequirement.SUBVIEW); }
     */

}
