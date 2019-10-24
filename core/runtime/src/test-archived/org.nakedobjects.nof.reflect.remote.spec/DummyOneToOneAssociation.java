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


package uk.co.objectconnexions.expressiveobjects.nof.reflect.remote.spec;

import uk.co.objectconnexions.expressiveobjects.noa.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.noa.adapter.ObjectAdapterReference;
import uk.co.objectconnexions.expressiveobjects.noa.facets.Facet;
import uk.co.objectconnexions.expressiveobjects.noa.reflect.Consent;
import uk.co.objectconnexions.expressiveobjects.noa.reflect.ObjectField;
import uk.co.objectconnexions.expressiveobjects.noa.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.nof.core.reflect.Allow;
import uk.co.objectconnexions.expressiveobjects.nof.reflect.peer.OneToOnePeer;


public class DummyOneToOneAssociation implements ObjectField {

    private final OneToOnePeer fieldPeer;

    public DummyOneToOneAssociation(final OneToOnePeer fieldPeer) {
        this.fieldPeer = fieldPeer;
    }
    
    public String getBusinessKeyName() {
        return null;
    }

    public Object getDefault(
            ObjectAdapter adapter) {
        return null;
    }
    
    public void toDefault(ObjectAdapter target) {
    }
    
    public ObjectAdapter[] getOptions(final ObjectAdapter target) {
        return null;
    }

    public OneToOnePeer getPeer() {
        return fieldPeer;
    }

    public ObjectSpecification getSpecification() {
        return null;
    }

    public boolean isCollection() {
        return false;
    }

    public boolean isPersisted() {
        return true;
    }

    public boolean isEmpty(final ObjectAdapter adapter) {
        return false;
    }

    public boolean isObject() {
        return false;
    }

    public boolean isValue() {
        return false;
    }

    public boolean isMandatory() {
        return false;
    }

    public boolean isOptionEnabled() {
        return false;
    }


    public ObjectAdapter get(final ObjectAdapter fromObject) {
        return null;
    }

    public Facet getFacet(final Class cls) {
        return null;
    }
    
	public Class[] getFacetTypes() {
        return null;
    }

	public Facet[] getFacets(Facet.Filter filter) {
		return null;
	}

	public void addFacet(Facet facet) {
	}

	public void removeFacet(Facet facet) {
	}

    public String getName() {
        return null;
    }

    public String getId() {
        return fieldPeer.getIdentifier().getName();
    }

    public boolean hasHint() {
        return false;
    }

    public String getDescription() {
        return null;
    }

    public Consent isUsable() {
        Consent usableDeclaratively = isUsableDeclaratively();
        if (usableDeclaratively.isVetoed()) {
            return usableDeclaratively;
        }
        return isUsableForSession();
    }

    public Consent isUsableDeclaratively() {
        return Allow.DEFAULT;
    }

    public Consent isUsableForSession() {
        return Allow.DEFAULT;
    }

    public Consent isUsable(final ObjectAdapterReference target) {
        return null;
    }

    public boolean isVisible() {
        return isVisibleDeclaratively() && isVisibleForSession();
    }
    
    public boolean isVisibleDeclaratively() {
        return false;
    }

    public boolean isVisibleForSession() {
        return false;
    }

    public boolean isVisible(final ObjectAdapterReference target) {
        return true;
    }
    
    public String getHelp() {
        return null;
    }

    public String debugData() {
        return "";
    }



}
