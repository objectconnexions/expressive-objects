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
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Image;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ImageFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;

public class RootCollection extends AbstractCollectionContent {
    private final ObjectAdapter collection;

    public RootCollection(final ObjectAdapter collection) {
        this.collection = collection;
    }

    @Override
    public void debugDetails(final DebugBuilder debug) {
        debug.appendln("collection", collection);
        super.debugDetails(debug);
    }

    @Override
    public ObjectAdapter getCollection() {
        return collection;
    }

    @Override
    public String getHelp() {
        return "No help for this collection";
    }

    @Override
    public String getIconName() {
        return null;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public ObjectAdapter getAdapter() {
        return collection;
    }

    @Override
    public ObjectSpecification getSpecification() {
        return collection.getSpecification();
    }

    @Override
    public boolean isTransient() {
        return collection != null;
    }

    public void setObject(final ObjectAdapter object) {
        throw new ExpressiveObjectsException("Invalid call");
    }

    @Override
    public String title() {
        return collection.titleString();
    }

    @Override
    public String windowTitle() {
        return collection.titleString();
    }

    @Override
    public String toString() {
        return "Root Collection: " + collection;
    }

    @Override
    public ObjectAdapter drop(final Content sourceContent) {
        return null;
    }

    @Override
    public Consent canDrop(final Content sourceContent) {
        return Veto.DEFAULT;
    }

    @Override
    public Image getIconPicture(final int iconHeight) {
        // return ImageFactory.getInstance().loadObjectIcon(getSpecification(),
        // "", iconHeight);
        return ImageFactory.getInstance().loadIcon("root-collection", iconHeight, null);
    }
}
