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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.SerialNumberVersion;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.Version;

public class DefaultVersionMapping implements VersionMapping {

    @Override
    public String mapVersion(final Version version) {
        // SerialNumberVersion v = (SerialNumberVersion) version;
        // return Long.toHexString(v.getSequence());
        return version.sequence();
    }

    @Override
    public Version getVersion(final String id) {
        final Long sequence = Long.valueOf(id, 16);
        return SerialNumberVersion.create(sequence, null, null);
    }

}
