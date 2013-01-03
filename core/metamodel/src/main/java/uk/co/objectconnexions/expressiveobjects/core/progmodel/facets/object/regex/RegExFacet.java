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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.regex;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.MultipleValueFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidatingInteractionAdvisor;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.mask.MaskFacet;

/**
 * Whether the (string) property or a parameter must correspond to a specific
 * regular expression.
 * 
 * <p>
 * In the standard Expressive Objects Programming Model, corresponds to the
 * <tt>@RegEx</tt> annotation.
 * 
 * @see MaskFacet
 */
public interface RegExFacet extends MultipleValueFacet, ValidatingInteractionAdvisor {

    public String validation();

    public String format();

    public boolean caseSensitive();

    public boolean doesNotMatch(String proposed);

    public String format(String text);

}
