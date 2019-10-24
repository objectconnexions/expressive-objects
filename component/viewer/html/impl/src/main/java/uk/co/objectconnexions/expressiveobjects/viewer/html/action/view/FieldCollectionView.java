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

package uk.co.objectconnexions.expressiveobjects.viewer.html.action.view;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToManyAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.view.util.TableUtil;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.Table;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.ViewPane;
import uk.co.objectconnexions.expressiveobjects.viewer.html.context.Context;
import uk.co.objectconnexions.expressiveobjects.viewer.html.request.Request;

public class FieldCollectionView extends ObjectViewAbstract {
    
    private final Where where = Where.PARENTED_TABLES;

    @Override
    protected void doExecute(final Context context, final ViewPane content, final ObjectAdapter object, final String field) {
        final String id = context.mapObject(object);
        final ObjectSpecification specification = object.getSpecification();

        final OneToManyAssociation collection = (OneToManyAssociation) specification.getAssociation(field);

        ExpressiveObjectsContext.getPersistenceSession().resolveField(object, collection);

        context.addCollectionFieldCrumb(collection.getName());
        content.add(context.getComponentFactory().createHeading(collection.getName()));
        final Table table = TableUtil.createTable(context, id, object, collection);
        content.add(table);
        if (collection.isUsable(ExpressiveObjectsContext.getAuthenticationSession(), object, where).isAllowed()) {
            content.add(context.getComponentFactory().createAddOption(id, collection.getId()));
        }
    }

    @Override
    public String name() {
        return Request.FIELD_COLLECTION_COMMAND;
    }

}
