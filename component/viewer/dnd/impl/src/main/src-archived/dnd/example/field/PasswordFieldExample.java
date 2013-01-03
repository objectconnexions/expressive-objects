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


package uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.field;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.ExampleViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.view.TestViews;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.field.PasswordField;


public class PasswordFieldExample extends TestViews {
    public static void main(final String[] args) {
        new PasswordFieldExample();
    }

    protected void views(final Workspace workspace) {
        View parent = new ParentView();

        Content content = new DummyTextParseableField("password");
        ViewSpecification specification = new ExampleViewSpecification();
        ViewAxis axis = null;

        PasswordField textField = new PasswordField(content, specification, axis);
        textField.setParent(parent);
        // textField.setMaxWidth(200);
        textField.setLocation(new Location(50, 20));
        textField.setSize(textField.getRequiredSize(new Size()));
        workspace.addView(textField);

        textField = new PasswordField(content, specification, axis);
        textField.setParent(parent);
        // textField.setMaxWidth(80);
        textField.setLocation(new Location(50, 80));
        textField.setSize(textField.getRequiredSize(new Size()));
        workspace.addView(textField);

        content = new DummyTextParseableField("pa");
        PasswordField view = new PasswordField(content, specification, axis);
        view.setParent(parent);
        // view.setMaxWidth(200);
        view.setLocation(new Location(50, 140));
        view.setSize(view.getRequiredSize(new Size()));
        workspace.addView(view);

        view = new PasswordField(content, specification, axis);
        view.setParent(parent);
        // view.setMaxWidth(80);
        view.setLocation(new Location(50, 250));
        view.setSize(view.getRequiredSize(new Size()));
        workspace.addView(view);
    }

    protected boolean showOutline() {
        return true;
    }
}
