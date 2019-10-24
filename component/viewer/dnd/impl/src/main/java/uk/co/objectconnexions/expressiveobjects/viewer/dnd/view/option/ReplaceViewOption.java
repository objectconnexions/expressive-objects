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
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;

public class ReplaceViewOption extends UserActionAbstract {
    private static final Logger LOG = Logger.getLogger(ReplaceViewOption.class);
    private final ViewSpecification specification;

    public ReplaceViewOption(final ViewSpecification specification) {
        super(specification.getName());
        this.specification = specification;
    }

    @Override
    public String getDescription(final View view) {
        return "Replace this " + view.getSpecification().getName() + " view with a " + specification.getName() + " view";
    }

    @Override
    public void execute(final Workspace workspace, final View view, final Location at) {
        final View replacement = specification.createView(view.getContent(), new Axes(), -1);
        LOG.debug("replacement view " + replacement);
        replace(view, replacement);
    }

    protected void replace(final View view, final View withReplacement) {
        final View existingView = view.getView();
        view.getParent().replaceView(existingView, withReplacement);
    }

    @Override
    public String toString() {
        return super.toString() + " [prototype=" + specification.getName() + "]";
    }
}
