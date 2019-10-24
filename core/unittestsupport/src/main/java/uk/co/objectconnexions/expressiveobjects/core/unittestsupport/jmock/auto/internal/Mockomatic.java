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
/**
Copyright (c) 2000-2007, jMock.org
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer. Redistributions in binary form must reproduce
the above copyright notice, this list of conditions and the following disclaimer in
the documentation and/or other materials provided with the distribution.

Neither the name of jMock nor the names of its contributors may be used to endorse
or promote products derived from this software without specific prior written
permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
DAMAGE.
 */
package uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.internal;

import java.lang.reflect.Field;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Auto;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;

public class Mockomatic {
    private final Mockery mockery;

    public Mockomatic(final Mockery mockery) {
        this.mockery = mockery;
    }

    public void fillIn(final Object object) {
        fillIn(object, AllDeclaredFields.in(object.getClass()));
    }

    public void fillIn(final Object object, final List<Field> knownFields) {
        for (final Field field : knownFields) {
            if (field.isAnnotationPresent(Mock.class)) {
                autoMock(object, field);
            } else if (field.isAnnotationPresent(Auto.class)) {
                autoInstantiate(object, field);
            }
        }
    }

    private void autoMock(final Object object, final Field field) {
        setAutoField(field, object, mockery.mock(field.getType(), field.getName()), "auto-mock field " + field.getName());
    }

    private void autoInstantiate(final Object object, final Field field) {
        final Class<?> type = field.getType();
        if (type == States.class) {
            autoInstantiateStates(field, object);
        } else if (type == Sequence.class) {
            autoInstantiateSequence(field, object);
        } else {
            throw new IllegalStateException("cannot auto-instantiate field of type " + type.getName());
        }
    }

    private void autoInstantiateStates(final Field field, final Object object) {
        setAutoField(field, object, mockery.states(field.getName()), "auto-instantiate States field " + field.getName());
    }

    private void autoInstantiateSequence(final Field field, final Object object) {
        setAutoField(field, object, mockery.sequence(field.getName()), "auto-instantiate Sequence field " + field.getName());
    }

    private void setAutoField(final Field field, final Object object, final Object value, final String description) {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("cannot " + description, e);
        }
    }

}