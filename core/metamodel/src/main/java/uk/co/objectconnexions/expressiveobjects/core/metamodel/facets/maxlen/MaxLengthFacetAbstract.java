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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.maxlen;

import uk.co.objectconnexions.expressiveobjects.applib.events.ValidityEvent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.SingleIntValueFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ProposedHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectAdapterUtils;

public abstract class MaxLengthFacetAbstract extends SingleIntValueFacetAbstract implements MaxLengthFacet {

    public static Class<? extends Facet> type() {
        return MaxLengthFacet.class;
    }

    public MaxLengthFacetAbstract(final int value, final FacetHolder holder) {
        super(type(), holder, value);
    }

    /**
     * Whether the provided argument exceeds the {@link #value() maximum length}
     * .
     */
    @Override
    public boolean exceeds(final ObjectAdapter adapter) {
        final String str = ObjectAdapterUtils.unwrapObjectAsString(adapter);
        if (str == null) {
            return false;
        }
        final int maxLength = value();
        return maxLength != 0 && str.length() > maxLength;
    }

    @Override
    public String invalidates(final ValidityContext<? extends ValidityEvent> context) {
        if (!(context instanceof ProposedHolder)) {
            return null;
        }
        final ProposedHolder proposedHolder = (ProposedHolder) context;
        final ObjectAdapter proposedArgument = proposedHolder.getProposed();
        if (!exceeds(proposedArgument)) {
            return null;
        }
        return "Proposed value '" + proposedArgument.titleString() + "' exceeds the maximum length of " + value();
    }

    @Override
    protected String toStringValues() {
        final int val = value();
        return val == 0 ? "unlimited" : String.valueOf(val);
    }

}
