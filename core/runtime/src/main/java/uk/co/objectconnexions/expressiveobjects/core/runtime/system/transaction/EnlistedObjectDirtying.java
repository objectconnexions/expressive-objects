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

package uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.Dirtiable;

/**
 * As called by the {@link ExpressiveObjectsTransactionManager}.
 * 
 * <p>
 * Dirtiable support.
 */
public interface EnlistedObjectDirtying {

    /**
     * Mark as {@link #objectChanged(ObjectAdapter) changed } all
     * {@link Dirtiable} objects that have been
     * {@link Dirtiable#markDirty(ObjectAdapter) manually marked} as dirty.
     * 
     * <p>
     * Called by the {@link ExpressiveObjectsTransactionManager}.
     */
    void objectChangedAllDirty();

    /**
     * Set as {@link Dirtiable#clearDirty(ObjectAdapter) clean} any
     * {@link Dirtiable} objects.
     */
    void clearAllDirty();

}