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

package uk.co.objectconnexions.expressiveobjects.profilestore.xml.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import uk.co.objectconnexions.expressiveobjects.core.commons.encoding.DataOutputStreamExtended;
import uk.co.objectconnexions.expressiveobjects.core.commons.xml.ContentWriter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.ServiceUtil;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.Options;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.PerspectiveEntry;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfile;

public class UserProfileContentWriter implements ContentWriter {
    private final UserProfile userProfile;

    public UserProfileContentWriter(final UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public void write(final Writer writer) throws IOException {
        final StringBuffer xml = new StringBuffer();
        xml.append("<profile>\n");

        final Options options = userProfile.getOptions();
        writeOptions(xml, options, null, 0);

        xml.append("  <perspectives>\n");
        for (final String perspectiveName : userProfile.list()) {
            final PerspectiveEntry perspective = userProfile.getPerspective(perspectiveName);

            xml.append("    <perspective" + attribute("name", perspectiveName) + ">\n");
            xml.append("      <services>\n");
            for (final Object service : perspective.getServices()) {
                xml.append("        <service " + attribute("id", ServiceUtil.id(service)) + "/>\n");
            }
            xml.append("      </services>\n");
            xml.append("      <objects>\n");
            for (final Object object : perspective.getObjects()) {
                final ObjectAdapter adapter = getPersistenceSession().getAdapterManager().adapterFor(object);
                xml.append("        <object>" + adapter.getOid().enString(getOidMarshaller()) + "</object>\n");
            }
            xml.append("      </objects>\n");
            xml.append("    </perspective>\n");
        }
        xml.append("  </perspectives>\n");

        xml.append("</profile>\n");

        writer.write(xml.toString());
    }

    private void writeOptions(final StringBuffer xml, final Options options, final String name1, final int level) {
        final String spaces = StringUtils.repeat("  ", level);

        final Iterator<String> names = options.names();
        if (level == 0 || names.hasNext()) {
            xml.append(spaces + "  <options");
            if (name1 != null) {
                xml.append(" id=\"" + name1 + "\"");
            }
            xml.append(">\n");
            while (names.hasNext()) {
                final String name = names.next();
                if (options.isOptions(name)) {
                    writeOptions(xml, options.getOptions(name), name, level + 1);
                } else {
                    xml.append(spaces + "    <option" + attribute("id", name) + ">" + options.getString(name) + "</option>\n");
                }
            }
            xml.append(spaces + "  </options>\n");
        }
    }

    private String attribute(final String name, final String value) {
        return " " + name + "=\"" + value + "\"";
    }

    // ///////////////////////////////////////////////////
    // Dependencies (from context)
    // ///////////////////////////////////////////////////

    protected OidMarshaller getOidMarshaller() {
		return ExpressiveObjectsContext.getOidMarshaller();
	}

    protected static PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

}
