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
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ScimpiException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class TableCell extends AbstractElementProcessor {

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with
    // @Hidden(where=Where.ALL_TABLES) or @Disabled(where=Where.ALL_TABLES) will indeed
    // be hidden from all tables but will be visible/enabled (perhaps incorrectly) 
    // if annotated with Where.PARENTED_TABLE or Where.STANDALONE_TABLE
    private final Where where = Where.ALL_TABLES;

    @Override
    public void process(final Request request) {
        final TableBlock tableBlock = (TableBlock) request.getBlockContent();
        final String id = request.getOptionalProperty(OBJECT);
        final String fieldName = request.getRequiredProperty(FIELD);
        final String linkView = request.getOptionalProperty(LINK_VIEW);
        String className = request.getOptionalProperty(CLASS);
        className = className == null ? "" : " class=\"" + className + "\"";
        RequestContext context = request.getContext();
        final ObjectAdapter object = context.getMappedObjectOrVariable(id, tableBlock.getElementName());
        final ObjectAssociation field = object.getSpecification().getAssociation(fieldName);
        if (field == null) {
            throw new ScimpiException("No field " + fieldName + " in " + object.getSpecification().getFullIdentifier());
        }
        request.appendHtml("<td" + className + ">");
        if (field.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where).isAllowed()) {
            final ObjectAdapter fieldReference = field.get(object);
            final String source = fieldReference == null ? "" : context.mapObject(fieldReference, Scope.REQUEST);
            final String name = request.getOptionalProperty(RESULT_NAME, fieldName);
            context.addVariable(name, Request.getEncoder().encoder(source), Scope.REQUEST);

            if (linkView != null) {
                final String linkId = context.mapObject(object, Scope.REQUEST);
                final String linkName = request.getOptionalProperty(LINK_NAME, RequestContext.RESULT);
                final String linkObject = request.getOptionalProperty(LINK_OBJECT, linkId);
                request.appendHtml("<a href=\"" + linkView + "?" + linkName + "=" + linkObject + context.encodedInteractionParameters() + "\">");
            } else if(tableBlock.getlinkView() != null) {
                String linkObjectInVariable = tableBlock.getElementName();
                final String linkId = (String) context.getVariable(linkObjectInVariable);
                request.appendHtml("<a href=\"" + tableBlock.getlinkView() + "?" + tableBlock.getlinkName() + "=" + linkId + context.encodedInteractionParameters() + "\">");                
            }
            request.pushNewBuffer();
            request.processUtilCloseTag();
            final String buffer = request.popBuffer();
            if (buffer.trim().length() == 0) {
                request.appendAsHtmlEncoded(fieldReference == null ? "" : fieldReference.titleString());
            } else {
                request.appendHtml(buffer);
            }
            if (linkView != null) {
                request.appendHtml("</a>");
            }
        } else {
            request.skipUntilClose();
        }
        request.appendHtml("</td>");
    }

    @Override
    public String getName() {
        return "table-cell";
    }

}
