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

package uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;

import com.google.common.base.Optional;

public final class OptionValidatorForViewers implements OptionValidator {
    private final OptionHandlerViewer optionHandlerViewer;

    public OptionValidatorForViewers(final OptionHandlerViewer optionHandlerViewer) {
        this.optionHandlerViewer = optionHandlerViewer;
    }

    @Override
    public Optional<String> validate(final DeploymentType deploymentType) {
        final List<String> viewerNames = optionHandlerViewer.getViewerNames();

        final boolean fail = !deploymentType.canSpecifyViewers(viewerNames);
        final String failMsg = String.format("Error: cannot specify %s viewer%s for deployment type %s\n", Strings.plural(viewerNames, "more than one", "any"), Strings.plural(viewerNames, "", "s"), deploymentType.nameLowerCase());
        return setIf(fail, failMsg);
    }
    
    private static Optional<String> setIf(final boolean fail, final String failMsg) {
        return fail? Optional.of(failMsg): Optional.<String>absent();
    }

}