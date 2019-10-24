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

package uk.co.objectconnexions.expressiveobjects.core.webserver.internal;

import static uk.co.objectconnexions.expressiveobjects.core.runtime.runner.Constants.TYPE_SERVER;
import static uk.co.objectconnexions.expressiveobjects.core.runtime.runner.Constants.TYPE_SERVER_EXPLORATION;
import static uk.co.objectconnexions.expressiveobjects.core.runtime.runner.Constants.TYPE_SERVER_PROTOTYPE;

import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerDeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;

public class OptionHandlerDeploymentTypeWebServer extends OptionHandlerDeploymentType {

    private static final String TYPES = TYPE_SERVER_EXPLORATION + "; " + TYPE_SERVER_PROTOTYPE + " (default); " + TYPE_SERVER;

    public OptionHandlerDeploymentTypeWebServer() {
        super(DeploymentType.SERVER_PROTOTYPE, TYPES);
    }
}
