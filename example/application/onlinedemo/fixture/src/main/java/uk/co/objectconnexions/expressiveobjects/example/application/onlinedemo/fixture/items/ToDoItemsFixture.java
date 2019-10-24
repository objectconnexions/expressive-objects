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

package uk.co.objectconnexions.expressiveobjects.example.application.onlinedemo.fixture.items;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.clock.Clock;
import uk.co.objectconnexions.expressiveobjects.applib.fixtures.AbstractFixture;
import uk.co.objectconnexions.expressiveobjects.applib.value.Date;
import uk.co.objectconnexions.expressiveobjects.example.application.onlinedemo.dom.items.Categories;
import uk.co.objectconnexions.expressiveobjects.example.application.onlinedemo.dom.items.Category;
import uk.co.objectconnexions.expressiveobjects.example.application.onlinedemo.dom.items.ToDoItem;
import uk.co.objectconnexions.expressiveobjects.example.application.onlinedemo.dom.items.ToDoItems;

public class ToDoItemsFixture extends AbstractFixture {

    @Override
    public void install() {
        final Category domesticCategory = findOrCreateCategory("Domestic");
        final Category professionalCategory = findOrCreateCategory("Professional");

        removeAllToDosForCurrentUser();

        createToDoItemForCurrentUser("Buy milk", domesticCategory, daysFromToday(0));
        createToDoItemForCurrentUser("Buy stamps", domesticCategory, daysFromToday(0));
        createToDoItemForCurrentUser("Pick up laundry", domesticCategory, daysFromToday(6));
        createToDoItemForCurrentUser("Write blog post", professionalCategory, null);
        createToDoItemForCurrentUser("Organize brown bag", professionalCategory, daysFromToday(14));

        getContainer().flush();
    }

    // {{ helpers
    private Category findOrCreateCategory(final String description) {
        final Category category = categories.find(description);
        if (category != null) {
            return category;
        }
        return categories.newCategory(description);
    }

    private void removeAllToDosForCurrentUser() {
        final List<ToDoItem> allToDos = toDoItems.allToDos();
        for (final ToDoItem toDoItem : allToDos) {
            getContainer().remove(toDoItem);
        }
    }

    private ToDoItem createToDoItemForCurrentUser(final String description, final Category category, final Date dueBy) {
        return toDoItems.newToDo(description, category, dueBy);
    }

    private static Date daysFromToday(final int i) {
        final Date date = new Date(Clock.getTimeAsDateTime());
        date.add(0, 0, i);
        return date;
    }

    // }}

    // {{ injected: Categories
    private Categories categories;

    public void setCategories(final Categories categories) {
        this.categories = categories;
    }

    // }}

    // {{ injected: ToDoItems
    private ToDoItems toDoItems;

    public void setToDoItems(final ToDoItems toDoItems) {
        this.toDoItems = toDoItems;
    }
    // }}

}
