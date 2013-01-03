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

package uk.co.objectconnexions.expressiveobjects.applib.fixtures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.fixtures.switchuser.SwitchUserService;
import uk.co.objectconnexions.expressiveobjects.applib.fixtures.switchuser.SwitchUserServiceAware;

/**
 * Convenience class for creating fixtures, with support to allow
 * users to be switching using the {@link SwitchUserService}.
 * 
 * <p>
 * Note that unlike {@link AbstractFixture}, fixtures inheriting from
 * this class <i>cannot</i> be used as domain objects (the {@link SwitchUserService} does
 * not conform to the domain object programming conventions).
 */
public abstract class AbstractFixtureSusa extends AbstractFixture implements SwitchUserServiceAware {

    public AbstractFixtureSusa() {
        super();
    }

    public AbstractFixtureSusa(final FixtureType fixtureType) {
        super(fixtureType);
    }


    // {{ User
    protected void switchUser(final String username, final String... roles) {
        switchUserService.switchUser(username, roles);
    }

    // }}

    // {{ Injected: SwitchUserService
    private SwitchUserService switchUserService;

    @Override
    public void setService(final SwitchUserService fixtureService) {
        this.switchUserService = fixtureService;
    }
    // }}

}
