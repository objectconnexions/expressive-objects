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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class ClassSubstitutorComposite_lifecycle {

    private final Mockery context = new JUnit4Mockery();

    final ClassSubstitutor mockSubstitutor = context.mock(ClassSubstitutor.class, "one");
    final ClassSubstitutor mockSubstitutor2 = context.mock(ClassSubstitutor.class, "two");

    private ClassSubstitutorComposite classSubstitutorComposite;

    @Before
    public void setUp() throws Exception {
        classSubstitutorComposite = new ClassSubstitutorComposite(mockSubstitutor, mockSubstitutor2);
    }

    @Test
    public void whenInitThenShouldPropogate() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockSubstitutor).init();
                one(mockSubstitutor2).init();
            }
        });
        classSubstitutorComposite.init();
    }

    @Test
    public void whenShutdownThenShouldPropogate() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockSubstitutor).shutdown();
                one(mockSubstitutor2).shutdown();
            }
        });
        classSubstitutorComposite.shutdown();
    }

}
