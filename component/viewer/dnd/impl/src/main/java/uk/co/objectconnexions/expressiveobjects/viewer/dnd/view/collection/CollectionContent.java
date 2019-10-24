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

import java.util.Enumeration;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Image;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.UserActionSet;

public interface CollectionContent extends Content {

    Enumeration allElements();

    @Override
    void debugDetails(final DebugBuilder debug);

    ObjectAdapter[] elements();

    ObjectAdapter getCollection();

    @Override
    void contentMenuOptions(final UserActionSet options);

    @Override
    void viewMenuOptions(final UserActionSet options);

    void parseTextEntry(final String entryText);

    void setOrder(final Comparator order);

    void setOrderByField(final ObjectAssociation field);

    void setOrderByElement();

    ObjectAssociation getFieldSortOrder();

    @Override
    Image getIconPicture(final int iconHeight);

    boolean getOrderByElement();

    boolean getReverseSortOrder();

    @Override
    boolean isOptionEnabled();

    @Override
    ObjectAdapter[] getOptions();

    ObjectSpecification getElementSpecification();
}
