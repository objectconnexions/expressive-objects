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

package uk.co.objectconnexions.expressiveobjects.viewer.html.action.view.util;

import static uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters.PROPERTIES;
import static uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters.WHEN_VISIBLE_IRRESPECTIVE_OF_WHERE;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.applib.filter.Filters;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.multiline.MultiLineFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToManyAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.Persistor;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.ComponentFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.Table;
import uk.co.objectconnexions.expressiveobjects.viewer.html.context.Context;

public class TableUtil {

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with
    // @Hidden(where=Where.ALL_TABLES) or @Disabled(where=Where.ALL_TABLES) will indeed
    // be hidden from all tables but will be visible/enabled (perhaps incorrectly) 
    // if annotated with Where.PARENTED_TABLE or Where.STANDALONE_TABLE
    private final static Where where = Where.ALL_TABLES;

    public static Table createTable(final Context context, final String id, final ObjectAdapter object, final OneToManyAssociation collectionField) {

        final ObjectAdapter collection = collectionField.get(object);
        final String name = collectionField.getName();
        final ObjectSpecification type = collectionField.getSpecification();

        final String summary = "Table showing elements of " + name + " field";
        return createTable(context, collectionField != null, collection, summary, type);
    }

    public static Table createTable(final Context context, final boolean addSelector, final ObjectAdapter collection, final String summary, final ObjectSpecification elementType) {

        final CollectionFacet facet = CollectionFacetUtils.getCollectionFacetFromSpec(collection);
        final List<ObjectAssociation> columnAssociations = elementType.getAssociations(Filters.and(WHEN_VISIBLE_IRRESPECTIVE_OF_WHERE, PROPERTIES));

        final int len = columnAssociations.size();

        final ComponentFactory factory = context.getComponentFactory();
        final Table table = factory.createTable(len, addSelector);
        table.setSummary(summary);

        for (final ObjectAssociation columnAssociation : columnAssociations) {
            table.addColumnHeader(columnAssociation.getName());
        }

        for (final ObjectAdapter rowAdapter : facet.iterable(collection)) {
            getPersistenceSession().resolveImmediately(rowAdapter);
            final String elementId = context.mapObject(rowAdapter);
            table.addRowHeader(factory.createObjectIcon(rowAdapter, elementId, "icon"));

            for (final ObjectAssociation columnAssociation : columnAssociations) {
                final ObjectAdapter columnAdapter = columnAssociation.get(rowAdapter);

                final ObjectSpecification columnSpec = columnAssociation.getSpecification();

                if (!columnAssociation.isVisible(getAuthenticationSession(), rowAdapter, where).isAllowed()) {
                    table.addEmptyCell();
                } else if (columnSpec.isParseable()) {
                    final MultiLineFacet multiline = columnSpec.getFacet(MultiLineFacet.class);
                    final boolean shouldTruncate = multiline != null && multiline.numberOfLines() > 1;
                    final String titleString = columnAdapter != null ? columnAdapter.titleString() : "";
                    table.addCell(titleString, shouldTruncate);
                } else if (columnAdapter == null) {
                    table.addEmptyCell();
                } else {
                    getPersistenceSession().resolveImmediately(columnAdapter);
                    final String objectId = context.mapObject(columnAdapter);
                    table.addCell(factory.createObjectIcon(columnAssociation, columnAdapter, objectId, "icon"));
                }
            }
            /*
             * if (addSelector) {
             * table.addCell(context.getFactory().createRemoveOption(id,
             * elementId, collectionField.getId())); }
             */
            // TODO add selection box
            // table.addCell();
            /*
             * if (collectionField != null) { if
             * (collectionField.isValidToRemove(object, element).isAllowed()) {
             * table.addCell(context.getFactory().createRemoveOption(id,
             * elementId, collectionField.getId())); } else {
             * table.addEmptyCell(); } }
             */

        }
        return table;
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // Dependencies (from context)
    // ////////////////////////////////////////////////////////////////////////////////

    private static Persistor getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    private static AuthenticationSession getAuthenticationSession() {
        return ExpressiveObjectsContext.getAuthenticationSession();
    }

}
