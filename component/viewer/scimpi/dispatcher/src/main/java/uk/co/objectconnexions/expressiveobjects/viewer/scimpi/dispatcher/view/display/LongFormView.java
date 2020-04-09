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

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display.TableView.SimpleTableBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.field.LinkedObject;

public class LongFormView extends AbstractFormView {

    @Override
    protected void addField(final Request request, final ObjectAdapter object, final ObjectAssociation field, final LinkedObject linkedObject, final boolean showIcons) {
        if (field.isOneToManyAssociation()) {
            final String noColumnsString = request.getOptionalProperty("no-columns", "3");
            final String tableClass = request.getOptionalProperty("table-class");
            final String rowClassesList = request.getOptionalProperty("row-classes", ODD_ROW_CLASS + "|" + EVEN_ROW_CLASS);
            String[] rowClasses = new String[0];
            if (rowClassesList != null) {
                rowClasses = rowClassesList.split("[,|/]");
            }
            int noColumns;
            ExpressiveObjectsContext.getPersistenceSession().resolveField(object, field);
            final ObjectAdapter collection = field.get(object);
            final ObjectSpecification elementSpec = collection.getElementSpecification();
            final List<ObjectAssociation> fields = elementSpec.getAssociations(ObjectAssociationFilters.WHEN_VISIBLE_IRRESPECTIVE_OF_WHERE);
            if (noColumnsString.equalsIgnoreCase("all")) {
                noColumns = fields.size();
            } else {
                noColumns = Math.min(fields.size(), Integer.valueOf(noColumnsString));
            }
            // final boolean isFieldEditable = field.isUsable(ExpressiveObjectsContext.getAuthenticationSession(), object).isAllowed();
            final String summary = "Table of elements in " + field.getName();
            // TableView.write(request, summary, object, field, collection, noColumns, fields, isFieldEditable, showIconByDefault(), tableClass, rowClasses, linkedObject);
            
            
            final String headers[] = new String[fields.size()];
            int h = 0;
            for (int i = 0; i < noColumns; i++) {
                if (fields.get(i).isOneToManyAssociation()) {
                    continue;
                }
                headers[h++] = fields.get(i).getName();
            }
            
            final LinkedObject[] linkedFields = new LinkedObject[fields.size()];

            final CollectionFacet facet = collection.getSpecification().getFacet(CollectionFacet.class);
            int size = facet.size(collection);
            if (size == 0) {
                request.appendHtml("<div class=\"empty-collection\">No " + collection.getElementSpecification().getPluralName() + "</div>");
            } else {
                final TableContentWriter rowBuilder =new SimpleTableBuilder(object.titleString(), true, false, null, noColumns, headers, fields, false,
                        showIcons, false, false, false, field.getName(), linkedFields, linkedObject);
                TableView.write(request, collection, summary, rowBuilder, null, "", tableClass, rowClasses);
            }
        } else {
            super.addField(request, object, field, linkedObject, showIcons);
        }
    }

    @Override
    public String getName() {
        return "long-form";
    }

}
