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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.basic;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.field.SingleLineTextField;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.AbstractFieldSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.TextParseableContent;

/**
 * Creates a single line text field without the baseline being drawn.
 */
public class UnlinedTextFieldSpecification extends AbstractFieldSpecification {
    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return requirement.isTextParseable() && ((TextParseableContent) requirement.getContent()).getNoLines() == 1;
    }

    @Override
    public View createView(final Content content, final Axes axes, final int sequence) {
        return new SingleLineTextField((TextParseableContent) content, this, false);
    }

    @Override
    public String getName() {
        return "Single Line Text Field";
    }

    @Override
    public boolean isAligned() {
        return true;
    }

}
