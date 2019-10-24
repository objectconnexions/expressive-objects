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

package uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.singleuser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequestAbstract;

/**
 * Requests corresponding to an {@link SingleUserSession}.
 */
public class AuthenticationRequestSingleUser extends AuthenticationRequestAbstract {

    private static final String SINGLE_USER_NAME = "self";
    private static final String SINGLE_USER_ROlE_NAME = "default_role";
    private final List<String> roles;

    public AuthenticationRequestSingleUser() {
        super(SINGLE_USER_NAME);
        roles = Collections.unmodifiableList(Arrays.asList(SINGLE_USER_ROlE_NAME));
    }

    @Override
    public List<String> getRoles() {
        return roles;
    }

}
