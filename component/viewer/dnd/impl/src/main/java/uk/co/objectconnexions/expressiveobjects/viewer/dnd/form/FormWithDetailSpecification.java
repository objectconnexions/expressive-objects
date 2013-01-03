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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.form;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.SelectObjectBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.MasterDetailPanel;

public class FormWithDetailSpecification implements ViewSpecification {
    private final FormSpecification leftHandSideSpecification;

    public FormWithDetailSpecification() {
        leftHandSideSpecification = new FormSpecification();
        leftHandSideSpecification.addSubviewDecorator(new SelectObjectBorder.Factory());
    }

    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return requirement.isObject() && requirement.isOpen() && !requirement.isSubview() && containsEnoughFields(requirement.getContent());
    }

    private boolean containsEnoughFields(final Content content) {
        final ObjectSpecification specification = content.getSpecification();
        final List<ObjectAssociation> associations = specification.getAssociations(new Filter<ObjectAssociation>() {
            @Override
            public boolean accept(final ObjectAssociation t) {
                return t.isOneToManyAssociation() || (t.isOneToOneAssociation() && !((OneToOneAssociation) t).getSpecification().isParseable());
            }
        });
        return associations.size() >= 1;
    }

    @Override
    public View createView(final Content content, final Axes axes, final int sequence) {
        return new MasterDetailPanel(content, this, leftHandSideSpecification);
    }

    @Override
    public String getName() {
        return "Form and details (experimental)";
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
    public boolean isResizeable() {
        return true;
    }

    @Override
    public boolean isSubView() {
        return false;
    }

}
