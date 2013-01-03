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


package uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.tree;

import uk.co.objectconnexions.expressiveobjects.noa.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.basic.TextFieldResizeBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.content.RootObject;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.ExampleViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.view.TestObjectViewWithDragging;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.view.TestViews;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.tree.TreeBrowserResizeBorder;


public class ResizeBorderExample extends TestViews {

    public static void main(final String[] args) {
        new ResizeBorderExample();
    }

    protected void views(final Workspace workspace) {
        ObjectAdapter object = createExampleObjectForView();
        Content content = new RootObject(object);
        ViewSpecification specification = new ExampleViewSpecification();
        ViewAxis axis = null;

        View view = new TextFieldResizeBorder(new TestObjectViewWithDragging(content, specification, axis, 400, 400, "resizing"));
        view.setLocation(new Location(50, 60));
        view.setSize(new Size(100, 24));
        workspace.addView(view);

        view = new TreeBrowserResizeBorder(new TestObjectViewWithDragging(content, specification, axis, 400, 400, "resizing"));
        view.setLocation(new Location(50, 120));
        view.setSize(new Size(200, 200));
        workspace.addView(view);
    }

}
