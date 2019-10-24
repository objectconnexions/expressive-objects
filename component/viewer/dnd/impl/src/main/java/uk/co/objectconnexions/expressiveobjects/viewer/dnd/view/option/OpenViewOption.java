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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.option;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Placement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.FieldContent;

public class OpenViewOption extends UserActionAbstract {
    private static final Logger LOG = Logger.getLogger(OpenViewOption.class);
    private final ViewSpecification specification;

    public OpenViewOption(final ViewSpecification builder) {
        super(builder.getName());
        this.specification = builder;
    }

    @Override
    public void execute(final Workspace workspace, final View view, final Location at) {
        Content content = view.getContent();
        if (content.getAdapter() != null && !(content instanceof FieldContent)) {
            content = Toolkit.getContentFactory().createRootContent(content.getAdapter());
        }
        final View newView = specification.createView(content, view.getViewAxes(), -1);
        LOG.debug("open view " + newView);
        workspace.addWindow(newView, new Placement(view));
        workspace.markDamaged();
    }

    @Override
    public String getDescription(final View view) {
        final String title = view.getContent().title();
        return "Open '" + title + "' in a " + specification.getName() + " window";
    }

    @Override
    public String toString() {
        return super.toString() + " [prototype=" + specification.getName() + "]";
    }
}
