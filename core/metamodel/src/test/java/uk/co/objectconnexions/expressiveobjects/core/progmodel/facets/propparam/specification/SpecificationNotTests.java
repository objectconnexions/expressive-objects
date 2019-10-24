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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.propparam.specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.spec.Specification;
import uk.co.objectconnexions.expressiveobjects.applib.spec.SpecificationNot;

public class SpecificationNotTests {

    private final Specification alwaysSatisfied = new SpecificationAlwaysSatisfied();
    private final Specification neverSatisfied = new SpecificationNeverSatisfied();

    @Test
    public void notSatisfiedIfUnderlyingIs() {
        class MySpecNot extends SpecificationNot {
            public MySpecNot() {
                super(alwaysSatisfied);
            }
        }
        ;
        final Specification mySpecOr = new MySpecNot();
        assertThat(mySpecOr.satisfies(null), is(not(nullValue())));
        assertThat(mySpecOr.satisfies(null), is("not satisfied"));
    }

    @Test
    public void satisfiedIfUnderlyingIsNot() {
        class MySpecNot extends SpecificationNot {
            public MySpecNot() {
                super(neverSatisfied);
            }
        }
        ;
        final Specification mySpecOr = new MySpecNot();
        assertThat(mySpecOr.satisfies(null), is(nullValue()));
    }

}
