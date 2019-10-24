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

package uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

public final class ReturnArgumentJMockAction implements Action {
    private final int i;

    public ReturnArgumentJMockAction(final int i) {
        this.i = i;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("parameter #" + i + " ");
    }

    @Override
    public Object invoke(final Invocation invocation) throws Throwable {
        return invocation.getParameter(i);
    }

    /**
     * Factory
     */
    public static Action returnArgument(final int i) {
        return new ReturnArgumentJMockAction(i);
    }

}
