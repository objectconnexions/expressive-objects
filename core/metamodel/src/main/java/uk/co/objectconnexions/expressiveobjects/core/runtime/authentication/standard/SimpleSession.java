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

package uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionAbstract;
import uk.co.objectconnexions.expressiveobjects.core.commons.encoding.DataInputExtended;

public final class SimpleSession extends AuthenticationSessionAbstract {

    private static final long serialVersionUID = 1L;

    // ///////////////////////////////////////////////////////////////
    // Constructor, encode
    // ///////////////////////////////////////////////////////////////

    /**
     * as per {@link #SimpleSession(String, List)}.
     */
    public SimpleSession(final String name, final String[] roles) {
        this(name, Arrays.asList(roles));
    }

    /**
     * Defaults {@link #getValidationCode()} to empty string (<tt>""</tt>).
     */
    public SimpleSession(final String name, final List<String> roles) {
        this(name, roles, "");
    }

    public SimpleSession(final String name, final String[] roles, final String code) {
        this(name, Arrays.asList(roles), code);
    }

    public SimpleSession(final String name, final List<String> roles, final String code) {
        super(name, roles, code);
    }

    public SimpleSession(final DataInputExtended input) throws IOException {
        super(input);
    }

    // ///////////////////////////////////////////////////////////////
    // equals, hashCode
    // ///////////////////////////////////////////////////////////////

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final SimpleSession other = (SimpleSession) obj;
        return equals(other);
    }

    public boolean equals(final SimpleSession other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        return getUserName().equals(other.getUserName());
    }

    @Override
    public int hashCode() {
        return getUserName().hashCode();
    }

}
