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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import uk.co.objectconnexions.expressiveobjects.applib.DomainObjectContainer;

public abstract class ClassSubstitutorAbstract implements ClassSubstitutor {

    private final Set<Class<?>> classesToIgnore = new HashSet<Class<?>>();

    /**
     * Will implicitly ignore the {@link DomainObjectContainer}.
     */
    public ClassSubstitutorAbstract() {
        ignore(DomainObjectContainer.class);
    }

    // /////////////////////////////////////////////////////////////////
    // init, shutdown
    // /////////////////////////////////////////////////////////////////

    /**
     * Default implementation does nothing.
     */
    @Override
    public void init() {
    }

    /**
     * Default implementation does nothing.
     */
    @Override
    public void shutdown() {
    }

    // /////////////////////////////////////////////////////////////////
    // ClassSubstitutor impl.
    // /////////////////////////////////////////////////////////////////

    /**
     * Hook method for subclasses to override if required.
     * 
     * <p>
     * Default implementation will either return the class, unless has been
     * registered as to be {@link #ignore(Class) ignore}d, in which case returns
     * <tt>null</tt>.
     */
    @Override
    public Class<?> getClass(final Class<?> cls) {
        if (shouldIgnore(cls)) {
            return null;
        }
        return cls;
    }

    private boolean shouldIgnore(final Class<?> cls) {
        if (cls.isArray()) {
            return shouldIgnore(cls.getComponentType());
        }
        return classesToIgnore.contains(cls);
    }

    // ////////////////////////////////////////////////////////////////////
    // ignoring
    // ////////////////////////////////////////////////////////////////////

    /**
     * For any classes registered as ignored, {@link #getClass(Class)} will
     * return <tt>null</tt>.
     */
    protected boolean ignore(final Class<?> q) {
        return classesToIgnore.add(q);
    }

    public Set<Class<?>> getIgnoredClasses() {
        return Collections.unmodifiableSet(classesToIgnore);
    }

    // ////////////////////////////////////////////////////////////////////
    // injectInto
    // ////////////////////////////////////////////////////////////////////

    @Override
    public void injectInto(final Object candidate) {
        if (ClassSubstitutorAware.class.isAssignableFrom(candidate.getClass())) {
            final ClassSubstitutorAware cast = ClassSubstitutorAware.class.cast(candidate);
            cast.setClassSubstitutor(this);
        }
    }

}
