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

package uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.standard;

import uk.co.objectconnexions.expressiveobjects.applib.Identifier;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.ApplicationScopedComponent;

public interface Authorizor extends ApplicationScopedComponent {

    public boolean isVisibleInAnyRole(final Identifier identifier);
    public boolean isUsableInAnyRole(final Identifier identifier);

    /**
     * Checked for each of the user's roles.
     */
    public boolean isVisibleInRole(final String role, final Identifier identifier);

    /**
     * Checked for each of the user's roles.
     */
    public boolean isUsableInRole(final String role, final Identifier identifier);


}
