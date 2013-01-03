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

import java.util.ArrayList;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CompositeViewSpecification;

public abstract class SplitViewSpecification extends CompositeViewSpecification {

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    final Where where = Where.ANYWHERE;

    public SplitViewSpecification() {
        builder = new SplitViewBuilder(this);
    }

    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        if (requirement.isObject() && requirement.is(ViewRequirement.OPEN) && !requirement.isSubview()) {
            final Content fieldContent = determineSecondaryContent(requirement.getContent());
            return fieldContent != null && fieldContent.getAdapter() != null;
        } else {
            return false;
        }
    }

    abstract View createMainView(Axes axes, Content mainContent, final Content secondaryContent);

    abstract View createSecondaryView(Axes axes, final Content fieldContent);

    abstract Content determineSecondaryContent(Content content);

    Content field(final ObjectAssociation field, final Content content) {
        final ObjectSpecification spec = content.getSpecification();
        final ObjectAdapter target = content.getAdapter();
        return Toolkit.getContentFactory().createFieldContent(field, target);
    }

    List<ObjectAssociation> determineAvailableFields(final Content content) {
        final ObjectSpecification spec = content.getSpecification();
        final ObjectAdapter target = content.getAdapter();
        final AuthenticationSession session = ExpressiveObjectsContext.getAuthenticationSession();
        final List<ObjectAssociation> fields = spec.getAssociations(ObjectAssociationFilters.dynamicallyVisible(session, target, where));
        final List<ObjectAssociation> selectableFields = new ArrayList<ObjectAssociation>();
        for (final ObjectAssociation field : fields) {
            if (validField(field)) {
                selectableFields.add(field);
            }
        }
        return selectableFields;
    }

    abstract boolean validField(ObjectAssociation field);

}
