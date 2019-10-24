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

/**
 * This package defines the {@link uk.co.objectconnexions.expressiveobjects.applib.query.Query} interface
 * and supporting implementations.
 * 
 * <p>
 * The {@link uk.co.objectconnexions.expressiveobjects.applib.query.Query} concept is provided as a
 * standardized mechanism by which 
 * {@link uk.co.objectconnexions.expressiveobjects.applib.AbstractFactoryAndRepository repositories}
 * or indeed any {@link uk.co.objectconnexions.expressiveobjects.applib.AbstractDomainObject domain object}
 * can submit.  Object store implementation are generally expected to support
 * the {@link uk.co.objectconnexions.expressiveobjects.applib.query.QueryBuiltInAbstract built-in queries},
 * meaning that there may not be any need to provide different implementations
 * of the repositories.  (This provides some of the benefits that .NET has
 * with its LINQ technology, though it is admittedly nowhere near as 
 * sophisticated).
 */
package uk.co.objectconnexions.expressiveobjects.applib.query;