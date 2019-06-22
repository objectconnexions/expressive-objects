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

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.ParseableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractObjectProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.HelpLink;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.field.LinkedFieldsBlock;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.field.LinkedObject;

public abstract class AbstractFormView extends AbstractObjectProcessor {

    @Override
    public String checkFieldType(final ObjectAssociation objectField) {
        return objectField.isOneToOneAssociation() ? null : "is not an object";
    }

    @Override
    public void process(final Request request, final ObjectAdapter object) {
        final LinkedFieldsBlock tag = new LinkedFieldsBlock();

        if (object != null) {
            final String id = request.getOptionalProperty(ID, object.getSpecification().getShortIdentifier()); 
            final String cls = request.getOptionalProperty(CLASS, "form");
            final String classString = " id=\"" + id + "\" class=\"" + cls + "\"";
            String title = request.getOptionalProperty(FORM_TITLE);
            final String oddRowClass = request.getOptionalProperty(ODD_ROW_CLASS);
            final String evenRowClass = request.getOptionalProperty(EVEN_ROW_CLASS);
            final String labelDelimiter = request.getOptionalProperty(LABEL_DELIMITER, ":");
            final boolean showIcons = request.isRequested(SHOW_ICON, showIconByDefault()); 
            String linkAllView = request.getOptionalProperty(LINK_VIEW);

            request.setBlockContent(tag);
            request.processUtilCloseTag();

            final AuthenticationSession session = ExpressiveObjectsContext.getAuthenticationSession(); 
            List<ObjectAssociation> associations = object.getSpecification().getAssociations(ObjectAssociationFilters.dynamicallyVisible(session, object, Where.ANYWHERE));
            final List<ObjectAssociation> fields = tag.includedFields(associations);
            final LinkedObject[] linkFields = tag.linkedFields(fields);

            if (linkAllView != null) {
                linkAllView = request.getContext().fullUriPath(linkAllView);
                for (int i = 0; i < linkFields.length; i++) {
                    final boolean isObject = fields.get(i).isOneToOneAssociation() || fields.get(i).isOneToManyAssociation();
                    final boolean isNotParseable = !fields.get(i).getSpecification().containsFacet(ParseableFacet.class);
                    linkFields[i] = isObject && isNotParseable ? new LinkedObject(linkAllView) : null;
                }
            }

            if (title == null) {
                title = object.getSpecification().getSingularName();
            } else if (title.equals("")) {
                title = null;
            }

            write(request, object, fields, linkFields, classString, title, labelDelimiter, oddRowClass, evenRowClass, showIcons);
        } else {
            request.skipUntilClose(); 
        }
    }

    private void write(
            final Request request,
            final ObjectAdapter object,
            final List<ObjectAssociation> fields,
            final LinkedObject[] linkFields,
            final String classString,
            final String title,
            final String labelDelimiter,
            final String oddRowClass,
            final String evenRowClass,
            final boolean showIcons) {
        request.appendHtml("<div" + classString + ">");
        if (title != null) {
            request.appendHtml("<div class=\"title\">");
            request.appendAsHtmlEncoded(title);
            request.appendHtml("</div>");
            HelpLink.append(request, object.getSpecification().getDescription(), object.getSpecification().getHelp());
        }
        int row = 1;
        for (int i = 0; i < fields.size(); i++) {
            final ObjectAssociation field = fields.get(i);
            if (ignoreField(field)) {
                continue;
            }
            if (field.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, Where.OBJECT_FORMS).isVetoed()) {
                continue;
            }

            final String description = field.getDescription().equals("") ? "" : "title=\"" + field.getDescription() + "\"";
            String cls;
            if (row++ % 2 == 1) {
                cls = " class=\"field " + (oddRowClass == null ? ODD_ROW_CLASS : oddRowClass) + "\"";
            } else {
                cls = " class=\"field " + (evenRowClass == null ? EVEN_ROW_CLASS : evenRowClass) + "\"";
            }
            request.appendHtml("<div " + cls + description + "><span class=\"label\">");
            request.appendAsHtmlEncoded(field.getName());
            request.appendHtml(labelDelimiter + "</span>");
            final LinkedObject linkedObject = linkFields[i];
            addField(request, object, field, linkedObject, showIcons);
            HelpLink.append(request, field.getDescription(), field.getHelp());
            request.appendHtml("</div>");
        }
        request.appendHtml("</div>");
    }

    protected void addField(final Request request, final ObjectAdapter object, final ObjectAssociation field, final LinkedObject linkedObject, final boolean showIcons) {
        FieldValue.write(request, object, field, linkedObject, null, showIcons, 0);
    }

    protected boolean ignoreField(final ObjectAssociation objectField) {
        return false;
    }

}
