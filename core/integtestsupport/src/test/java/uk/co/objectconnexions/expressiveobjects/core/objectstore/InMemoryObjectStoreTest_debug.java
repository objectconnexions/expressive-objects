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

package uk.co.objectconnexions.expressiveobjects.core.objectstore;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugString;
import uk.co.objectconnexions.expressiveobjects.core.commons.matchers.ExpressiveObjectsMatchers;
import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures;
import uk.co.objectconnexions.expressiveobjects.core.objectstore.InMemoryObjectStore;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import org.junit.Rule;
import org.junit.Test;

public class InMemoryObjectStoreTest_debug {

    @Rule
    public ExpressiveObjectsSystemWithFixtures iswf = ExpressiveObjectsSystemWithFixtures.builder().build();
    
    private static InMemoryObjectStore getStore() {
        return (InMemoryObjectStore) ExpressiveObjectsContext.getPersistenceSession().getObjectStore();
    }

    @Test
    public void debugTitle() throws Exception {

        // when
        final String debugTitle = getStore().debugTitle();
        
        // then
        assertThat(debugTitle, is("In-Memory Object Store"));
    }


    @Test
    public void debugXxx_whenHasObject() throws Exception {

        // given
        iswf.persist(iswf.fixtures.smpl1);

        // when
        final DebugString debug = new DebugString();
        getStore().debugData(debug);
        
        
        // then
        assertThat(debug.toString(), ExpressiveObjectsMatchers.containsStripNewLines("SMPL:2"));
    }

    
    @Test
    public void testEmpty() throws Exception {
        
        // when
        final DebugString debug = new DebugString();
        getStore().debugData(debug);
        
        // then
        assertThat(debug.toString(), is("\nDomain Objects\n--------------\n\n"));
    }
}
