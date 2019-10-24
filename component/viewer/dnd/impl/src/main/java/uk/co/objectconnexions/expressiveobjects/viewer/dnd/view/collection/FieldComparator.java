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

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;

public class FieldComparator implements Comparator {
    private final ObjectAssociation field;
    private String title;

    public FieldComparator(final ObjectAssociation field) {
        this.field = field;
    }

    @Override
    public void init(final ObjectAdapter element) {
        final ObjectAdapter refTo = field.get(element);
        title = refTo == null ? null : refTo.titleString();
        title = title == null ? "" : title;
    }

    @Override
    public int compare(final ObjectAdapter sortedElement) {
        final ObjectAdapter refTo = field.get(sortedElement);
        String sortedTitle = refTo == null ? null : refTo.titleString();
        sortedTitle = sortedTitle == null ? "" : sortedTitle;
        final int compareTo = sortedTitle.compareTo(title);
        return compareTo;
    }

    public ObjectAssociation getField() {
        return field;
    }
}
