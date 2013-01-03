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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions;

import static uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.util.AdapterUtils.unwrap;

import uk.co.objectconnexions.expressiveobjects.applib.Identifier;
import uk.co.objectconnexions.expressiveobjects.applib.events.CollectionAddToEvent;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionContextType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionInvocationMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;

/**
 * See {@link InteractionContext} for overview; analogous to
 * {@link CollectionAddToEvent}.
 */
public class CollectionAddToContext extends ValidityContext<CollectionAddToEvent> {

    private final ObjectAdapter proposed;

    public CollectionAddToContext(DeploymentCategory deploymentCategory, final AuthenticationSession session, final InteractionInvocationMethod invocationMethod, final ObjectAdapter target, final Identifier id, final ObjectAdapter proposed) {
        super(InteractionContextType.COLLECTION_ADD_TO, deploymentCategory, session, invocationMethod, id, target);

        this.proposed = proposed;
    }

    public ObjectAdapter getProposed() {
        return proposed;
    }

    @Override
    public CollectionAddToEvent createInteractionEvent() {
        return new CollectionAddToEvent(unwrap(getTarget()), getIdentifier(), unwrap(getProposed()));
    }

}
