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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request.RepeatMarker;

public class BlockUse extends AbstractElementProcessor {

    @Override
    public String getName() {
        return "use-block";
    }

    @Override
    public void process(final Request request) {
        final String name = request.getOptionalProperty(NAME, "unamed");
        request.closeEmpty();
        final RepeatMarker end = request.createMarker();

        final RepeatMarker marker = (RepeatMarker) request.getContext().getVariable("_block-" + name);
        marker.repeat();

        request.processUtilCloseTag();
        end.repeat();
    }

}
