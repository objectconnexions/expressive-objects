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
import uk.co.objectconnexions.expressiveobjects.applib.events.ActionArgumentEvent;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionContextType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionInvocationMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;

/**
 * See {@link InteractionContext} for overview; analogous to
 * {@link ActionArgumentEvent}.
 */
public class ActionArgumentContext extends ValidityContext<ActionArgumentEvent> implements ProposedHolder {

    private final ObjectAdapter[] args;
    private final int position;
    private final ObjectAdapter proposed;

    public ActionArgumentContext(DeploymentCategory deploymentCategory, final AuthenticationSession session, final InteractionInvocationMethod invocationMethod, final ObjectAdapter target, final Identifier id, final ObjectAdapter[] args, final int position) {
        super(InteractionContextType.ACTION_PROPOSED_ARGUMENT, deploymentCategory, session, invocationMethod, id, target);

        this.args = args;
        this.position = position;
        this.proposed = args[position];
    }

    public ObjectAdapter[] getArgs() {
        return args;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public ObjectAdapter getProposed() {
        return proposed;
    }

    @Override
    public ActionArgumentEvent createInteractionEvent() {
        return new ActionArgumentEvent(unwrap(getTarget()), getIdentifier(), unwrap(getArgs()), getPosition());
    }

}
