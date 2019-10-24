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

package fixture.todo;

import dom.todo.ToDoItems;

import uk.co.objectconnexions.expressiveobjects.applib.AbstractService;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Named;

/**
 * Enables fixtures to be installed from the application.
 */
@Named("Fixtures")
public class ToDoItemsFixturesService extends AbstractService {

    public void install() {
        final ToDoItemsFixture fixture = new ToDoItemsFixture();
        fixture.setContainer(getContainer());
        fixture.setToDoItems(toDoItems);
        fixture.install();
    }

    private ToDoItems toDoItems;

    public void setToDoItems(final ToDoItems toDoItems) {
        this.toDoItems = toDoItems;
    }

}
