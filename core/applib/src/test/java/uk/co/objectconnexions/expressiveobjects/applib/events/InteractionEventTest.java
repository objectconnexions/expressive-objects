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

package uk.co.objectconnexions.expressiveobjects.applib.events;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.objectconnexions.expressiveobjects.applib.Identifier;

@RunWith(JMock.class)
public class InteractionEventTest {

    @SuppressWarnings("unused")
    private final Mockery mockery = new JUnit4Mockery();

    private InteractionEvent interactionEvent;

    private Object source;
    private Identifier identifier;

    private Class<? extends InteractionEventTest> advisorClass;

    @Before
    public void setUp() {
        source = new Object();
        identifier = Identifier.actionIdentifier("CustomerOrder", "cancelOrder", new Class[] { String.class, boolean.class });
        advisorClass = this.getClass();
    }

    @Test
    public void getIdentifier() {
        interactionEvent = new InteractionEvent(source, identifier) {

            private static final long serialVersionUID = 1L;
        };
        assertThat(interactionEvent.getIdentifier(), is(identifier));
    }

    @Test
    public void getSource() {
        interactionEvent = new InteractionEvent(source, identifier) {

            private static final long serialVersionUID = 1L;
        };
        assertThat(interactionEvent.getSource(), is(source));
    }

    @Test
    public void getClassName() {
        interactionEvent = new InteractionEvent(source, identifier) {

            private static final long serialVersionUID = 1L;
        };
        assertThat(interactionEvent.getClassName(), equalTo("CustomerOrder"));
    }

    @Test
    public void getClassNaturalName() {
        interactionEvent = new InteractionEvent(source, identifier) {

            private static final long serialVersionUID = 1L;
        };
        assertThat(interactionEvent.getClassNaturalName(), equalTo("Customer Order"));
    }

    @Test
    public void getMember() {
        interactionEvent = new InteractionEvent(source, identifier) {

            private static final long serialVersionUID = 1L;
        };
        assertThat(interactionEvent.getMemberName(), equalTo("cancelOrder"));
    }

    @Test
    public void getMemberNaturalName() {
        interactionEvent = new InteractionEvent(source, identifier) {

            private static final long serialVersionUID = 1L;
        };
        assertThat(interactionEvent.getMemberNaturalName(), equalTo("Cancel Order"));
    }

    @Test
    public void shouldInitiallyNotVeto() {
        interactionEvent = new InteractionEvent(source, identifier) {

            private static final long serialVersionUID = 1L;
        };
        assertThat(interactionEvent.isVeto(), is(false));
    }

    @Test
    public void afterAdvisedShouldVeto() {
        interactionEvent = new InteractionEvent(source, identifier) {

            private static final long serialVersionUID = 1L;
        };
        interactionEvent.advised("some reason", this.getClass());
        assertThat(interactionEvent.isVeto(), is(true));
    }

    @Test
    public void afterAdvisedShouldReturnReason() {
        interactionEvent = new InteractionEvent(source, identifier) {

            private static final long serialVersionUID = 1L;
        };
        interactionEvent.advised("some reason", this.getClass());
        assertThat(interactionEvent.isVeto(), is(true));
    }

    @Test
    public void afterAdvisedShouldReturnAdvisorClass() {
        interactionEvent = new InteractionEvent(source, identifier) {

            private static final long serialVersionUID = 1L;
        };
        interactionEvent.advised("some reason", advisorClass);
        assertEquals(interactionEvent.getAdvisorClass(), advisorClass);
    }

}
