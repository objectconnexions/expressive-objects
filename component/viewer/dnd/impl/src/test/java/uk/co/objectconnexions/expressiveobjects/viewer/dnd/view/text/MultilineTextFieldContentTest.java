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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.text;

import java.util.Collections;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContextStatic;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactoryDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileLoader;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MultilineTextFieldContentTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private TextContent content;
    
    @Mock
    protected TemplateImageLoader mockTemplateImageLoader;
    @Mock
    protected SpecificationLoaderSpi mockSpecificationLoader;
    @Mock
    protected PersistenceSessionFactory mockPersistenceSessionFactory;
    @Mock
    private UserProfileLoader mockUserProfileLoader;
    @Mock
    protected AuthenticationManager mockAuthenticationManager;
    @Mock
    protected AuthorizationManager mockAuthorizationManager;

    @Before
    public void setUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);

        context.ignoring(mockTemplateImageLoader, mockSpecificationLoader, mockPersistenceSessionFactory, mockUserProfileLoader, mockAuthenticationManager, mockAuthorizationManager);

        final ExpressiveObjectsSessionFactoryDefault sessionFactory = new ExpressiveObjectsSessionFactoryDefault(DeploymentType.EXPLORATION, new ExpressiveObjectsConfigurationDefault(), mockTemplateImageLoader, mockSpecificationLoader, mockAuthenticationManager, mockAuthorizationManager, mockUserProfileLoader,
                mockPersistenceSessionFactory, Collections.emptyList(), new OidMarshaller());
        ExpressiveObjectsContextStatic.createRelaxedInstance(sessionFactory);
        sessionFactory.init();

        final TextBlockTarget target = new TextBlockTargetExample();

        content = new TextContent(target, 4, TextContent.WRAPPING);
        content.setText("Line one\nLine two\nLine three\nLine four that is long enough that it wraps");
    }

    @Test
    public void testDeleteOnSingleLine() {
        final TextSelection selection = new TextSelection(content);
        selection.resetTo(new CursorPosition(content, 1, 3));
        selection.extendTo(new CursorPosition(content, 1, 7));
        content.delete(selection);
        Assert.assertEquals("Line one\nLino\nLine three\nLine four that is long enough that it wraps", content.getText());
    }

    @Test
    public void testDeleteOnSingleLineWithStartAndEndOutOfOrder() {
        final TextSelection selection = new TextSelection(content);
        selection.resetTo(new CursorPosition(content, 1, 7));
        selection.extendTo(new CursorPosition(content, 1, 3));
        content.delete(selection);
        Assert.assertEquals("Line one\nLino\nLine three\nLine four that is long enough that it wraps", content.getText());
    }

    @Test
    public void testDeleteAcrossTwoLines() {
        final TextSelection selection = new TextSelection(content);
        selection.resetTo(new CursorPosition(content, 0, 6));
        selection.extendTo(new CursorPosition(content, 1, 6));
        content.delete(selection);
        Assert.assertEquals("Line owo\nLine three\nLine four that is long enough that it wraps", content.getText());
    }

    @Test
    public void testDeleteAcrossThreeLines() {
        final TextSelection selection = new TextSelection(content);
        selection.resetTo(new CursorPosition(content, 0, 6));
        selection.extendTo(new CursorPosition(content, 2, 6));
        content.delete(selection);
        Assert.assertEquals("Line ohree\nLine four that is long enough that it wraps", content.getText());
    }

    @Test
    public void testDeleteAcrossThreeLinesIncludingWrappedBlock() {
        final TextSelection selection = new TextSelection(content);
        selection.resetTo(new CursorPosition(content, 2, 5));
        selection.extendTo(new CursorPosition(content, 4, 5));
        content.delete(selection);
        Assert.assertEquals("Line one\nLine two\nLine enough that it wraps", content.getText());
    }

    @Test
    public void testDeleteWithinWrappedBlock() {
        final TextSelection selection = new TextSelection(content);
        selection.resetTo(new CursorPosition(content, 5, 0));
        selection.extendTo(new CursorPosition(content, 5, 3));
        content.delete(selection);
        Assert.assertEquals("Line one\nLine two\nLine three\nLine four that is long enough that wraps", content.getText());
    }

    @Test
    public void testDeleteMultipleLinesWithinWrappedBlock() {
        final TextSelection selection = new TextSelection(content);
        selection.resetTo(new CursorPosition(content, 3, 5));
        selection.extendTo(new CursorPosition(content, 5, 3));
        content.delete(selection);
        Assert.assertEquals("Line one\nLine two\nLine three\nLine wraps", content.getText());
    }

    @Test
    public void testLineBreaks() {
        Assert.assertEquals(6, content.getNoLinesOfContent());

        content.setNoDisplayLines(8);
        final String[] lines = content.getDisplayLines();

        Assert.assertEquals(8, lines.length);
        Assert.assertEquals("Line one", lines[0]);
        Assert.assertEquals("Line two", lines[1]);
        Assert.assertEquals("Line three", lines[2]);
        Assert.assertEquals("Line four that is ", lines[3]);
        Assert.assertEquals("long enough that ", lines[4]);
        Assert.assertEquals("it wraps", lines[5]);
        Assert.assertEquals("", lines[6]);
        Assert.assertEquals("", lines[7]);

    }

}
