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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.field;

import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.date.DateValueFacet;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.AbstractFieldSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.TextFieldResizeBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.TextParseableContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.lookup.OpenDropDownBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.lookup.OptionContent;

/**
 * Creates a single line text field with the base line drawn.
 */
public class DateFieldSpecification extends AbstractFieldSpecification {
    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return requirement.isTextParseable() && requirement.isForValueType(DateValueFacet.class);
    }

    @Override
    public View createView(final Content content, final Axes axes, final int sequence) {
        final SingleLineTextField textField = new SingleLineTextField((TextParseableContent) content, this, true);
        final View field = new TextFieldResizeBorder(textField);
        return new OpenDropDownBorder(field) {
            @Override
            protected View createDropDownView() {
                return DatePickerControl.getPicker(content);
            }

            @Override
            protected void setSelection(final OptionContent selectedContent) {
            }
        };
    }

    @Override
    public String getName() {
        return "Date Field";
    }
}
