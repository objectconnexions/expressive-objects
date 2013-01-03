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
 * Provides an {@link uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSource abstraction}
 * over {@link java.io.InputStream}s for accessing
 * resources.
 * 
 * <p>
 * The principle usage is to allow the easy searching of a single resource (eg
 * <tt>expressive-objects.properties</tt> file) in {@link uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceContextLoaderClassPath multiple}
 * {@link uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceCurrentClassClassPath locations} 
 * and {@link uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceFileSystem technologies} 
 * on the classpath through
 * the use of a {@link uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceChainOfResponsibility composite}
 * pattern.
 */
package uk.co.objectconnexions.expressiveobjects.core.commons.resource;