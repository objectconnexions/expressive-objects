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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import junit.AbstractTest;
import dom.todo.ToDoItem;
import dom.todo.ToDoItem.Category;
import fixture.LogonAsSvenFixture;
import fixture.todo.ToDoItemsFixture;

import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.viewer.junit.Fixture;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.Fixtures;

@Fixtures({ @Fixture(ToDoItemsFixture.class), @Fixture(LogonAsSvenFixture.class) })
public class ToDoItemRepositoryTest extends AbstractTest {

    @Test
    public void canFindAllItemsNotYetDone() throws Exception {
        final List<ToDoItem> foobarList = toDoItems.notYetDone();
        assertThat(foobarList.size(), is(5));
    }

    @Test
    public void canCreateToDoItem() throws Exception {
        final ToDoItem newItem = toDoItems.newToDo("item description", Category.Professional);
        assertThat(newItem, is(not(nullValue())));
        assertThat(newItem.getDescription(), is("item description"));
        assertThat(getDomainObjectContainer().isPersistent(newItem), is(true));
    }

}
