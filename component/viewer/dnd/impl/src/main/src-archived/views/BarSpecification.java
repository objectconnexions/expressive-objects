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

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ObjectContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.core.AbstractCompositeViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.value.PercentageBarField;

class BarSpecification extends AbstractCompositeViewSpecification {
	public BarSpecification() {
		builder = new StackLayout(new ObjectFieldBuilder(new DataFormSubviews()));
	}
	
	private static class DataFormSubviews implements SubviewSpec {
		public View createSubview(Content content, ViewAxis axis) {
			if(content instanceof ObjectContent && ((ObjectContent) content).getObject() instanceof Percentage) { 
				ViewSpecification specification = new PercentageBarField.Specification();
				return specification.createView(content, axis);
			}
			
			return null;
		}
		
		public View decorateSubview(View view) {
			return view;
		}
	}

	public String getName() {
		return "Data Form";
	}
}


