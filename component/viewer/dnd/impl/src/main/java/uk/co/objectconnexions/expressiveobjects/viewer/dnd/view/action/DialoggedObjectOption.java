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
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.UnknownTypeException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.BackgroundTask;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Placement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;

/**
 * Options for an underlying object determined dynamically by looking for
 * methods starting with action, veto and option for specifying the action,
 * vetoing the option and giving the option an name respectively.
 */
public class DialoggedObjectOption extends AbstractObjectOption {

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final static Where where = Where.ANYWHERE;

    public static DialoggedObjectOption createOption(final ObjectAction action, final ObjectAdapter object) {
        final int paramCount = action.getParameterCount();
        Assert.assertTrue("Only for actions taking one or more params", paramCount > 0);
        if (!action.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where).isAllowed() || !action.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where).isAllowed()) {
            return null;
        }

        final DialoggedObjectOption option = new DialoggedObjectOption(action, object);
        return option;
    }

    private DialoggedObjectOption(final ObjectAction action, final ObjectAdapter target) {
        super(action, target, action.getName() + "...");
    }

    @Override
    public void execute(final Workspace workspace, final View view, final Location at) {
        BackgroundWork.runTaskInBackground(view, new BackgroundTask() {
            @Override
            public void execute() {
                final ActionHelper helper = ActionHelper.createInstance(target, action);
                Content content;
                if (target == null && action.getOnType().isService() || target != null && target.getSpecification().isNotCollection()) {
                    content = new ObjectActionContent(helper);
                } else if (target.getSpecification().isParentedOrFreeCollection()) {
                    content = new CollectionActionContent(helper);
                } else {
                    throw new UnknownTypeException(target);
                }
                final View dialog = Toolkit.getViewFactory().createDialog(content);
                workspace.addDialog(dialog, new Placement(view));
            }

            @Override
            public String getDescription() {
                return "Preparing action " + getName() + " on  " + view.getContent().getAdapter();
            }

            @Override
            public String getName() {
                return "Preparing action " + action.getName();
            }
        });
    }
}
