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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.encodable;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Encodable;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.encodeable.EncodableFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryTest;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.encodeable.EncodableAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.encodeable.EncodableFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.encodeable.EncoderDecoderUtil;

public class EncodableFacetFactoryTest extends AbstractFacetFactoryTest {

    private EncodableAnnotationFacetFactory facetFactory;
    private ExpressiveObjectsConfigurationDefault expressiveObjectsConfigurationDefault;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new EncodableAnnotationFacetFactory();
        expressiveObjectsConfigurationDefault = new ExpressiveObjectsConfigurationDefault();
        facetFactory.setConfiguration(expressiveObjectsConfigurationDefault);
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testFacetPickedUp() {

        facetFactory.process(new ProcessClassContext(MyEncodableUsingEncoderDecoderName.class, methodRemover, facetedMethod));

        final EncodableFacet facet = facetedMethod.getFacet(EncodableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof EncodableFacetAbstract);
    }

    public void testFacetFacetHolderStored() {

        facetFactory.process(new ProcessClassContext(MyEncodableUsingEncoderDecoderName.class, methodRemover, facetedMethod));

        final EncodableFacetAbstract valueFacet = (EncodableFacetAbstract) facetedMethod.getFacet(EncodableFacet.class);
        assertEquals(facetedMethod, valueFacet.getFacetHolder());
    }

    public void testNoMethodsRemoved() {

        facetFactory.process(new ProcessClassContext(MyEncodableUsingEncoderDecoderName.class, methodRemover, facetedMethod));

        assertNoMethodsRemoved();
    }

    abstract static class EncoderDecoderNoop<T> implements EncoderDecoder<T> {

        @Override
        public T fromEncodedString(final String encodedString) {
            return null;
        }

        @Override
        public String toEncodedString(final T toEncode) {
            return null;
        }
    }

    @Encodable(encoderDecoderName = "uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.encodable.EncodableFacetFactoryTest$MyEncodableUsingEncoderDecoderName")
    public static class MyEncodableUsingEncoderDecoderName extends EncoderDecoderNoop<MyEncodableUsingEncoderDecoderName> {

        /**
         * Required since is an EncoderDecoder
         */
        public MyEncodableUsingEncoderDecoderName() {
        }

    }

    public void testEncodeableUsingEncoderDecoderName() {

        facetFactory.process(new ProcessClassContext(MyEncodableUsingEncoderDecoderName.class, methodRemover, facetedMethod));

        final EncodableFacetAbstract encodeableFacet = (EncodableFacetAbstract) facetedMethod.getFacet(EncodableFacet.class);
        assertEquals(MyEncodableUsingEncoderDecoderName.class, encodeableFacet.getEncoderDecoderClass());
    }

    @Encodable(encoderDecoderClass = MyEncodeableUsingEncoderDecoderClass.class)
    public static class MyEncodeableUsingEncoderDecoderClass extends EncoderDecoderNoop<MyEncodeableUsingEncoderDecoderClass> {

        /**
         * Required since is a EncoderDecoder.
         */
        public MyEncodeableUsingEncoderDecoderClass() {
        }

    }

    public void testEncodeableUsingEncoderDecoderClass() {

        facetFactory.process(new ProcessClassContext(MyEncodeableUsingEncoderDecoderClass.class, methodRemover, facetedMethod));

        final EncodableFacetAbstract encodeableFacet = (EncodableFacetAbstract) facetedMethod.getFacet(EncodableFacet.class);
        assertEquals(MyEncodeableUsingEncoderDecoderClass.class, encodeableFacet.getEncoderDecoderClass());
    }

    public void testEncodeableMustBeAEncoderDecoder() {
        // no test, because compiler prevents us from nominating a class that
        // doesn't
        // implement EncoderDecoder
    }

    @Encodable(encoderDecoderClass = MyEncodeableWithoutNoArgConstructor.class)
    public static class MyEncodeableWithoutNoArgConstructor extends EncoderDecoderNoop<MyEncodeableWithoutNoArgConstructor> {

        // no no-arg constructor

        public MyEncodeableWithoutNoArgConstructor(final int value) {
        }

    }

    public void testEncodeableHaveANoArgConstructor() {
        facetFactory.process(new ProcessClassContext(MyEncodeableWithoutNoArgConstructor.class, methodRemover, facetedMethod));

        final EncodableFacetAbstract encodeableFacet = (EncodableFacetAbstract) facetedMethod.getFacet(EncodableFacet.class);
        assertNull(encodeableFacet);
    }

    @Encodable(encoderDecoderClass = MyEncodeableWithoutPublicNoArgConstructor.class)
    public static class MyEncodeableWithoutPublicNoArgConstructor extends EncoderDecoderNoop<MyEncodeableWithoutPublicNoArgConstructor> {

        // no public no-arg constructor
        MyEncodeableWithoutPublicNoArgConstructor() {
        }

        public MyEncodeableWithoutPublicNoArgConstructor(final int value) {
        }

    }

    public void testEncodeableHaveAPublicNoArgConstructor() {
        facetFactory.process(new ProcessClassContext(MyEncodeableWithoutPublicNoArgConstructor.class, methodRemover, facetedMethod));

        final EncodableFacetAbstract encodeableFacet = (EncodableFacetAbstract) facetedMethod.getFacet(EncodableFacet.class);
        assertNull(encodeableFacet);
    }

    @Encodable()
    public static class MyEncodableWithEncoderDecoderSpecifiedUsingConfiguration extends EncoderDecoderNoop<MyEncodableWithEncoderDecoderSpecifiedUsingConfiguration> {

        /**
         * Required since is a EncoderDecoder.
         */
        public MyEncodableWithEncoderDecoderSpecifiedUsingConfiguration() {
        }
    }

    public void testEncoderDecoderNameCanBePickedUpFromConfiguration() {
        final String className = "uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.encodable.EncodableFacetFactoryTest$MyEncodableWithEncoderDecoderSpecifiedUsingConfiguration";
        expressiveObjectsConfigurationDefault.add(EncoderDecoderUtil.ENCODER_DECODER_NAME_KEY_PREFIX + canonical(className) + EncoderDecoderUtil.ENCODER_DECODER_NAME_KEY_SUFFIX, className);
        facetFactory.process(new ProcessClassContext(MyEncodableWithEncoderDecoderSpecifiedUsingConfiguration.class, methodRemover, facetedMethod));
        final EncodableFacetAbstract facet = (EncodableFacetAbstract) facetedMethod.getFacet(EncodableFacet.class);
        assertNotNull(facet);
        assertEquals(MyEncodableWithEncoderDecoderSpecifiedUsingConfiguration.class, facet.getEncoderDecoderClass());
    }

    public static class NonAnnotatedEncodeableEncoderDecoderSpecifiedUsingConfiguration extends EncoderDecoderNoop<NonAnnotatedEncodeableEncoderDecoderSpecifiedUsingConfiguration> {

        /**
         * Required since is a EncoderDecoder.
         */
        public NonAnnotatedEncodeableEncoderDecoderSpecifiedUsingConfiguration() {
        }
    }

    public void testNonAnnotatedEncodeableCanPickUpEncoderDecoderFromConfiguration() {
        final String className = "uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.encodable.EncodableFacetFactoryTest$NonAnnotatedEncodeableEncoderDecoderSpecifiedUsingConfiguration";
        expressiveObjectsConfigurationDefault.add(EncoderDecoderUtil.ENCODER_DECODER_NAME_KEY_PREFIX + canonical(className) + EncoderDecoderUtil.ENCODER_DECODER_NAME_KEY_SUFFIX, className);
        facetFactory.process(new ProcessClassContext(NonAnnotatedEncodeableEncoderDecoderSpecifiedUsingConfiguration.class, methodRemover, facetedMethod));
        final EncodableFacetAbstract facet = (EncodableFacetAbstract) facetedMethod.getFacet(EncodableFacet.class);
        assertNotNull(facet);
        assertEquals(NonAnnotatedEncodeableEncoderDecoderSpecifiedUsingConfiguration.class, facet.getEncoderDecoderClass());
    }

    private String canonical(final String className) {
        return className.replace('$', '.');
    }

}
