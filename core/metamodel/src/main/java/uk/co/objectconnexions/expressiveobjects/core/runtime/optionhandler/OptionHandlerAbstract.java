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

package uk.co.objectconnexions.expressiveobjects.core.runtime.optionhandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;

import uk.co.objectconnexions.expressiveobjects.core.commons.components.Installer;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ListUtils;

public abstract class OptionHandlerAbstract implements OptionHandler {

    public OptionHandlerAbstract() {
    }

    protected StringBuffer availableInstallers(final Object[] factories) {
        final StringBuffer types = new StringBuffer();
        for (int i = 0; i < factories.length; i++) {
            if (i > 0) {
                types.append("; ");
            }
            types.append(((Installer) factories[i]).getName());
        }
        return types;
    }

    protected List<String> getOptionValues(final CommandLine commandLine, final String opt) {
        final List<String> list = new ArrayList<String>();
        final String[] optionValues = commandLine.getOptionValues(opt);
        if (optionValues != null) {
            for (final String optionValue : optionValues) {
                ListUtils.appendDelimitedStringToList(optionValue, list);
            }
        }
        return list;
    }

}
