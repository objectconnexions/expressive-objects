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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ScimpiException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request.RepeatMarker;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.util.CollectionUtils;

public class Collection extends AbstractElementProcessor {

    @Override
    public void process(final Request request) {
        final RequestContext context = request.getContext();

        ObjectAdapter collection;

        final String field = request.getOptionalProperty(FIELD);
        if (field != null) {
            final String id = request.getOptionalProperty(OBJECT);
            final ObjectAdapter object = context.getMappedObjectOrResult(id);
            final ObjectAssociation objectField = object.getSpecification().getAssociation(field);
            if (!objectField.isOneToManyAssociation()) {
                throw new ScimpiException("Field " + objectField.getId() + " is not a collection");
            }
            ExpressiveObjectsContext.getPersistenceSession().resolveField(object, objectField);
            collection = objectField.get(object);
        } else {
            final String id = request.getOptionalProperty(COLLECTION);
            collection = context.getMappedObjectOrResult(id);
        }

        final RepeatMarker marker = request.createMarker();

        final String variable = request.getOptionalProperty(ELEMENT_NAME);
        final String order = request.getOptionalProperty("sort");
        final String scopeName = request.getOptionalProperty(SCOPE);
        final Scope scope = RequestContext.scope(scopeName, Scope.REQUEST);
        final String rowClassesList = request.getOptionalProperty(ROW_CLASSES, ODD_ROW_CLASS + "|" + EVEN_ROW_CLASS);
        String[] rowClasses = new String[0];
        if (rowClassesList != null) {
            rowClasses = rowClassesList.split("[,|/]");
        }

        final CollectionFacet facet = collection.getSpecification().getFacet(CollectionFacet.class);
        if (facet.size(collection) == 0) {
            request.skipUntilClose();
        } else {
        	List<ObjectAdapter> elements = new ArrayList<ObjectAdapter>();
            final Iterator<ObjectAdapter> iterator = facet.iterator(collection);
            while (iterator.hasNext()) {
                final ObjectAdapter element = iterator.next();
                elements.add(element);
            }
            
            CollectionUtils.sort(elements, collection.getElementSpecification(), order);
            
            int row = 0;
            for (ObjectAdapter element : elements) {
                context.addVariable("row", "" + (row + 1), Scope.REQUEST);
                if (rowClassesList != null) {
                    context.addVariable("row-class", rowClasses[row % rowClasses.length], Scope.REQUEST);
                }
                context.addVariable(variable, context.mapObject(element, scope), scope);
                marker.repeat();
                request.processUtilCloseTag();
                row++;
            }
        }
    }

    @Override
    public String getName() {
        return COLLECTION;
    }

}
