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

package uk.co.objectconnexions.expressiveobjects.core.runtime.authorization;

import uk.co.objectconnexions.expressiveobjects.applib.Identifier;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.ApplicationScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;

/**
 * Authorises the user in the current session view and use members of an object.
 */
public interface AuthorizationManager extends ApplicationScopedComponent {

    /**
     * Returns true when the user represented by the specified session is
     * authorised to view the member of the class/object represented by the
     * member identifier. Normally the view of the specified field, or the
     * display of the action will be suppress if this returns false.
     */
    boolean isVisible(AuthenticationSession session, ObjectAdapter target, Identifier identifier);

    /**
     * Returns true when the use represented by the specified session is
     * authorised to change the field represented by the member identifier.
     * Normally the specified field will be not appear editable if this returns
     * false.
     */
    boolean isUsable(AuthenticationSession session, ObjectAdapter target, Identifier identifier);
}
