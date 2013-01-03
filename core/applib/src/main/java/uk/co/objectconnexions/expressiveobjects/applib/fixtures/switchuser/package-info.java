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
 * Provides support for switching the current user while fixtures are
 * being loaded.
 * 
 * <p>
 * Not expected to be used by domain objects, but decouples the fixtures from
 * the framework (which provides the implementation of {@link uk.co.objectconnexions.expressiveobjects.applib.fixtures.switchuser.SwitchUserService}).
 */
package uk.co.objectconnexions.expressiveobjects.applib.fixtures.switchuser;