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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.UserActionSet;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.AbstractViewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.option.UserActionAbstract;

public class FieldOrderBorder extends AbstractViewDecorator {

    private boolean isReversed;

    public FieldOrderBorder(final View wrappedView) {
        super(wrappedView);
    }

    @Override
    public View[] getSubviews() {
        View[] subviews = super.getSubviews();
        if (isReversed) {
            final View[] v = new View[subviews.length];
            for (int i = 0; i < v.length; i++) {
                v[i] = subviews[v.length - i - 1];
            }
            subviews = v;
        }

        return subviews;
    }

    @Override
    public void viewMenuOptions(final UserActionSet menuOptions) {
        super.viewMenuOptions(menuOptions);

        menuOptions.add(new UserActionAbstract("Reverse view order") {
            @Override
            public void execute(final Workspace workspace, final View view, final Location at) {
                isReversed = !isReversed;
                invalidateLayout();
            }
        });
    }

}
