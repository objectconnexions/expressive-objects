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

package uk.co.objectconnexions.expressiveobjects.core.runtime.memento;

import java.io.IOException;
import java.io.Serializable;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.encoding.DataInputExtended;
import uk.co.objectconnexions.expressiveobjects.core.commons.encoding.DataOutputExtended;
import uk.co.objectconnexions.expressiveobjects.core.commons.encoding.Encodable;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ResolveState;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.TypedOid;

public class Data implements Encodable, Serializable {

    private final static long serialVersionUID = 1L;

    private final String className;
    private final Oid oid;

    public Data(final Oid oid, final String className) {
        this.className = className;
        this.oid = oid;
        initialized();
    }

    public Data(final DataInputExtended input) throws IOException {
        this.className = input.readUTF();
        this.oid = input.readEncodable(Oid.class);
        initialized();
    }

    @Override
    public void encode(final DataOutputExtended output) throws IOException {
        output.writeUTF(className);
        output.writeEncodable(oid);
    }

    private void initialized() {
        // nothing to do
    }

    // ///////////////////////////////////////////////////////
    //
    // ///////////////////////////////////////////////////////

    /**
     * Note: could be <tt>null</tt> if represents a
     * {@link ResolveState#isValue() standalone} adapter.
     */
    public Oid getOid() {
        return oid;
    }

    /**
     * REVIEW: this probably isn't needed anymore given that {@link #getOid() oid} (at least for {@link TypedOid})
     * includes the object type.
     */
    public String getClassName() {
        return className;
    }

    public void debug(final DebugBuilder debug) {
        debug.appendln(className);
        debug.appendln(oid != null ? oid.toString() : "null");
    }

    @Override
    public String toString() {
        return className + "/" + oid;
    }

}
