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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugHtmlString;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugString;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugTee;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.debug.DebugHtmlWriter;
import org.apache.log4j.Logger;

public class ErrorCollator {
    private static final Logger LOG = Logger.getLogger(ErrorCollator.class);

    private String errorRef;
    private String message;
    private final DebugString debugText = new DebugString();
    private final DebugHtmlString debugHtml = new DebugHtmlString();
    private final DebugBuilder debug = new DebugTee(debugText, debugHtml);
 
    public void missingFile(String message) {
        this.message = message;
    }

    public void message(final Throwable exception) {
        LOG.debug(exception.getMessage(), exception);
        message = exception.getMessage();
        debug.appendPreformatted(message);
    }

    public void exception(final Throwable exception) {
        String messageText = exception.getMessage(); 
        LOG.debug(messageText, exception); 
        try {
            debug.startSection("Exception");
            debug.appendException(exception);
            debug.endSection();
        } catch (final RuntimeException e) {
            debug.appendln("NOTE - an exception occurred while dumping an exception!");
            debug.appendException(e);
        }
        message = messageText == null ? exception.getClass().getName() : messageText; 
    }
        
    public DebugBuilder getDebug() {
        return debug;
    }
    
    public void compileError(final RequestContext requestContext) {
        errorRef = Long.toString(System.currentTimeMillis(), 36).toUpperCase();
        LOG.info("error " + errorRef);

        captureWarningsAndMessages();
        dumpDebugDetails(requestContext);
        writeErrorFile();
    }

    private void captureWarningsAndMessages() {
        // Capture warnings/messages
        if (ExpressiveObjectsContext.getCurrentTransaction() != null) {
            final List<String> messages = ExpressiveObjectsContext.getMessageBroker().getMessages();
            final List<String> warnings = ExpressiveObjectsContext.getMessageBroker().getWarnings();
            if (messages.size() > 0 || messages.size() > 0) {
                debug.startSection("Warnings/Messages");
                for (final String message : messages) {
                    debug.appendln("message", message);
                }
                for (final String message : warnings) {
                    debug.appendln("warning", message);
                }
            }
        }
    }

    private void dumpDebugDetails(final RequestContext requestContext) {
        // Dump page debug details 
        requestContext.append(debug);

        debug.startSection("Processing Trace");
        debug.appendPreformatted(requestContext.getDebugTrace());
        debug.endSection();
        debug.close();
    }

    private void writeErrorFile() {
        LOG.error(message + "\n" + debugText.toString());
        
        
        PrintWriter writer;
        try {
            final String directory =
                ExpressiveObjectsContext.getConfiguration().getString(ConfigurationConstants.ROOT + "scimpi.error-snapshots", ".");
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            writer = new PrintWriter(new File(dir, "error_" + errorRef + ".html"));
            final DebugHtmlWriter writer2 = new DebugHtmlWriter(writer, true);
            writer2.concat(debugHtml);
            writer2.close();
            writer.close();
        } catch (final FileNotFoundException e) {
            LOG.error("Failed to archive error page", e);
        }
    }

    public String getReference() {
        return errorRef;
    }
    
    public String getDetails() {
        return debugHtml.toString();
    }

    public String getMessage() {
        return message;
    }

    
}

