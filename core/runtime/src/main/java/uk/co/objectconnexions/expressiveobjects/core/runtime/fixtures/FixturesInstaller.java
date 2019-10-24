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

package uk.co.objectconnexions.expressiveobjects.core.runtime.fixtures;

import uk.co.objectconnexions.expressiveobjects.applib.fixtures.InstallableFixture;
import uk.co.objectconnexions.expressiveobjects.applib.fixtures.LogonFixture;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.Installer;

/**
 * A particular mechanism to install fixtures.
 */
public interface FixturesInstaller extends Installer {

    /**
     * NB: this has the suffix '-installer' because in the command line we must
     * distinguish from the '--fixture' flag meaning a particular fixture to be
     * installed.
     */
    static String TYPE = "fixtures-installer";

    void installFixtures();

    /**
     * The {@link InstallableFixture} (if any) that is an instance of
     * {@link LogonFixture}.
     * 
     * <p>
     * If there is more than one {@link LogonFixture}, then the last one
     * installed is returned.
     */
    LogonFixture getLogonFixture();
}
