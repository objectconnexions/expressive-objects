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


package uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.border;

import uk.co.objectconnexions.expressiveobjects.noa.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.border.WindowBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.content.RootObject;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.ExampleViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.view.TestObjectView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.view.TestViews;


public class WindowBorderExample extends TestViews {

    public static void main(final String[] args) {
        new WindowBorderExample();
    }

    protected void views(final Workspace workspace) {
        ObjectAdapter object = createExampleObjectForView();
        Content content = new RootObject(object);
        ViewSpecification specification = new ExampleViewSpecification();
        ViewAxis axis = null;

        View view = new WindowBorder(new TestObjectView(content, specification, axis, 300, 120, "normal"), false);
        view.setLocation(new Location(50, 60));
        view.setSize(view.getRequiredSize(new Size()));
        workspace.addView(view);

        view = new WindowBorder(new TestObjectView(content, specification, axis, 100, 30, "active"), false);
        view.setLocation(new Location(200, 300));
        view.setSize(view.getRequiredSize(new Size()));
        workspace.addView(view);

        view.getState().setActive();

        view = new WindowBorder(new TestObjectView(content, specification, axis, 100, 30, "view identified"), false);
        view.setLocation(new Location(200, 400));
        view.setSize(view.getRequiredSize(new Size()));
        workspace.addView(view);

        view.getState().setInactive();
        view.getState().setRootViewIdentified();

    }

}
