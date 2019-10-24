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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.dialog;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.SubviewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.axis.LabelAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.DroppableLabelBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.LabelBorder;

public class ParametersLabelDecorator implements SubviewDecorator {

    @Override
    public ViewAxis createAxis(final Content content) {
        return new LabelAxis();
    }

    @Override
    public View decorate(final Axes axes, final View view) {
        final LabelAxis axis = axes.getAxis(LabelAxis.class);
        if (view.getContent().isObject() && !view.getContent().isTextParseable()) {
            return DroppableLabelBorder.createObjectParameterLabelBorder(axis, view);
        } else {
            return LabelBorder.createValueParameterLabelBorder(axis, view);
        }
    }
}
