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

package uk.co.objectconnexions.expressiveobjects.security.sql.authorization;

import uk.co.objectconnexions.expressiveobjects.applib.Identifier;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.standard.AuthorizorAbstract;

public class SqlAuthorizor extends AuthorizorAbstract {
    // TODO: Need to implement this class.

    // private static final Logger LOG = Logger.getLogger(SqlAuthorizor.class);

    public SqlAuthorizor(final ExpressiveObjectsConfiguration configuration) {
        super(configuration);

    }

    // //////////////////////////////////////////////////////////////
    // init, shutdown
    // //////////////////////////////////////////////////////////////

    @Override
    public void init() {
        // does nothing
    }

    @Override
    public void shutdown() {
        // does nothing
    }

    // //////////////////////////////////////////////////////////////
    // API
    // //////////////////////////////////////////////////////////////

    @Override
    public boolean isUsableInRole(final String role, final Identifier identifier) {
        return true;
    }

    @Override
    public boolean isVisibleInRole(final String user, final Identifier identifier) {
        return true;
    }

    @Override
    public boolean isVisibleInAnyRole(Identifier identifier) {
        return true;
    }

    @Override
    public boolean isUsableInAnyRole(Identifier identifier) {
        return true;
    }
}
