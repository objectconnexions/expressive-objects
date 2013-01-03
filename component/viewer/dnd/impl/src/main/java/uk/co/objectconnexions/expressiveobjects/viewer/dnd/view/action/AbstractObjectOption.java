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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.action;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ToString;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Allow;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Veto;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.option.UserActionAbstract;

public abstract class AbstractObjectOption extends UserActionAbstract {

    // REVIEW: confirm this rendering context
    private final Where where = Where.OBJECT_FORMS;

    protected final ObjectAction action;
    protected final ObjectAdapter target;

    protected AbstractObjectOption(final ObjectAction action, final ObjectAdapter target, final String name) {
        super(name);
        this.action = action;
        this.target = target;
    }

    @Override
    public Consent disabled(final View view) {
        final ObjectAdapter adapter = view.getContent().getAdapter();
        if (adapter != null && adapter.isDestroyed()) {
            // TODO: move logic into Facet
            return new Veto("Can't do anything with a destroyed object");
        }
        final Consent usableForUser = action.isUsable(ExpressiveObjectsContext.getAuthenticationSession(), target, where);
        if (usableForUser.isVetoed()) {
            return usableForUser;
        }

        final Consent validParameters = checkValid();
        if (validParameters != null && validParameters.isVetoed()) {
            return validParameters;
        }
        final String desc = action.getDescription();
        final String description = getName(view) + (desc.length() == 0 ? "" : ": " + desc);
        // TODO: replace with a Facet
        return new Allow(description);
    }

    protected Consent checkValid() {
        return null;
    }

    @Override
    public String getHelp(final View view) {
        return action.getHelp();
    }

    @Override
    public ActionType getType() {
        return action.getType();
    }

    @Override
    public String toString() {
        return new ToString(this).append("action", action).toString();
    }
}
