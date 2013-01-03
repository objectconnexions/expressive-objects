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

package uk.co.objectconnexions.expressiveobjects.core.runtime.system;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContextStatic;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContextThreadLocal;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactory;

/**
 * @see DeploymentType
 */
public abstract class ContextCategory {

    public static ContextCategory STATIC_RELAXED = new ContextCategory() {
        @Override
        public void initContext(final ExpressiveObjectsSessionFactory sessionFactory) {
            ExpressiveObjectsContextStatic.createRelaxedInstance(sessionFactory);
        }

        @Override
        public boolean canSpecifyViewers(final List<String> viewers) {
            // no more than one
            return viewers.size() <= 1;
        }
    };

    public static ContextCategory STATIC = new ContextCategory() {
        @Override
        public void initContext(final ExpressiveObjectsSessionFactory sessionFactory) {
            ExpressiveObjectsContextStatic.createInstance(sessionFactory);
        }

        @Override
        public boolean canSpecifyViewers(final List<String> viewers) {
            // no more than one
            return viewers.size() == 1;
        }
    };
    public static ContextCategory THREADLOCAL = new ContextCategory() {
        @Override
        public void initContext(final ExpressiveObjectsSessionFactory sessionFactory) {
            ExpressiveObjectsContextThreadLocal.createInstance(sessionFactory);
        }

        @Override
        public boolean canSpecifyViewers(final List<String> viewers) {
            // as many as you like
            return true;
        }
    };

    public abstract void initContext(ExpressiveObjectsSessionFactory sessionFactory);

    /**
     * Whether the list of connector names provided is compatible with this
     * {@link ContextCategory}.
     */
    public abstract boolean canSpecifyViewers(List<String> viewers);

}
