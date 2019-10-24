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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facetdecorators.help.file.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.applib.Identifier;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facetdecorators.help.HelpManagerAbstract;

public class HelpManagerUsingFiles extends HelpManagerAbstract {

    private static final Logger LOG = Logger.getLogger(HelpManagerUsingFiles.class);

    /**
     * The name of the file used unless overridden with
     * {@link #setFileName(String)}.
     */
    public static final String DEFAULT_FILE_NAME = "help.txt";
    private static final String CLASS_PREFIX = "c:";
    private static final String NAME_PREFIX = "m:";

    private String fileName = DEFAULT_FILE_NAME;

    @SuppressWarnings("unused")
    private final ExpressiveObjectsConfiguration configuration;

    public HelpManagerUsingFiles(final ExpressiveObjectsConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getHelpText(final Identifier identifier) {
        BufferedReader reader = null;
        try {
            reader = getReader();

            if (reader == null) {
                return "No help available (no file found)";
            }

            final String className = CLASS_PREFIX + identifier.getClassName().toLowerCase();
            final String name = NAME_PREFIX + identifier.getMemberName().toLowerCase();

            final StringBuffer str = new StringBuffer();
            String line;

            boolean lookingForClass = true;
            boolean lookingForName = identifier.getMemberName().length() > 0;
            /*
             * Read through each line in file.
             */
            while ((line = reader.readLine()) != null) {
                // Skip comments - lines begining with hash
                if (line.length() > 0 && line.charAt(0) == '#') {
                    continue;
                }

                /*
                 * Look for class.
                 */
                if (line.toLowerCase().equals(className)) {
                    lookingForClass = false;
                    continue;
                }

                if (lookingForClass) {
                    continue;
                } else if (line.toLowerCase().startsWith(CLASS_PREFIX)) {
                    break;
                }

                /*
                 * Look for field/method.
                 */
                if (line.toLowerCase().equals(name)) {
                    lookingForName = false;
                    continue;
                }

                if (lookingForName) {
                    continue;
                } else if (line.toLowerCase().startsWith(NAME_PREFIX)) {
                    break;
                }

                str.append(line);
                str.append('\n');
            }

            return str.toString();

        } catch (final FileNotFoundException e) {
            LOG.error("opening help file", e);
            return "Failure opening help file: " + e.getMessage();
        } catch (final IOException e) {
            LOG.error("reading help file", e);
            return "Failure reading help file: " + e.getMessage();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException ignore) {
                }
            }
        }
    }

    protected BufferedReader getReader() throws FileNotFoundException {
        final File file = new File(fileName);
        if (!file.exists()) {
            final String message = "No help file found: " + file.getAbsolutePath();
            LOG.warn(message);
            return null;
        }

        return new BufferedReader(new FileReader(file));
    }
}
