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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.histogram;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.collection.CollectionContent;

class HistogramAxis implements ViewAxis {
    private final ObjectAssociation fields[];
    private final double maxValues[];
    private final int noBars;

    public HistogramAxis(final Content content) {
        final List<? extends ObjectAssociation> associationList = HistogramSpecification.availableFields((CollectionContent) content);
        noBars = associationList.size();
        fields = new ObjectAssociation[noBars];
        maxValues = new double[noBars];
        int i = 0;
        for (final ObjectAssociation association : associationList) {
            fields[i++] = association;
        }
    }

    public double getLengthFor(final Content content, final int fieldNo) {
        return NumberAdapters.doubleValue(fields[fieldNo], fields[fieldNo].get(content.getAdapter())) / maxValues[fieldNo];
    }

    public void determineMaximum(final Content content) {
        int i = 0;
        for (final ObjectAssociation field : fields) {
            maxValues[i] = 0;
            final CollectionFacet collectionFacet = content.getAdapter().getSpecification().getFacet(CollectionFacet.class);
            for (final ObjectAdapter element : collectionFacet.iterable(content.getAdapter())) {
                final ObjectAdapter value = field.get(element);
                final double doubleValue = NumberAdapters.doubleValue(field, value);
                maxValues[i] = Math.max(maxValues[i], doubleValue);
            }
            i++;
        }
    }

    public int getNoBars() {
        return noBars;
    }
}
