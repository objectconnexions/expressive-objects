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
package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ToString;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ScimpiException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.util.MethodsUtils;

public class History extends AbstractElementProcessor {

    private static final String _HISTORY = "_history";

    public static class Event {
        String type;
        String name;
        String link;
        String id;
    }

    public static class Events implements Serializable {
        private static final long serialVersionUID = 1L;
        private static final int MAXIMUM_SIZE = 10;
        private final List<Event> events = new ArrayList<Event>();

        public void add(final String name, final String link, String id, String type) {
            for (final Event event : events) {
                if (event.link.equals(link) && event.id.equals(id)) {
                    events.remove(event);
                    events.add(event);
                    return;
                }
            }

            final Event event = new Event();
            event.name = name;
            event.link = link;
            event.id = id;
            event.type = type;
            events.add(event);

            if (events.size() > MAXIMUM_SIZE) {
                events.remove(0);
            }
        }

        public void clear() {
            events.clear();
        }

        public boolean isEmpty() {
            return events.size() == 0;
        }

        public int size() {
            return events.size();
        }

        public Iterable<Event> iterator() {
            return events;
        }

        @Override
        public String toString() {
            ToString toString = new ToString(this);
            for (Event event : events) {
                toString.append(event.toString());
                toString.append(",");
            }
            return "{" + toString + "}";
        }
    }

    @Override
    public String getName() {
        return "history";
    }

    @Override
    public void process(final Request request) {
        final String action = request.getOptionalProperty("action", "display");
        final Events events = getEvents(request);
        if (action.equals("display") && events != null) {
            write(events, request);
        } else if (action.equals("link")) {
            final String name = request.getRequiredProperty(NAME);
            final String link = request.getRequiredProperty(LINK_VIEW);
            events.add(name, link, "",  null);
        } else if (action.equals("add")) {
            final String id = request.getOptionalProperty(OBJECT);
            final ObjectAdapter object = MethodsUtils.findObject(request.getContext(), id);
            final String name = object.titleString();
            String link = request.getRequiredProperty(LINK_VIEW);
            events.add(name, link, id, object.getSpecification().getShortIdentifier());
        } else if (action.equals("return")) {

        } else if (action.equals("clear")) {
            events.clear();
        } else {
            throw new ScimpiException("No such action: " + action);
        }

    }

    public void write(final Events events, final Request request) {
        if (events.isEmpty()) {
            return;
        }

        request.appendHtml("<div id=\"history\">");
        int i = 0;
        final int length = events.size();
        for (final Event event : events.iterator()) {
            final String link = event.link;
            if (i > 0) {
                request.appendHtml("<span class=\"separator\"> | </span>");
            }
            if (i == length - 1 || link == null) {
                request.appendHtml("<span class=\"disabled\">");
                request.appendHtml(event.name);
                request.appendHtml("</span>");
            } else {
                request.appendHtml("<a class=\"linked\" href=\"" + link + "?" + RequestContext.RESULT + "=" + event.id + "\">");
                request.appendHtml(event.name);
                request.appendHtml("</a>");
            }
            i++;
        }
        request.appendHtml("</div>");
    }

    private static Events getEvents(final Request request) {
        final RequestContext context = request.getContext();
        Events events = (Events) context.getVariable(_HISTORY);
        if (events == null) {
            events = new Events();
            context.addVariable(_HISTORY, events, Scope.SESSION);
        }
        return events;
    }

    
    public static ReferencedObjects getReferencedObjects(final Request request) {
        ReferencedObjects referenced = new ReferencedObjects();
        Events events = getEvents(request);
        for (Event event : events.events) {
            referenced.add(event);
        }
        return referenced;
    }
}
