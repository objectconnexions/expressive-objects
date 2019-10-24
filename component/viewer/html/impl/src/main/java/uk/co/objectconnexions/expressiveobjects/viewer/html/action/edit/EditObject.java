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

package uk.co.objectconnexions.expressiveobjects.viewer.html.action.edit;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ResolveState;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.Action;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.ActionException;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.Page;
import uk.co.objectconnexions.expressiveobjects.viewer.html.context.Context;
import uk.co.objectconnexions.expressiveobjects.viewer.html.request.ForwardRequest;
import uk.co.objectconnexions.expressiveobjects.viewer.html.request.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.html.task.EditTask;

public class EditObject implements Action {

    @Override
    public void execute(final Request request, final Context context, final Page page) {
        final String idString = request.getObjectId();
        if (idString == null) {
            throw new ActionException("Task no longer in progress");
        }
        final ObjectAdapter object = context.getMappedObject(idString);
        if (!(object.isTransient())) {
            context.setObjectCrumb(object);
        }
        final EditTask editTask = new EditTask(context, object);
        context.addTaskCrumb(editTask);
        request.forward(ForwardRequest.task(editTask));
    }

    @Override
    public String name() {
        return Request.EDIT_COMMAND;
    }
}
