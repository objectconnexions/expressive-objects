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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.specimpl;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.MutableProposedHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.TypedHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.Instance;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneActionParameter;

public class OneToOneActionParameterImpl extends ObjectActionParameterAbstract implements OneToOneActionParameter {

    public OneToOneActionParameterImpl(final int index, final ObjectActionImpl actionImpl, final TypedHolder peer) {
        super(index, actionImpl, peer);
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.ACTION_PARAMETER;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    /**
     * TODO: need to be able to validate parameters individually, eg if have
     * <tt>RegEx</tt> annotation; should delegate to the Check framework
     * instead.
     */
    @Override
    public String isValid(final ObjectAdapter adapter, final Object proposedValue, final Localization localization) {
        return null;
    }

    // /////////////////////////////////////////////////////////////
    // getInstance
    // /////////////////////////////////////////////////////////////

    @Override
    public Instance getInstance(final ObjectAdapter adapter) {
        final OneToOneActionParameter specification = this;
        return adapter.getInstance(specification);
    }

    // //////////////////////////////////////////////////////////////////////
    // get, set
    // //////////////////////////////////////////////////////////////////////

    /**
     * Gets the proposed value of the {@link Instance} (downcast as a
     * {@link MutableProposed}, wrapping the proposed value into a
     * {@link ObjectAdapter}.
     */
    @Override
    public ObjectAdapter get(final ObjectAdapter owner) {
        final MutableProposedHolder proposedHolder = getProposedHolder(owner);
        final Object proposed = proposedHolder.getProposed();
        return getAdapterMap().adapterFor(proposed);
    }

    /**
     * Sets the proposed value of the {@link Instance} (downcast as a
     * {@link MutableProposed}, unwrapped the proposed value from a
     * {@link ObjectAdapter}.
     */
    public void set(final ObjectAdapter owner, final ObjectAdapter newValue) {
        final MutableProposedHolder proposedHolder = getProposedHolder(owner);
        final Object newValuePojo = newValue.getObject();
        proposedHolder.setProposed(newValuePojo);
    }

    private MutableProposedHolder getProposedHolder(final ObjectAdapter owner) {
        final Instance instance = getInstance(owner);
        if (!(instance instanceof MutableProposedHolder)) {
            throw new IllegalArgumentException("Instance should implement MutableProposedHolder");
        }
        final MutableProposedHolder proposedHolder = (MutableProposedHolder) instance;
        return proposedHolder;
    }

}
