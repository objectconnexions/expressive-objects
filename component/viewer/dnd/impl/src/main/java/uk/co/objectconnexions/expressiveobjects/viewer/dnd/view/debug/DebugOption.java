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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.debug;

import java.util.List;

import com.google.common.collect.Lists;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebuggableWithTitle;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.PerspectiveEntry;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfilesDebugUtil;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.service.PerspectiveContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.option.UserActionAbstract;

/**
 * Display debug window
 */
public class DebugOption extends UserActionAbstract {
    public DebugOption() {
        super("Debug...", ActionType.DEBUG);
    }

    @Override
    public void execute(final Workspace workspace, final View view, final Location at) {
        final Content content = view.getContent();
        final ObjectAdapter object = content == null ? null : content.getAdapter();

        final List<DebuggableWithTitle> debug = Lists.newArrayList();
        if (content instanceof PerspectiveContent) {
            final PerspectiveEntry perspectiveEntry = ((PerspectiveContent) content).getPerspective();
            debug.add(UserProfilesDebugUtil.asDebuggableWithTitle(perspectiveEntry));
        } else {
            debug.add(new DebugObjectSpecification(content.getSpecification()));
        }
        if (object != null) {
            debug.add(new DebugAdapter(object));
            debug.add(new DebugObjectGraph(object));
        }

        debug.add(new DebugViewStructure(view));
        debug.add(new DebugContent(view));
        debug.add(new DebugDrawing(view));
        debug.add(new DebugDrawingAbsolute(view));

        final DebuggableWithTitle[] info = debug.toArray(new DebuggableWithTitle[debug.size()]);
        at.add(50, 6);
        // at.getX() + 50, at.getY() + 6
        Toolkit.getViewer().showDebugFrame(info, at);
    }

    @Override
    public String getDescription(final View view) {
        return "Open debug window about " + view;
    }
}
