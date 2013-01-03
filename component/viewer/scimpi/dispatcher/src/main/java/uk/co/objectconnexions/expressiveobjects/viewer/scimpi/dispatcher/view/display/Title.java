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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ForbiddenException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.util.MethodsUtils;

public class Title extends AbstractElementProcessor {

    @Override
    public void process(final Request request) {
        final String id = request.getOptionalProperty(OBJECT);
        final String fieldName = request.getOptionalProperty(FIELD);
        final int truncateTo = Integer.valueOf(request.getOptionalProperty(TRUNCATE, "0")).intValue();
        final boolean isIconShowing = request.isRequested(SHOW_ICON, showIconByDefault());
        String className = request.getOptionalProperty(CLASS);
        className = className == null ? "title-icon" : className;
        ObjectAdapter object = MethodsUtils.findObject(request.getContext(), id);
        if (fieldName != null) {
            final ObjectAssociation field = object.getSpecification().getAssociation(fieldName);
            if (field.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, Where.ANYWHERE).isVetoed()) {
                throw new ForbiddenException(field, ForbiddenException.VISIBLE);
            }
            object = field.get(object);
        }

        if (object != null) {
            request.appendHtml("<span class=\"object\">");
            ExpressiveObjectsContext.getPersistenceSession().resolveImmediately(object);
            if (isIconShowing) {
                final String iconPath = request.getContext().imagePath(object);
                request.appendHtml("<img class=\"" + className + "\" src=\"" + iconPath + "\" />");
            }
            request.appendTruncated(object.titleString(), truncateTo);
            request.appendHtml("</span>");
        }
        request.closeEmpty();
    }

    @Override
    public String getName() {
        return "title";
    }

}
