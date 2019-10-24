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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks;

/**
 * Represents the mechanism to inform the object that it has just been created.
 * 
 * <p>
 * In the standard Expressive Objects Programming Model, this is represented by a
 * <tt>created</tt> method. The framework calls this once the object has been
 * created via <tt>newTransientInstance</tt> or <tt>newInstance</tt>. The method
 * is <i>not</i> called when the object is subsequently resolved having been
 * persisted; for that see {@link LoadingCallbackFacet} and
 * {@link LoadedCallbackFacet}.
 * 
 * @see LoadingCallbackFacet
 * @see LoadedCallbackFacet
 */
public interface CreatedCallbackFacet extends CallbackFacet {

}
