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

import uk.co.objectconnexions.expressiveobjects.applib.AbstractContainedObject;
import uk.co.objectconnexions.expressiveobjects.applib.clock.Clock;

abstract class BaseFixture extends AbstractContainedObject implements InstallableFixture {

    private final FixtureType fixtureType;
    FixtureClock clock = null;

    public BaseFixture(final FixtureType fixtureType) {
        this.fixtureType = fixtureType;
        try {
            clock = FixtureClock.initialize();
        } catch (final IllegalStateException ex) {
            clock = null;
            System.err.println(ex.getMessage());
            System.err.println("calls to change date or time will be ignored");
        }
    }

    // /////////////////////////////////////////////////
    // FixtureType
    // /////////////////////////////////////////////////

    /**
     * As specified in constructor.
     */
    @Override
    public final FixtureType getType() {
        return fixtureType;
    }

    // /////////////////////////////////////////////////
    // FixtureClock
    // /////////////////////////////////////////////////

    /**
     * Will print warning message and do nothing if {@link FixtureClock} could
     * not be {@link FixtureClock#initialize() initialized}.
     */
    public void setDate(final int year, final int month, final int day) {
        if (shouldIgnoreCallBecauseNoClockSetup("setDate()")) {
            return;
        }
        clock.setDate(year, month, day);
    }

    /**
     * Will print warning message and do nothing if {@link FixtureClock} could
     * not be {@link FixtureClock#initialize() initialized}.
     */
    public void setTime(final int hour, final int minute) {
        if (shouldIgnoreCallBecauseNoClockSetup("setTime()")) {
            return;
        }
        clock.setTime(hour, minute);
    }

    /**
     * The {@link Clock} singleton, downcast to {@link FixtureClock}.
     * 
     * <p>
     * Will return <tt>null</tt> if {@link FixtureClock} could not be
     * {@link FixtureClock#initialize() initialized}.
     */
    public FixtureClock getFixtureClock() {
        return clock;
    }

    /**
     * Will print warning message and do nothing if {@link FixtureClock} could
     * not be {@link FixtureClock#initialize() initialized}.
     */
    public void resetClock() {
        if (shouldIgnoreCallBecauseNoClockSetup("resetClock()")) {
            return;
        }
        clock.reset();
    }

    boolean shouldIgnoreCallBecauseNoClockSetup(final String methodName) {
        if (clock == null) {
            System.err.println("clock not set, call to " + methodName + " ignored");
            return true;
        }
        return false;
    }
    // }}

}