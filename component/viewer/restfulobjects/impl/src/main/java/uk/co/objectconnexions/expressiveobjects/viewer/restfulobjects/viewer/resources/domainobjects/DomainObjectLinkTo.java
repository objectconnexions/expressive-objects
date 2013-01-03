/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.resources.domainobjects;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectMember;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.links.Rel;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.ResourceContext;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.representations.LinkBuilder;

public class DomainObjectLinkTo implements ObjectAdapterLinkTo {

    protected ResourceContext resourceContext;
    protected ObjectAdapter objectAdapter;

    @Override
    public final DomainObjectLinkTo usingResourceContext(final ResourceContext resourceContext) {
        this.resourceContext = resourceContext;
        return this;
    }

    @Override
    public ObjectAdapterLinkTo with(final ObjectAdapter objectAdapter) {
        this.objectAdapter = objectAdapter;
        return this;
    }

    @Override
    public LinkBuilder builder() {
        return builder(null);
    }

    @Override
    public LinkBuilder builder(final Rel rel) {
        final LinkBuilder linkBuilder = LinkBuilder.newBuilder(resourceContext, relElseDefault(rel), RepresentationType.DOMAIN_OBJECT, linkRef());
        linkBuilder.withTitle(objectAdapter.titleString());
        return linkBuilder;
    }

    /**
     * hook method
     */
    protected String linkRef() {
        if (resourceContext == null) {
            throw new IllegalStateException("resourceContext not provided");
        }
        if (objectAdapter == null) {
            throw new IllegalStateException("objectAdapter not provided");
        }
        final StringBuilder buf = new StringBuilder("objects/");
        buf.append(objectAdapter.getOid().enString(getOidMarshaller()));
        return buf.toString();
    }

    private Rel relElseDefault(final Rel rel) {
        return rel != null ? rel : defaultRel();
    }

    /**
     * hook method; used by {@link #builder(Rel)}.
     */
    protected Rel defaultRel() {
        return Rel.OBJECT;
    }

    @Override
    public final LinkBuilder memberBuilder(final Rel rel, final MemberType memberType, final ObjectMember objectMember, final String... parts) {
        return memberBuilder(rel, memberType, objectMember, memberType.getRepresentationType(), parts);
    }

    @Override
    public final LinkBuilder memberBuilder(final Rel rel, final MemberType memberType, final ObjectMember objectMember, final RepresentationType representationType, final String... parts) {
        final StringBuilder buf = new StringBuilder(linkRef());
        buf.append("/").append(memberType.getUrlPart()).append(objectMember.getId());
        for (final String part : parts) {
            if (part == null) {
                continue;
            }
            buf.append("/").append(part);
        }
        final String url = buf.toString();
        return LinkBuilder.newBuilder(resourceContext, rel, representationType, url);
    }


    
    //////////////////////////////////////////////////
    // Dependencies (from context)
    //////////////////////////////////////////////////
    
    protected static OidMarshaller getOidMarshaller() {
        return ExpressiveObjectsContext.getOidMarshaller();
    }


}