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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.combined;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.form.FormSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.table.InternalTableSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.Layout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.GridLayout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.StackLayout;

public class FormWithTableSpecification extends SplitViewSpecification {

    // REVIEW: confirm this rendering context
    final Where where = Where.OBJECT_FORMS;

    @Override
    public Layout createLayout(final Content content, final Axes axes) {
        return new StackLayout();
    }

    @Override
    View createMainView(final Axes axes, final Content mainContent, final Content secondaryContent) {
        final View form1 = new FormSpecification() {
            @Override
            protected boolean include(final Content content, final int sequence) {
                return !secondaryContent.getId().equals(content.getId());
            };

            @Override
            public Layout createLayout(final Content content, final Axes axes) {
                final GridLayout gridLayout = new GridLayout();
                gridLayout.setSize(2);
                return gridLayout;
            }
        }.createView(mainContent, axes, -1);
        return form1;
    }

    @Override
    View createSecondaryView(final Axes axes, final Content fieldContent) {
        return new InternalTableSpecification().createView(fieldContent, axes, -1);
    }

    @Override
    Content determineSecondaryContent(final Content content) {
        final ObjectSpecification spec = content.getSpecification();
        final ObjectAdapter target = content.getAdapter();
        final AuthenticationSession session = ExpressiveObjectsContext.getAuthenticationSession();
        final List<ObjectAssociation> fields = spec.getAssociations(ObjectAssociationFilters.dynamicallyVisible(session, target, where));
        for (final ObjectAssociation field : fields) {
            if (field.isOneToManyAssociation()) {
                return Toolkit.getContentFactory().createFieldContent(field, target);
            }
        }
        return null;
    }

    /*
     * 
     * @Override protected void init() { addSubviewDecorator(new
     * FieldLabelsDecorator() { public View decorate(Axes axes, View view) { if
     * (view.getContent().isCollection()) { return view; } else { return
     * super.decorate(axes, view); } } }); addViewDecorator(new
     * IconBorder.Factory()); }
     * 
     * @Override protected SubviewSpec createFieldFactory() { return new
     * SubviewSpec() { public View createView(final Content content, Axes axes,
     * int sequence) { if (content.isCollection()) { return new
     * InternalTableSpecification().createView(content, axes, sequence); } else
     * { final ViewFactory factory = Toolkit.getViewFactory(); int requirement =
     * ViewRequirement.CLOSED | ViewRequirement.SUBVIEW; ViewRequirement
     * viewRequirement = new ViewRequirement(content, requirement); return
     * factory.createView(viewRequirement); } } }; }
     */
    @Override
    public String getName() {
        return "Form with table (experimental)";
    }

    @Override
    boolean validField(final ObjectAssociation field) {
        return false;
    }
}
