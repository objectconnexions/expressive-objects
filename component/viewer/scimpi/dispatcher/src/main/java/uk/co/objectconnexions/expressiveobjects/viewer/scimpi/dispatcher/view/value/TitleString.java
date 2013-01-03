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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.value;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ForbiddenException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

/**
 * 
 */
public class TitleString extends AbstractElementProcessor {

    @Override
    public void process(final Request request) {
        final String id = request.getOptionalProperty(OBJECT);
        final String fieldName = request.getOptionalProperty(FIELD);
        final int truncateTo = Integer.valueOf(request.getOptionalProperty(TRUNCATE, "0")).intValue();
        final ObjectAdapter object = request.getContext().getMappedObjectOrResult(id);
        if (object == null) { 
            return; 
        } 
        String titleString;
        if (fieldName == null) {
            titleString = object.titleString();
        } else {
            final ObjectAssociation field = object.getSpecification().getAssociation(fieldName);
            if (field.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, Where.ANYWHERE).isVetoed()) {
                throw new ForbiddenException(field, ForbiddenException.VISIBLE);
            }
            final ObjectAdapter fieldReference = field.get(object);
            if (fieldReference != null) {
                titleString = fieldReference.titleString();
            } else {
                titleString = "";
            }
        }
        request.appendDebug("    " + titleString);
        request.appendTruncated(titleString, truncateTo);
    }

    @Override
    public String getName() {
        return "title-string";
    }

}
