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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.icon;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.ObjectBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.field.OneToOneField;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.lookup.OpenObjectDropDownBorder;

public class SubviewIconSpecification extends IconSpecification {
    private static final ViewSpecification spec = new IconSpecification();

    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return super.canDisplay(requirement) && requirement.is(ViewRequirement.CLOSED) && requirement.is(ViewRequirement.SUBVIEW);
    }

    @Override
    public View createView(final Content content, final Axes axes, final int sequence) {
        final View view = super.createView(content, axes, sequence);
        /*
         * boolean isEditable = content instanceof OneToOneField &&
         * ((OneToOneField) content).isEditable().isAllowed(); boolean
         * hasOptions = content.isOptionEnabled(); if (isEditable && hasOptions)
         * { return new OpenObjectDropDownBorder(view, spec); } return view;
         */

        if (content instanceof OneToOneField && ((OneToOneField) content).isEditable().isVetoed()) {
            return new ObjectBorder(view);
        } else {
            if (content.isOptionEnabled()) {
                return new ObjectBorder(new OpenObjectDropDownBorder(view, spec));
            } else {
                return new ObjectBorder(view);
            }
        }

    }

}
