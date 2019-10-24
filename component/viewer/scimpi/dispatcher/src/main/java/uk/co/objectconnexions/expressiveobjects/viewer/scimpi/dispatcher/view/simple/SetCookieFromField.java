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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple;

import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.NotYetImplementedException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class SetCookieFromField extends AbstractElementProcessor {

    @Override
    public void process(final Request request) {
        throw new NotYetImplementedException("3.1");
        /*
         * String objectId = request.getOptionalProperty(OBJECT); String
         * fieldName = request.getRequiredProperty(FIELD);
         * 
         * ObjectAdapter object = (ObjectAdapter)
         * request.getContext().getMappedObjectOrResult(objectId);
         * ObjectAssociation field =
         * object.getSpecification().getField(fieldName); if (field.isValue()) {
         * throw new ScimpiException("Can only set up a value field"); }
         * ObjectAdapter value = field.get(object); if (value != null) { String
         * title = value.titleString();
         * 
         * if (title.length() > 0) { String name =
         * request.getRequiredProperty(NAME); String expiresString =
         * request.getOptionalProperty("expires", "-1");
         * request.getContext().addCookie(name, title,
         * Integer.valueOf(expiresString)); } }
         */
    }

    @Override
    public String getName() {
        return "set-cookie-from-field";
    }
}
