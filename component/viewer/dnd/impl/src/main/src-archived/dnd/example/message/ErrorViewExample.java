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


package uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.message;

import uk.co.objectconnexions.expressiveobjects.noa.ObjectAdapterRuntimeException;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.view.TestViews;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.message.DetailedMessageViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.message.ExceptionMessageContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.message.MessageDialogSpecification;



public class ErrorViewExample extends TestViews {

    public static void main(final String[] args) {
        new ErrorViewExample();
    }

    protected void views(final Workspace workspace) {
        Object object = new ObjectAdapterRuntimeException("The test exception message");
        Content content = new ExceptionMessageContent((Throwable) object);
        ViewAxis axis = null;

        View view = new MessageDialogSpecification().createView(content, axis);
        view.setLocation(new Location(100, 30));
        view.setSize(view.getRequiredSize(new Size()));
        workspace.addView(view);

        View view2 = new DetailedMessageViewSpecification().createView(content, axis);
        view2.setLocation(new Location(100, 260));
        view2.setSize(view2.getMaximumSize());
        workspace.addView(view2);
    }

}
