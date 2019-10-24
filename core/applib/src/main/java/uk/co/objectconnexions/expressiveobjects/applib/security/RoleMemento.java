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

package uk.co.objectconnexions.expressiveobjects.applib.security;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.MemberOrder;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.NotPersistable;

@NotPersistable
public final class RoleMemento {

    /**
     * Creates a new role with the specified name. Description is left blank.
     */
    public RoleMemento(final String name) {
        this(name, "");
    }

    /**
     * Creates a new role with the specified name and description.
     */
    public RoleMemento(final String name, final String description) {
        if (name == null) {
            throw new IllegalArgumentException("Name not specified");
        }
        this.name = name;
        if (description == null) {
            throw new IllegalArgumentException("Description not specified");
        }
        this.description = description;
    }

    // {{ Identification
    public String title() {
        return name;
    }

    // }}

    // {{ Name
    private final String name;

    @MemberOrder(sequence = "1.1")
    public String getName() {
        return name;
    }

    // }}

    // {{ Description
    private final String description;

    @MemberOrder(sequence = "1.2")
    public String getDescription() {
        return description;
    }
    // }}
}
