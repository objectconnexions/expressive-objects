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

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.runtime.transaction.facets.CollectionClearFacetWrapTransaction;

public class TransactionUtils {
    private final static Logger LOG = Logger.getLogger(CollectionClearFacetWrapTransaction.class);

    private TransactionUtils() {
    }

    public static void abort(final ExpressiveObjectsTransactionManager transactionManager, final FacetHolder holder) {
        LOG.info("exception executing " + holder + ", aborting transaction");
        try {
            transactionManager.abortTransaction();
        } catch (final Exception e2) {
            LOG.error("failure during abort", e2);
        }
    }

    /**
     * TODO: need to downcast the FacetHolder and fetch out an identifier.
     */
    public static String getIdentifierFor(final FacetHolder facetHolder) {
        return facetHolder.toString();
    }

}
