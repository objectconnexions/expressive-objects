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
package uk.co.objectconnexions.expressiveobjects.core.metamodel.services.bookmarks;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Hidden;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.NotInServiceMenu;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Programmatic;
import uk.co.objectconnexions.expressiveobjects.applib.bookmarks.Bookmark;
import uk.co.objectconnexions.expressiveobjects.applib.bookmarks.BookmarkHolder;
import uk.co.objectconnexions.expressiveobjects.applib.bookmarks.BookmarkService;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.DomainObjectServices;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.DomainObjectServicesAware;

public class BookmarkServiceDefault implements BookmarkService, DomainObjectServicesAware {

    private DomainObjectServices domainObjectServices;
    
    @Override
    @NotInServiceMenu
    public Object lookup(BookmarkHolder bookmarkHolder) {
        Bookmark bookmark = bookmarkHolder.bookmark();
        Object lookup = domainObjectServices.lookup(bookmark);
        return lookup;
    }

    @Override
    @Programmatic
    public void setDomainObjectServices(DomainObjectServices domainObjectServices) {
        this.domainObjectServices = domainObjectServices;
    }

    @Override
    @Hidden
    public Bookmark bookmarkFor(Object domainObject) {
        return domainObjectServices.bookmarkFor(domainObject);
    }

}
