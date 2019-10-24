/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.bookmarks.Bookmark;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.Injectable;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.AggregatedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContextAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;

public interface DomainObjectServices extends Injectable {

    // ///////////////////////////////////////////
    // Instantiate
    // ///////////////////////////////////////////

    /**
     * Provided by the <tt>PersistenceSession</tt> when used by framework.
     * 
     * <p>
     * Called by <tt>DomainObjectContainerDefault</tt>.
     */
    ObjectAdapter createTransientInstance(ObjectSpecification spec);

    /**
     * Create an instance of an aggregated object that will be persisted within the
     * parent adapter.
     * 
     * <p>
     * The {@link Oid} of the returned {@link ObjectAdapter adapter} will be of type
     * {@link AggregatedOid}.  The oid's {@link AggregatedOid#getLocalId() localId}
     * is generated as per the configured objectstore (<tt>OidGenerator</tt>).
     * 
     * <p>
     * Called by <tt>DomainObjectContainerDefault</tt>.
     */
    ObjectAdapter createAggregatedInstance(ObjectSpecification spec, ObjectAdapter parent);

    // ///////////////////////////////////////////
    // retrieve
    // ///////////////////////////////////////////

    /**
     * Provided by <tt>PersistenceSession</tt> when used by framework.
     * 
     * <p>
     * Called by <tt>DomainObjectContainerDefault</tt>.
     */
    void resolve(Object parent);

    /**
     * Provided by <tt>PersistenceSession</tt> when used by framework.
     * 
     * <p>
     * Called by <tt>DomainObjectContainerDefault</tt>.
     */
    void resolve(Object parent, Object field);

    /**
     * Provided by <tt>PersistenceSession</tt> when used by framework.
     * 
     * <p>
     * Called by <tt>BookmarkServicesDefault</tt>.
     * @return 
     */
    Object lookup(Bookmark bookmark);

    Bookmark bookmarkFor(Object domainObject);


    // ///////////////////////////////////////////
    // flush, commit
    // ///////////////////////////////////////////

    /**
     * Provided by <tt>TransactionManager</tt> when used by framework.
     * 
     * <p>
     * Called by <tt>DomainObjectContainerDefault</tt>.
     */
    boolean flush();

    /**
     * Provided by <tt>TransactionManager</tt> when used by framework.
     * 
     * <p>
     * Called by <tt>DomainObjectContainerDefault</tt>.
     */
    void commit();

    // //////////////////////////////////////////////////////////////////
    // info, warn, error messages
    // //////////////////////////////////////////////////////////////////

    /**
     * Provided by <tt>MessageBroker</tt> when used by framework.
     * 
     * <p>
     * Called by <tt>DomainObjectContainerDefault</tt>.
     */
    void informUser(String message);

    /**
     * Provided by <tt>MessageBroker</tt> when used by framework.
     * 
     * <p>
     * Called by <tt>DomainObjectContainerDefault</tt>.
     */
    void warnUser(String message);

    /**
     * Provided by <tt>MessageBroker</tt> when used by framework.
     * 
     * <p>
     * Called by <tt>DomainObjectContainerDefault</tt>.
     */
    void raiseError(String message);

    // //////////////////////////////////////////////////////////////////
    // properties
    // //////////////////////////////////////////////////////////////////

    /**
     * Provided by {@link RuntimeContextAbstract} itself, cloned properties from
     * {@link ExpressiveObjectsConfiguration}.
     * 
     * <p>
     * Called by <tt>DomainObjectContainerDefault</tt>.
     */
    String getProperty(String name);

    /**
     * Provided by {@link RuntimeContextAbstract} itself, cloned properties from
     * {@link ExpressiveObjectsConfiguration}.
     * 
     * <p>
     * Called by <tt>DomainObjectContainerDefault</tt>.
     */
    List<String> getPropertyNames();


}
