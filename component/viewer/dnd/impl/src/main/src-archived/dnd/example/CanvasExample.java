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


package uk.co.objectconnexions.expressiveobjects.viewer.dnd.example;

import uk.co.objectconnexions.expressiveobjects.nof.core.util.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.view.TestViews;


public class CanvasExample extends TestViews {

    public static void main(final String[] args) {
        new CanvasExample();

    }

    protected void configure(final ExpressiveObjectsConfiguration configuration) {
        configuration.add("expressive-objects.viewer.skylark.ascent-adjust", "true");
    }

    protected void views(final Workspace workspace) {
        // AbstractView.debug = true;

        View view = new TestCanvasView();
        view.setLocation(new Location(50, 60));
        view.setSize(new Size(216, 300));
        workspace.addView(view);

        view = new TestCanvasView();
        view.setLocation(new Location(300, 60));
        view.setSize(new Size(216, 300));
        workspace.addView(view);

        view = new TestCanvasView2();
        view.setLocation(new Location(570, 60));
        view.setSize(new Size(50, 70));
        workspace.addView(view);

        view = new TestCanvasView2();
        view.setLocation(new Location(570, 160));
        view.setSize(new Size(8, 5));
        workspace.addView(view);
    }

}
