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

import uk.co.objectconnexions.expressiveobjects.noa.adapter.ResolveState;
import uk.co.objectconnexions.expressiveobjects.nof.testsystem.TestProxyAdapter;
import uk.co.objectconnexions.expressiveobjects.nof.testsystem.TestSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.basic.TreeBrowserSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.content.RootObject;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.view.TestViews;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.tree.TreeBrowserFrame;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.form.WindowFormSpecification;



public class TreeExample extends TestViews {

    public static void main(final String[] args) {
        new TreeExample();
    }

    protected void views(final Workspace workspace) {
        TestProxyAdapter object = new TestProxyAdapter();
        object.setupSpecification(new TestSpecification());
        object.setupResolveState(ResolveState.TRANSIENT);
        
        ViewAxis axis = new TreeBrowserFrame(null, null);

        Content content = new RootObject(object);

        View view = new TreeBrowserSpecification().createView(content, axis);
        view.setLocation(new Location(100, 50));
        view.setSize(view.getRequiredSize(new Size()));
        workspace.addView(view);

        view = new WindowFormSpecification().createView(content, axis);
        view.setLocation(new Location(100, 200));
        view.setSize(view.getRequiredSize(new Size()));
        workspace.addView(view);

    }

}
