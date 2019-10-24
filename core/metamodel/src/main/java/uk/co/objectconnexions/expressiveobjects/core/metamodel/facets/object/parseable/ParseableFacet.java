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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.MultipleValueFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.title.TitleFacet;

/**
 * Indicates that this class can parse an entry string.
 */
public interface ParseableFacet extends MultipleValueFacet {

    /**
     * Parses a text entry made by a user and sets the domain object's value.
     * 
     * <p>
     * Equivalent to <tt>Parser#parseTextEntry(Object, String)</tt>, though may
     * be implemented through some other mechanism.
     * @param localization TODO
     * 
     * @throws InvalidEntryException
     * @throws TextEntryParseException
     */
    ObjectAdapter parseTextEntry(ObjectAdapter original, String text, Localization localization);

    /**
     * A title for the object that is valid but which may be easier to edit than
     * the title provided by a {@link TitleFacet}.
     * 
     * <p>
     * The idea here is that the viewer can display a parseable title for an
     * existing object when, for example, the user initially clicks in the
     * field. So, a date might be rendered via a {@link TitleFacet} as
     * <tt>May 2, 2007</tt>, but its parseable form might be <tt>20070502</tt>.
     */
    public String parseableTitle(ObjectAdapter obj);
}
