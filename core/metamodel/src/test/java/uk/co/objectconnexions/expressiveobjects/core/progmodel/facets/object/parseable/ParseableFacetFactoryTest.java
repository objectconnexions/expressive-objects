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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.parseable;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Parseable;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.ParseableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.noruntime.RuntimeContextNoRuntime;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;

public class ParseableFacetFactoryTest extends AbstractFacetFactoryTest {

    private ParseableFacetFactory facetFactory;
    private ExpressiveObjectsConfigurationDefault expressiveObjectsConfigurationDefault;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new ParseableFacetFactory();
        expressiveObjectsConfigurationDefault = new ExpressiveObjectsConfigurationDefault();
        facetFactory.setConfiguration(expressiveObjectsConfigurationDefault);
        facetFactory.setRuntimeContext(new RuntimeContextNoRuntime(DeploymentCategory.PRODUCTION));
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testFacetPickedUp() {

        facetFactory.process(new ProcessClassContext(MyParseableUsingParserName.class, methodRemover, facetedMethod));

        final ParseableFacet facet = facetedMethod.getFacet(ParseableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof ParseableFacetAbstract);
    }

    public void testFacetFacetHolderStored() {

        facetFactory.process(new ProcessClassContext(MyParseableUsingParserName.class, methodRemover, facetedMethod));

        final ParseableFacetAbstract parseableFacet = (ParseableFacetAbstract) facetedMethod.getFacet(ParseableFacet.class);
        assertEquals(facetedMethod, parseableFacet.getFacetHolder());
    }

    public void testNoMethodsRemoved() {

        facetFactory.process(new ProcessClassContext(MyParseableUsingParserName.class, methodRemover, facetedMethod));

        assertNoMethodsRemoved();
    }

    @Parseable(parserName = "uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.parseable.ParseableFacetFactoryTest$MyParseableUsingParserName")
    public static class MyParseableUsingParserName extends ParserNoop<MyParseableUsingParserName> {

        /**
         * Required since is a Parser.
         */
        public MyParseableUsingParserName() {
        }
    }

    public void testParseableUsingParserName() {

        facetFactory.process(new ProcessClassContext(MyParseableUsingParserName.class, methodRemover, facetedMethod));

        final ParseableFacetAbstract parseableFacet = (ParseableFacetAbstract) facetedMethod.getFacet(ParseableFacet.class);
        assertEquals(MyParseableUsingParserName.class, parseableFacet.getParserClass());
    }

    public static class ParserNoop<T> implements Parser<T> {
        @Override
        public T parseTextEntry(final Object context, final String entry, Localization localization) {
            return null;
        }

        @Override
        public int typicalLength() {
            return 0;
        }

        @Override
        public String displayTitleOf(final T object, final Localization localization) {
            return null;
        }

        @Override
        public String displayTitleOf(final T object, final String usingMask) {
            return null;
        }

        @Override
        public String parseableTitleOf(final T existing) {
            return null;
        }
    }

    @Parseable(parserClass = MyParseableUsingParserClass.class)
    public static class MyParseableUsingParserClass extends ParserNoop<MyParseableUsingParserClass> {
        /**
         * Required since is a Parser.
         */
        public MyParseableUsingParserClass() {
        }
    }

    public void testParseableUsingParserClass() {

        facetFactory.process(new ProcessClassContext(MyParseableUsingParserClass.class, methodRemover, facetedMethod));

        final ParseableFacetAbstract parseableFacet = (ParseableFacetAbstract) facetedMethod.getFacet(ParseableFacet.class);
        assertEquals(MyParseableUsingParserClass.class, parseableFacet.getParserClass());
    }

    public void testParseableMustBeAParser() {
        // no test, because compiler prevents us from nominating a class that
        // doesn't
        // implement Parser
    }

    @Parseable(parserClass = MyParseableWithoutNoArgConstructor.class)
    public static class MyParseableWithoutNoArgConstructor extends ParserNoop<MyParseableWithoutNoArgConstructor> {

        // no no-arg constructor

        public MyParseableWithoutNoArgConstructor(final int value) {
        }
    }

    public void testParseableHaveANoArgConstructor() {
        facetFactory.process(new ProcessClassContext(MyParseableWithoutNoArgConstructor.class, methodRemover, facetedMethod));

        final ParseableFacetAbstract parseableFacet = (ParseableFacetAbstract) facetedMethod.getFacet(ParseableFacet.class);
        assertNull(parseableFacet);
    }

    @Parseable(parserClass = MyParseableWithoutPublicNoArgConstructor.class)
    public static class MyParseableWithoutPublicNoArgConstructor extends ParserNoop<MyParseableWithoutPublicNoArgConstructor> {

        // no public no-arg constructor
        MyParseableWithoutPublicNoArgConstructor() {
            this(0);
        }

        public MyParseableWithoutPublicNoArgConstructor(final int value) {
        }
    }

    public void testParseableHaveAPublicNoArgConstructor() {
        facetFactory.process(new ProcessClassContext(MyParseableWithoutPublicNoArgConstructor.class, methodRemover, facetedMethod));

        final ParseableFacetAbstract parseableFacet = (ParseableFacetAbstract) facetedMethod.getFacet(ParseableFacet.class);
        assertNull(parseableFacet);
    }

    @Parseable()
    public static class MyParseableWithParserSpecifiedUsingConfiguration extends ParserNoop<MyParseableWithParserSpecifiedUsingConfiguration> {

        /**
         * Required since is a Parser.
         */
        public MyParseableWithParserSpecifiedUsingConfiguration() {
        }
    }

    public void testParserNameCanBePickedUpFromConfiguration() {
        final String className = "uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.parseable.ParseableFacetFactoryTest$MyParseableWithParserSpecifiedUsingConfiguration";
        expressiveObjectsConfigurationDefault.add(ParserUtil.PARSER_NAME_KEY_PREFIX + canonical(className) + ParserUtil.PARSER_NAME_KEY_SUFFIX, className);
        facetFactory.process(new ProcessClassContext(MyParseableWithParserSpecifiedUsingConfiguration.class, methodRemover, facetedMethod));
        final ParseableFacetAbstract facet = (ParseableFacetAbstract) facetedMethod.getFacet(ParseableFacet.class);
        assertNotNull(facet);
        assertEquals(MyParseableWithParserSpecifiedUsingConfiguration.class, facet.getParserClass());
    }

    public static class NonAnnotatedParseableParserSpecifiedUsingConfiguration extends ParserNoop<NonAnnotatedParseableParserSpecifiedUsingConfiguration> {

        /**
         * Required since is a Parser.
         */
        public NonAnnotatedParseableParserSpecifiedUsingConfiguration() {
        }
    }

    public void testNonAnnotatedParseableCanPickUpParserFromConfiguration() {
        final String className = "uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.parseable.ParseableFacetFactoryTest$NonAnnotatedParseableParserSpecifiedUsingConfiguration";
        expressiveObjectsConfigurationDefault.add(ParserUtil.PARSER_NAME_KEY_PREFIX + canonical(className) + ParserUtil.PARSER_NAME_KEY_SUFFIX, className);
        facetFactory.process(new ProcessClassContext(NonAnnotatedParseableParserSpecifiedUsingConfiguration.class, methodRemover, facetedMethod));
        final ParseableFacetAbstract facet = (ParseableFacetAbstract) facetedMethod.getFacet(ParseableFacet.class);
        assertNotNull(facet);
        assertEquals(NonAnnotatedParseableParserSpecifiedUsingConfiguration.class, facet.getParserClass());
    }

    private String canonical(final String className) {
        return className.replace('$', '.');
    }

}
