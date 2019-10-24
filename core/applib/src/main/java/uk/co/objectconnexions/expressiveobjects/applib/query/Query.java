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

package uk.co.objectconnexions.expressiveobjects.applib.query;

import java.io.Serializable;

import uk.co.objectconnexions.expressiveobjects.applib.DomainObjectContainer;
import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;

/**
 * For use by repository implementations, representing the values of a query.
 * 
 * <p>
 * The implementations of these objects are be provided by the underlying
 * persistor/object store; consult its documentation.
 * 
 * <p>
 * <b>Note:</b> that not every object store will necessarily support this
 * interface. In particular, the in-memory object store does not. For this, you
 * can use the {@link Filter} interface to similar effect, for example in
 * {@link DomainObjectContainer#allMatches(Class, Filter)}). Note that the
 * filtering is done within the {@link DomainObjectContainer} rather than being
 * pushed back to the object store.
 */
public interface Query<T> extends Serializable {

    /**
     * The {@link Class} of the objects returned by this query.
     */
    public Class<T> getResultType();

    /**
     * A human-readable representation of this query and its values.
     */
    public String getDescription();
}
