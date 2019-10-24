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

import java.util.Arrays;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.FreeStandingList;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionContainer.Contributed;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.UserAction;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.UserActionSet;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.option.DisposeObjectOption;

public class OptionFactory {

    public static void addCreateOptions(final ObjectSpecification specification, final UserActionSet options) {
        // TODO do the same as addObjectMenuOptions and collect together all the
        // actions for all the types
        final List<ObjectAction> actions = specification.getServiceActionsReturning(Arrays.asList(ActionType.USER, ActionType.EXPLORATION, ActionType.PROTOTYPE, ActionType.DEBUG));
        menuOptions(actions, null, options);
    }

    public static void addObjectMenuOptions(final ObjectAdapter adapter, final UserActionSet options) {
        if (adapter == null) {
            return;
        }

        final ObjectSpecification noSpec = adapter.getSpecification();
        menuOptions(noSpec.getObjectActions(Arrays.asList(ActionType.USER, ActionType.EXPLORATION, ActionType.PROTOTYPE, ActionType.DEBUG), Contributed.INCLUDED), adapter, options);

        // TODO: this looks like a bit of a hack; can we improve it by looking
        // at the facets?
        if (adapter.getObject() instanceof FreeStandingList) {
            return;
        }
        final Oid oid = adapter.getOid();
        if (oid != null && adapter.isTransient()) {
            return;
        }
        if (noSpec.isService()) {
            return;
        }

        options.add(new DisposeObjectOption());
    }

    private static void menuOptions(final List<ObjectAction> actions, final ObjectAdapter target, final UserActionSet menuOptionSet) {
        for (int i = 0; i < actions.size(); i++) {
            UserAction option = null;
            if (actions.get(i).getActions().size() > 0) {
                option = menuOptionSet.addNewActionSet(actions.get(i).getName());
                menuOptions(actions.get(i).getActions(), target, (UserActionSet) option);

            } else {
                final int noOfParameters = actions.get(i).getParameterCount();
                if (noOfParameters == 0) {
                    option = ImmediateObjectOption.createOption(actions.get(i), target);
                } else if (actions.get(i).isContributed() && noOfParameters == 1 && target != null && target.getSpecification().isOfType(actions.get(i).getParameters().get(0).getSpecification())) {
                    option = ImmediateObjectOption.createServiceOption(actions.get(i), target);
                } else {
                    option = DialoggedObjectOption.createOption(actions.get(i), target);
                }
                if (option != null) {
                    menuOptionSet.add(option);
                }
            }
        }
    }
}
