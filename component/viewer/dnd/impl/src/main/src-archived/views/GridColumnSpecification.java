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


package uk.co.objectconnexions.expressiveobjects.viewer.dnd.special;

import uk.co.objectconnexions.expressiveobjects.object.ObjectAdapterRuntimeException;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ObjectContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.OneToManyField;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ValueContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.core.AbstractCompositeViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.util.ViewFactory;

class GridColumnSpecification extends AbstractCompositeViewSpecification{

    GridColumnSpecification() {
        builder = new ColumnLayout(155, new ObjectFieldBuilder(new ColumnSubviews()));
    }
	
	public String getName() {
		return "Grid Column";
	}
	
    private static class ColumnSubviews implements SubviewSpec {
        public View createSubview(Content content, ViewAxis axis) {
            ViewFactory factory = Skylark.getViewFactory();

            ViewSpecification specification;

            if (content instanceof OneToManyField) {
                specification = new ScheduleSpecification();
            } else if (content instanceof ValueContent) {
                specification = factory.getValueFieldSpecification((ValueContent) content);
            } else if (content instanceof ObjectContent) {
                specification = factory.getIconizedSubViewSpecification((ObjectContent) content);
            } else {
                throw new ObjectAdapterRuntimeException();
            }

            return specification.createView(content, axis);
        }

        public View decorateSubview(View view) {
            return view;
        }
    }
}


