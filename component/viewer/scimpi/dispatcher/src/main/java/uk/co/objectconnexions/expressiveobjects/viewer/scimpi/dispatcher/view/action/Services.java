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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.action;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionContainer.Contributed;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.Dispatcher;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.field.InclusionList;

public class Services extends AbstractElementProcessor {

    @Override
    public void process(final Request request) {
        final boolean showForms = request.isRequested(FORMS, false);
        final String view = request.getOptionalProperty(VIEW, "_generic_action." + Dispatcher.EXTENSION);
        final String cancelTo = request.getOptionalProperty(CANCEL_TO);
        final String labelDelimiter = request.getOptionalProperty(LABEL_DELIMITER, "");

        final InclusionList inclusionList = new InclusionList();
        request.setBlockContent(inclusionList);
        request.processUtilCloseTag();

        final List<ObjectAdapter> serviceAdapters = getPersistenceSession().getServices();
        for (final ObjectAdapter adapter : serviceAdapters) {
            List<ObjectAction> actions = adapter.getSpecification().getObjectActions(ActionType.USER, Contributed.INCLUDED);
			if (inclusionList.includedActions(actions).size() > 0) {
	            final String serviceId = request.getContext().mapObject(adapter, Scope.REQUEST);
	            request.appendHtml("<div class=\"services\">");
	            request.appendHtml("<h3>");
	            request.appendAsHtmlEncoded(adapter.titleString());
	            request.appendHtml("</h3>");
	            Methods.writeMethods(request, serviceId, adapter, showForms, inclusionList, view, cancelTo, labelDelimiter);
	            request.appendHtml("</div>");
			}
        }
        request.popBlockContent();
    }

    @Override
    public String getName() {
        return "services";
    }

    private static PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

}
