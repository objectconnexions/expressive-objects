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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.MessageBroker;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class Warnings extends AbstractElementProcessor {

    @Override
    public void process(final Request request) {
        final String cls = request.getOptionalProperty(CLASS);
        final StringBuffer buffer = new StringBuffer();
        write(cls, buffer);
        if (buffer.length() > 0) {
            request.appendHtml("<div class=\"feedback\">");
            request.appendHtml(buffer.toString());
            request.appendHtml("</div>");
        }
    }

    public static void write(String cls, final StringBuffer buffer) {
        if (cls == null) {
            cls = "warning";
        }
        final MessageBroker messageBroker = ExpressiveObjectsContext.getMessageBroker();
        final List<String> warnings = messageBroker.getWarnings();
        for (final String warning : warnings) {
            buffer.append("<div class=\"" + cls + "\">" + warning + "</div>");
        }
    }

    @Override
    public String getName() {
        return "warnings";
    }

}
