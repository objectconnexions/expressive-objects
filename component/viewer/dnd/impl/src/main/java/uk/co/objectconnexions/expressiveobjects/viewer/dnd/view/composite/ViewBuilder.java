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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.SubviewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.UserActionSet;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;

public interface ViewBuilder {

    void addSubviewDecorator(SubviewDecorator decorator);

    void createAxes(Axes axes, Content content);

    void build(View view, Axes axes);

    /**
     * Indicates whether this view is expanded, or iconized.
     * 
     * @return true if it is showing the object's details; false if it is
     *         showing the object only.
     */
    boolean isOpen();

    /**
     * Indicates whether this view can be replaced with another view (for the
     * same value or reference).
     * 
     * @return true if it can be replaced by another view; false if it can't be
     *         replaces
     */
    boolean isReplaceable();

    boolean isSubView();

    boolean canDragView();

    void viewMenuOptions(UserActionSet options, View view);
}
