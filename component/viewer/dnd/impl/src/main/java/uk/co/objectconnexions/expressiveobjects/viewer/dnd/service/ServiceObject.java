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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.service;

import java.util.Arrays;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.UnexpectedCallException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Veto;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Image;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ImageFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.UserActionSet;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.AbstractContent;

public class ServiceObject extends AbstractContent {
    private final ObjectAdapter adapter;

    public ServiceObject(final ObjectAdapter adapter) {
        this.adapter = adapter;
    }

    public Consent canClear() {
        return Veto.DEFAULT;
    }

    public Consent canSet(final ObjectAdapter dragSource) {
        return Veto.DEFAULT;
    }

    public void clear() {
        throw new ExpressiveObjectsException("Invalid call");
    }

    @Override
    public void debugDetails(final DebugBuilder debug) {
        debug.appendln("service", adapter);
    }

    @Override
    public ObjectAdapter getAdapter() {
        return adapter;
    }

    @Override
    public String getDescription() {
        final String specName = getSpecification().getSingularName();
        final String objectTitle = getObject().titleString();
        return specName + (specName.equalsIgnoreCase(objectTitle) ? "" : ": " + objectTitle) + " " + getSpecification().getDescription();
    }

    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public String getId() {
        return "";
    }

    public ObjectAdapter getObject() {
        return adapter;
    }

    @Override
    public ObjectAdapter[] getOptions() {
        return null;
    }

    @Override
    public ObjectSpecification getSpecification() {
        return adapter.getSpecification();
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isOptionEnabled() {
        return false;
    }

    @Override
    public boolean isTransient() {
        return adapter != null && adapter.isTransient();
    }

    public void setObject(final ObjectAdapter object) {
        throw new ExpressiveObjectsException("Invalid call");
    }

    @Override
    public String title() {
        return adapter.titleString();
    }

    @Override
    public String toString() {
        return "Service Object [" + adapter + "]";
    }

    @Override
    public String windowTitle() {
        return (isTransient() ? "UNSAVED " : "") + getSpecification().getSingularName();
    }

    @Override
    public Consent canDrop(final Content sourceContent) {
        final ObjectAction action = actionFor(sourceContent);
        if (action == null) {
            return Veto.DEFAULT;
        } else {
            final ObjectAdapter source = sourceContent.getAdapter();
            final Consent parameterSetValid = action.isProposedArgumentSetValid(adapter, new ObjectAdapter[] { source });
            parameterSetValid.setDescription("Execute '" + action.getName() + "' with " + source.titleString());
            return parameterSetValid;
        }
    }

    private ObjectAction actionFor(final Content sourceContent) {
        ObjectAction action;
        action = adapter.getSpecification().getObjectAction(ActionType.USER, null, Arrays.asList(sourceContent.getSpecification()));
        return action;
    }

    @Override
    public ObjectAdapter drop(final Content sourceContent) {
        final ObjectAction action = actionFor(sourceContent);
        final ObjectAdapter source = sourceContent.getAdapter();
        return action.execute(adapter, new ObjectAdapter[] { source });
    }

    @Override
    public String getIconName() {
        final ObjectAdapter object = getObject();
        return object == null ? null : object.getIconName();
    }

    @Override
    public Image getIconPicture(final int iconHeight) {
        final ObjectAdapter adapter = getObject();
        final ObjectSpecification specification = adapter.getSpecification();
        final Image icon = ImageFactory.getInstance().loadIcon(specification, iconHeight, null);
        return icon;
    }

    public void parseTextEntry(final String entryText) {
        throw new UnexpectedCallException();
    }

    @Override
    public void viewMenuOptions(final UserActionSet options) {
    }

}
