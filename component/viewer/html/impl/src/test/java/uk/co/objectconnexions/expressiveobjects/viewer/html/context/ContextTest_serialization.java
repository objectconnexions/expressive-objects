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
package uk.co.objectconnexions.expressiveobjects.viewer.html.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.viewer.html.HtmlViewerConstants;
import uk.co.objectconnexions.expressiveobjects.viewer.html.PathBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.html.PathBuilderDefault;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.ComponentFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.html.HtmlComponentFactory;

public class ContextTest_serialization {

    @Rule
    public ExpressiveObjectsSystemWithFixtures iswf = ExpressiveObjectsSystemWithFixtures.builder().build();

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_ONLY);
    
    @Mock
    private ExpressiveObjectsConfiguration expressiveObjectsConfiguration;
    
    private ComponentFactory factory;
    private PathBuilder pathBuilder;
    
    private Context viewerContext;

    @Before
    public void setUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);
        
        pathBuilder = new PathBuilderDefault("shtml");
        context.checking(new Expectations() {
            {
                allowing(expressiveObjectsConfiguration).getString(HtmlViewerConstants.STYLE_SHEET);
                will(returnValue("someStyleSheet.css"));

                allowing(expressiveObjectsConfiguration).getString(HtmlViewerConstants.HEADER_FILE);
                will(returnValue(null));

                allowing(expressiveObjectsConfiguration).getString(HtmlViewerConstants.HEADER);
                will(returnValue("<div></div>"));

                allowing(expressiveObjectsConfiguration).getString(HtmlViewerConstants.FOOTER_FILE);
                will(returnValue(null));

                allowing(expressiveObjectsConfiguration).getString(HtmlViewerConstants.FOOTER);
                will(returnValue("<div></div>"));
            }
        });

        factory = new HtmlComponentFactory(pathBuilder, expressiveObjectsConfiguration);
        
        viewerContext = new Context(factory);
    }


    @Test
    public void writeObject() throws IOException {
        OutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos);
        objectOutputStream.writeObject(viewerContext);
    }

}
