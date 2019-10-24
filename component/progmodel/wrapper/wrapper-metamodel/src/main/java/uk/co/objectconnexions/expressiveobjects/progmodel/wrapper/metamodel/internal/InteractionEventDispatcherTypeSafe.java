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

package uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.metamodel.internal;

import uk.co.objectconnexions.expressiveobjects.applib.events.InteractionEvent;

public abstract class InteractionEventDispatcherTypeSafe<T extends InteractionEvent> implements InteractionEventDispatcher {

    public abstract void dispatchTypeSafe(T interactionEvent);

    @Override
    @SuppressWarnings("unchecked")
    public void dispatch(final InteractionEvent interactionEvent) {
        dispatchTypeSafe((T) interactionEvent);
    }

}
