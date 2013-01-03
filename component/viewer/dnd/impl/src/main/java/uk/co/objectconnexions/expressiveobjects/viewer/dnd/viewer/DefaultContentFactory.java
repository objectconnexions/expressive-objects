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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer;

import uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Assert;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToManyAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ContentFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.collection.RootCollection;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.RootObject;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.field.OneToManyFieldImpl;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.field.OneToOneFieldImpl;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.field.TextParseableFieldImpl;

public class DefaultContentFactory implements ContentFactory {

    @Override
    public Content createRootContent(final ObjectAdapter object) {
        Assert.assertNotNull(object);
        final ObjectSpecification objectSpec = object.getSpecification();
        if (objectSpec.isParentedOrFreeCollection()) {
            return new RootCollection(object);
        }
        if (objectSpec.isNotCollection()) {
            return new RootObject(object);
        }

        throw new IllegalArgumentException("Must be an object or collection: " + object);
    }

    @Override
    public Content createFieldContent(final ObjectAssociation field, final ObjectAdapter object) {
        Content content;
        final ObjectAdapter associatedObject = field.get(object);
        if (field instanceof OneToManyAssociation) {
            content = new OneToManyFieldImpl(object, associatedObject, (OneToManyAssociation) field);
        } else if (field instanceof OneToOneAssociation) {
            final ObjectSpecification fieldSpecification = field.getSpecification();
            if (fieldSpecification.isParseable()) {
                content = new TextParseableFieldImpl(object, associatedObject, (OneToOneAssociation) field);
            } else {
                content = new OneToOneFieldImpl(object, associatedObject, (OneToOneAssociation) field);
            }
        } else {
            throw new ExpressiveObjectsException();
        }

        return content;
    }

}
