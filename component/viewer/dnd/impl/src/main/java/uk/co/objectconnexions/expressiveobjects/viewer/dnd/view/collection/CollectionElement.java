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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.collection;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Veto;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.AbstractObjectContent;

public class CollectionElement extends AbstractObjectContent {
    private final ObjectAdapter adapter;

    public CollectionElement(final ObjectAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public boolean isOptionEnabled() {
        return false;
    }

    @Override
    public Consent canClear() {
        return Veto.DEFAULT;
    }

    @Override
    public Consent canSet(final ObjectAdapter dragSource) {
        return Veto.DEFAULT;
    }

    @Override
    public void clear() {
        throw new ExpressiveObjectsException("Invalid call");
    }

    @Override
    public void debugDetails(final DebugBuilder debug) {
        debug.appendln("element", adapter);
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getDescription() {
        return getSpecification().getSingularName() + ": " + getObject().titleString() + " " + getSpecification().getDescription();
    }

    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public ObjectAdapter getObject() {
        return adapter;
    }

    @Override
    public ObjectAdapter getAdapter() {
        return adapter;
    }

    @Override
    public ObjectSpecification getSpecification() {
        return adapter.getSpecification();
    }

    @Override
    public boolean isTransient() {
        return adapter.isTransient();
    }

    @Override
    public void setObject(final ObjectAdapter object) {
        throw new ExpressiveObjectsException("Invalid call");
    }

    @Override
    public String title() {
        return adapter.titleString();
    }

    @Override
    public String windowTitle() {
        return getSpecification().getShortIdentifier();
    }

    @Override
    public String toString() {
        return "" + adapter;
    }

    @Override
    public ObjectAdapter[] getOptions() {
        return null;
    }
}
