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

package uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.services.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.google.common.collect.Lists;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Assert;
import uk.co.objectconnexions.expressiveobjects.core.commons.xml.ContentWriter;
import uk.co.objectconnexions.expressiveobjects.core.commons.xml.XmlFile;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.data.xml.Utils;
import uk.co.objectconnexions.expressiveobjects.objectstore.xml.internal.services.ServiceManager;


public class XmlServiceManager implements ServiceManager {
    private static final String SERVICES_FILE_NAME = "services";
    private List<ServiceElement> services;
    private final XmlFile xmlFile;

    public XmlServiceManager(final XmlFile xmlFile) {
        this.xmlFile = xmlFile;
    }

    @Override
    public RootOid getOidForService(final ObjectSpecId objectSpecId) {
        for (final ServiceElement element: services) {
            if (element.oid.getObjectSpecId().equals(objectSpecId)) {
                return element.oid;
            }
        }
        return null;
    }

    @Override
    public void loadServices() {
        final ServiceHandler handler = new ServiceHandler();
        xmlFile.parse(handler, SERVICES_FILE_NAME);
        services = handler.services;
    }

    @Override
    public void registerService(final RootOid rootOid) {
        final RootOidDefault soid = (RootOidDefault) rootOid;
        final ServiceElement element = new ServiceElement(soid);
        services.add(element);
        saveServices();
    }

    public final void saveServices() {
        xmlFile.writeXml(SERVICES_FILE_NAME, new ContentWriter() {
            @Override
            public void write(final Writer writer) throws IOException {
                final String tag = SERVICES_FILE_NAME;
                writer.append("<" + tag + ">\n");
                for (final ServiceElement element: services) {
                    writer.append("  <service");
//                    Utils.appendAttribute(writer, "type", element.oid.getObjectSpecId());
//                    Utils.appendAttribute(writer, "id", element.oid.getIdentifier());

                  Utils.appendAttribute(writer, "oid", element.oid.enString(getOidMarshaller()));

                    writer.append("/>\n");
                }
                writer.append("</" + tag + ">\n");
            }
        });
    }
    
    
    ////////////////////////////////////////////////////
    // dependencies (from context)
    ////////////////////////////////////////////////////
    
    protected OidMarshaller getOidMarshaller() {
		return ExpressiveObjectsContext.getOidMarshaller();
	}
}

class ServiceElement {
    final RootOid oid;

    public ServiceElement(final RootOid oid) {
        Assert.assertNotNull("oid", oid.enString(getOidMarshaller()));
        this.oid = oid;
    }

    
    /////////////////////////////////////////////////////
    // dependencies (from context)
    /////////////////////////////////////////////////////
    
    protected OidMarshaller getOidMarshaller() {
		return ExpressiveObjectsContext.getOidMarshaller();
	}

}

class ServiceHandler extends DefaultHandler {
    List<ServiceElement> services = Lists.newArrayList();

    @Override
    public void startElement(final String ns, final String name, final String tagName, final Attributes attrs) throws SAXException {
        if (tagName.equals("service")) {
//            final String objectType = attrs.getValue("type");
//            final String identifier = attrs.getValue("id");
//             final RootOid rootOid = RootOidDefault.create(objectType, identifier);
            
            final String oidStr = attrs.getValue("oid");
            RootOid rootOid = getOidMarshaller().unmarshal(oidStr, RootOid.class);
            final ServiceElement service = new ServiceElement(rootOid);
            services.add(service);
        }
    }

    
    ///////////////////////////////////////////////////////
    // dependencies (from context)
    ///////////////////////////////////////////////////////
    
    protected OidMarshaller getOidMarshaller() {
        return ExpressiveObjectsContext.getOidMarshaller();
    }
}
