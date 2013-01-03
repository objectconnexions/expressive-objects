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
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ScimpiException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class FieldLabel extends AbstractElementProcessor {

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final Where where = Where.ANYWHERE;

    @Override
    public void process(final Request request) {
        final String id = request.getOptionalProperty(OBJECT);
        final String fieldName = request.getRequiredProperty(FIELD);
        final ObjectAdapter object = request.getContext().getMappedObjectOrResult(id);
        final ObjectAssociation field = object.getSpecification().getAssociation(fieldName);
        if (field == null) {
            throw new ScimpiException("No field " + fieldName + " in " + object.getSpecification().getFullIdentifier());
        }
        if (field.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where).isVetoed()) {
            throw new ForbiddenException(field, ForbiddenException.VISIBLE);
        }
        String delimiter = request.getOptionalProperty("delimiter");
        if (delimiter == null) {
            delimiter = ": ";
        } else if (delimiter.equals("")) {
            delimiter = null;
        }
        write(request, field, delimiter);
    }

    @Override
    public String getName() {
        return "label";
    }

    public static void write(final Request content, final ObjectAssociation field, final String delimiter) {
        final String description = field.getDescription();
        final String titleSegment = description == null || description.equals("") ? null : ("title=\"" + description + "\"");
        content.appendHtml("<span class=\"label\"" + titleSegment + ">");
        content.appendAsHtmlEncoded(field.getName());
        if (delimiter != null) {
            content.appendHtml("<span class=\"delimiter\">" + delimiter + "</span>");
        }
        content.appendHtml("</span>");
    }

}
