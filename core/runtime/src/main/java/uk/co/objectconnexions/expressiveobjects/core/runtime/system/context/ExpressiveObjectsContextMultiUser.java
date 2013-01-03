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

package uk.co.objectconnexions.expressiveobjects.core.runtime.system.context;

import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactory;

/**
 * Provides <i>access to</i> the current {@link ExpressiveObjectsSession} in a multi-user
 * deployment.
 */
public abstract class ExpressiveObjectsContextMultiUser extends ExpressiveObjectsContext {

    // //////////////////////////////////////////////
    // Constructor
    // //////////////////////////////////////////////

    protected ExpressiveObjectsContextMultiUser(final ExpressiveObjectsSessionFactory sessionFactory) {
        this(ContextReplacePolicy.NOT_REPLACEABLE, SessionClosePolicy.EXPLICIT_CLOSE, sessionFactory);
    }

    protected ExpressiveObjectsContextMultiUser(final ContextReplacePolicy contextReplacePolicy, final SessionClosePolicy sessionClosePolicy, final ExpressiveObjectsSessionFactory sessionFactory) {
        super(contextReplacePolicy, sessionClosePolicy, sessionFactory);
    }

}
