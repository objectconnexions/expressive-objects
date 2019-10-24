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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class LogLevel extends AbstractElementProcessor {

    @Override
    public void process(final Request request) {

        String view = request.getOptionalProperty(VIEW, request.getViewPath());
        view = request.getContext().fullFilePath(view);
        final Level level = LogManager.getRootLogger().getLevel();
        final boolean showSelector = request.isRequested(SHOW_SELECT, true);
        if (showSelector) {
            request.appendHtml("<form action=\"log.app\" type=\"post\" >");
            request.appendHtml("<input type=\"hidden\" name=\"view\" value=\"" + view + "\" />");
            request.appendHtml("<select name=\"level\">");
            for (final Level l : new Level[] { Level.OFF, Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE }) {
                final String settings = level + "\"" + (level == l ? " selected=\"selected\" " : "");
                request.appendHtml("<option " + settings + ">" + l + "</option>");
            }
            request.appendHtml("<input type=\"submit\" value=\"Change Level\" />");
            request.appendHtml("</select>");
            request.appendHtml("</form>");
        } else {
            request.appendHtml(level.toString());
        }
    }

    @Override
    public String getName() {
        return "log-level";
    }

}
