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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncodingException;
import uk.co.objectconnexions.expressiveobjects.applib.value.Time;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolderImpl;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.time.TimeValueSemanticsProvider;

public class TimeValueSemanticsProviderTest extends ValueSemanticsProviderAbstractTestCase {

    private TimeValueSemanticsProvider adapter;
    private Time time;
    private FacetHolder holder;

    @Before
    public void setUpObjects() throws Exception {
        context.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString("expressive-objects.value.format.time");
                will(returnValue(null));
            }
        });

        TestClock.initialize();
        time = new Time(8, 13);
        holder = new FacetHolderImpl();
        setValue(adapter = new TimeValueSemanticsProvider(holder, mockConfiguration, mockContext));
    }

    @Test
    public void testTimeAsEncodedString() throws Exception {
        assertEquals("081300000", adapter.toEncodedString(time));
    }

    @Test
    public void testParseEntryOfHoursMinutesText() throws Exception {
        final Object parsed = adapter.parseTextEntry(null, "8:30", null);
        assertEquals(new Time(8, 30), parsed);
    }

    @Test
    @Ignore
    public void testParseEntryOfHoursMinutesSecondsText() throws Exception {
        final Object parsed = adapter.parseTextEntry(null, "8:30:45", null); 
        // I can't get the text parser to parse HH:mm:ss before HH:mm!!
        final Time expected = new Time(8, 30, 45);
        assertEquals(expected, parsed);
    }

    @Test
    public void testParseEntryOfHoursAfterTime() throws Exception {
        final Object parsed = adapter.parseTextEntry(time, "+5H", null);
        assertEquals(new Time(13, 13), parsed);
    }

    @Test
    public void testParseEntryOfHoursAfterNow() throws Exception {
        final Object parsed = adapter.parseTextEntry(null, "+5H", null);
        assertEquals(new Time(2, 30, 25), parsed);
    }

    @Test
    public void testParseEntryOfHoursBeforeTime() throws Exception {
        final Object parsed = adapter.parseTextEntry(time, "-7H", null);
        assertEquals(new Time(1, 13), parsed);
    }

    @Test
    public void testParseEntryOfHoursBeforeToNow() throws Exception {
        final Object parsed = adapter.parseTextEntry(null, "-5H", null);
        assertEquals(new Time(16, 30, 25), parsed);
    }

    @Test
    public void testParseEntryOfKeywordNow() throws Exception {
        final Object parsed = adapter.parseTextEntry(time, "now", null);
        assertEquals(new Time(), parsed);
    }

    @Test
    public void testRestoreTime() throws Exception {
        final Time expected = new Time(21, 30);
        final Object parsed = adapter.fromEncodedString("213000000");
        assertEquals(expected, parsed);
    }

    @Test
    public void testRestoreOfInvalidDatal() throws Exception {
        try {
            adapter.fromEncodedString("two ten");
            fail();
        } catch (final EncodingException expected) {
        }
    }

}
