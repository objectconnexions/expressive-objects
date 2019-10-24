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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.consent;

import static uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Ensure.ensureThatArg;
import static uk.co.objectconnexions.expressiveobjects.core.commons.matchers.ExpressiveObjectsMatchers.nonEmptyString;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;

public class Veto extends ConsentAbstract {

    private static final long serialVersionUID = 1L;

    public static Veto DEFAULT = new Veto("Vetoed by default");

    /**
     * Called by DnD viewer; we should instead find a way to put the calling
     * logic into {@link Facet}s so that it is available for use by other
     * viewers.
     * 
     * @param reasonVeteod
     *            - must not be <tt>null</tt>
     */
    public Veto(final String reasonVetoed) {
        super(null, ensureThatArg(reasonVetoed, nonEmptyString()));
    }

    public Veto(final InteractionResult interactionResult) {
        super(interactionResult);
    }

}
