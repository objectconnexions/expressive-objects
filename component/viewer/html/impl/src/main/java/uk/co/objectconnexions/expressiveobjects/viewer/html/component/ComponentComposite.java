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

package uk.co.objectconnexions.expressiveobjects.viewer.html.component;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.viewer.html.PathBuilder;

public class ComponentComposite implements Component {

    private final List<Component> components = new ArrayList<Component>();
    protected final PathBuilder pathBuilder;

    public ComponentComposite(final PathBuilder pathBuilder) {
        super();
        this.pathBuilder = pathBuilder;
    }

    protected String pathTo(final String prefix) {
        return pathBuilder.pathTo(prefix);
    }

    @Override
    public void write(final PrintWriter writer) {
        writeBefore(writer);
        for (final Component component : components) {
            write(writer, component);
        }
        writeAfter(writer);
        writer.println();
    }

    protected void write(final PrintWriter writer, final Component component) {
        component.write(writer);
    }

    protected void writeBefore(final PrintWriter writer) {
    }

    protected void writeAfter(final PrintWriter writer) {
    }

    public void add(final Component component) {
        components.add(component);
    }

}
