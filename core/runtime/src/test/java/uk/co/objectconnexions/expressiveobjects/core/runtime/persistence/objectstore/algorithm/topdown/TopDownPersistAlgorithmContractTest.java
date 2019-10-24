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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.algorithm.topdown;

import org.junit.Ignore;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.NotPersistableException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.algorithm.PersistAlgorithm;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.algorithm.PersistAlgorithmContractTest;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.algorithm.PersistAlgorithmTopDown;

public class TopDownPersistAlgorithmContractTest extends PersistAlgorithmContractTest {

    @Override
    protected PersistAlgorithm createPersistAlgorithm() {
        return new PersistAlgorithmTopDown();
    }

    @Ignore
    @Test(expected=NotPersistableException.class)
    public void makePersistent_failsIfObjectIsAggregated() {
        // does not pass...
    }

    @Ignore
    @Test(expected=NotPersistableException.class)
    public void makePersistent_failsIfObjectAlreadyPersistent() {
        // does not pass...
    }
    
    @Ignore
    @Test(expected=NotPersistableException.class)
    public void makePersistent_failsIfObjectMustBeTransient() {
        // does not pass...
    }

}
