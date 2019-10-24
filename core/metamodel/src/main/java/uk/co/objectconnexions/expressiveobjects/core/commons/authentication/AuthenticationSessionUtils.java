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

package uk.co.objectconnexions.expressiveobjects.core.commons.authentication;

import java.util.List;

import com.google.common.collect.Lists;

import uk.co.objectconnexions.expressiveobjects.applib.security.RoleMemento;
import uk.co.objectconnexions.expressiveobjects.applib.security.UserMemento;

public final class AuthenticationSessionUtils {

    private AuthenticationSessionUtils() {
    }

    public static UserMemento createUserMemento(final AuthenticationSession session) {
        final List<RoleMemento> roles = Lists.newArrayList();
        for (final String roleName : session.getRoles()) {
            roles.add(new RoleMemento(roleName));
        }
        return new UserMemento(session.getUserName(), roles);
    }
}
