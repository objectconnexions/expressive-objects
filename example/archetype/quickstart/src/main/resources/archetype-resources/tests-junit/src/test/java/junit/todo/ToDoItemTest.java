#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package junit.todo;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import junit.AbstractTest;
import dom.todo.ToDoItem;
import fixture.LogonAsSvenFixture;
import fixture.todo.ToDoItemsFixture;

import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.applib.DisabledException;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.Fixture;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.Fixtures;

@Fixtures({ @Fixture(ToDoItemsFixture.class), @Fixture(LogonAsSvenFixture.class) })
public class ToDoItemTest extends AbstractTest {

    private ToDoItem toDoItem;

    @Override
    @Before
    public void setUp() {
        toDoItem = toDoItems.notYetDone().get(0);
        toDoItem = wrapped(toDoItem);
    }

    @Test
    public void canMarkAsDone() throws Exception {
        toDoItem.markAsDone();
        assertThat(toDoItem.getDone(), is(true));
    }

    @Test
    public void cannotMarkAsDoneTwice() throws Exception {
        toDoItem.markAsDone();
        try {
            toDoItem.markAsDone();
            fail("Should have been disabled");
        } catch (final DisabledException e) {
            assertThat(e.getMessage(), is("Already done"));
        }
    }

}
