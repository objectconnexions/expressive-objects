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
import uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Assert;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.BackgroundTask;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Placement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;

/**
 * Options for an underlying object determined dynamically by looking for
 * methods starting with action, veto and option for specifying the action,
 * vetoing the option and giving the option an name respectively.
 */
public class ImmediateObjectOption extends AbstractObjectOption {

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final static Where where = Where.ANYWHERE;

    public static ImmediateObjectOption createOption(final ObjectAction action, final ObjectAdapter object) {
        Assert.assertTrue("Only suitable for 0 param methods", action.getParameterCount() == 0);
        if (!action.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where).isAllowed()) {
            return null;
        }
        final ImmediateObjectOption option = new ImmediateObjectOption(action, object);
        return option;
    }

    public static ImmediateObjectOption createServiceOption(final ObjectAction action, final ObjectAdapter object) {
        Assert.assertTrue("Only suitable for 1 param methods", action.getParameterCount() == 1);
        if (!action.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where).isAllowed()) {
            return null;
        }
        final ImmediateObjectOption option = new ImmediateObjectOption(action, object);

        return option;
    }

    private ImmediateObjectOption(final ObjectAction action, final ObjectAdapter target) {
        super(action, target, action.getName());
    }

    @Override
    protected Consent checkValid() {
        return action.isProposedArgumentSetValid(target, null);
    }

    // TODO this method is very similar to ActionDialogSpecification.execute()
    @Override
    public void execute(final Workspace workspace, final View view, final Location at) {
        BackgroundWork.runTaskInBackground(view, new BackgroundTask() {
            @Override
            public void execute() {
                ObjectAdapter result;
                result = action.execute(target, null);
                view.objectActionResult(result, new Placement(view));
                view.getViewManager().disposeUnneededViews();
                view.getFeedbackManager().showMessagesAndWarnings();
                workspace.notifyViewsFor(target);
                workspace.notifyViewsFor(result);
            }

            @Override
            public String getDescription() {
                return "Running action " + getName() + " on  " + view.getContent().getAdapter();
            }

            @Override
            public String getName() {
                return "ObjectAction " + action.getName();
            }
        });
    }
}
