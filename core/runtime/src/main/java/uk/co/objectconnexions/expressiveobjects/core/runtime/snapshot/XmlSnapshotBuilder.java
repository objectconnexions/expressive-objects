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
package uk.co.objectconnexions.expressiveobjects.core.runtime.snapshot;

import java.util.List;

import com.google.common.collect.Lists;

import uk.co.objectconnexions.expressiveobjects.applib.snapshot.Snapshottable;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.snapshot.XmlSchema;
import uk.co.objectconnexions.expressiveobjects.core.runtime.snapshot.XmlSnapshot;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;

/**
 * Builds an {@link XmlSnapshot} using a fluent use through a builder:
 * 
 * <pre>
 * XmlSnapshot snapshot = XmlSnapshotBuilder.create(customer).includePath(&quot;placeOfBirth&quot;).includePath(&quot;orders/product&quot;).build();
 * Element customerAsXml = snapshot.toXml();
 * </pre>
 */
public class XmlSnapshotBuilder {

    private final Snapshottable snapshottable;
    private XmlSchema schema;
    private OidMarshaller oidMarshaller = new OidMarshaller();

    static class PathAndAnnotation {
        public PathAndAnnotation(final String path, final String annotation) {
            this.path = path;
            this.annotation = annotation;
        }

        private final String path;
        private final String annotation;
    }

    private final List<XmlSnapshotBuilder.PathAndAnnotation> paths = Lists.newArrayList();

    public XmlSnapshotBuilder(final Snapshottable domainObject) {
        this.snapshottable = domainObject;
    }

    public XmlSnapshotBuilder usingSchema(final XmlSchema schema) {
        this.schema = schema;
        return this;
    }

    public XmlSnapshotBuilder usingOidMarshaller(final OidMarshaller oidMarshaller) {
        this.oidMarshaller = oidMarshaller;
        return this;
    }

    public XmlSnapshotBuilder includePath(final String path) {
        return includePathAndAnnotation(path, null);
    }

    public XmlSnapshotBuilder includePathAndAnnotation(final String path, final String annotation) {
        paths.add(new PathAndAnnotation(path, annotation));
        return this;
    }

    public XmlSnapshot build() {
        final ObjectAdapter adapter = getAdapterManager().adapterFor(snapshottable);
        final XmlSnapshot snapshot = (schema != null) ? new XmlSnapshot(adapter, schema, oidMarshaller) : new XmlSnapshot(adapter, oidMarshaller);
        for (final XmlSnapshotBuilder.PathAndAnnotation paa : paths) {
            if (paa.annotation != null) {
                snapshot.include(paa.path, paa.annotation);
            } else {
                snapshot.include(paa.path);
            }
        }
        return snapshot;
    }

    // ///////////////////////////////////////////////////////
    // Dependencies (from context)
    // ///////////////////////////////////////////////////////

    private static AdapterManager getAdapterManager() {
        return getPersistenceSession().getAdapterManager();
    }

    private static PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }
}