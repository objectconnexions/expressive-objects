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

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ToString;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Allow;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.UserAction;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;

/**
 * Each option that a user is shown in an objects popup menu a MenuOption. A
 * MenuOption details: the name of an option (in the users language);
 * <ul>
 * the type of object that might result when requesting this option
 * </ul>
 * ; a way to determine whether a user can select this option on the current
 * object.
 */
public abstract class UserActionAbstract implements UserAction {
    private String description;
    private String name;
    private final ActionType type;

    public UserActionAbstract(final String name) {
        this(name, ActionType.USER);
    }

    public UserActionAbstract(final String name, final ActionType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public Consent disabled(final View view) {
        return Allow.DEFAULT;
    }

    @Override
    public abstract void execute(final Workspace workspace, final View view, final Location at);

    @Override
    public String getDescription(final View view) {
        return description;
    }

    @Override
    public String getHelp(final View view) {
        return "No help available for user action";
    }

    /**
     * Returns the stored name of the menu option.
     */
    @Override
    public String getName(final View view) {
        return name;
    }

    @Override
    public ActionType getType() {
        return type;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final ToString str = new ToString(this);
        str.append("name", name);
        str.append("type", type);
        return str.toString();
    }
}
