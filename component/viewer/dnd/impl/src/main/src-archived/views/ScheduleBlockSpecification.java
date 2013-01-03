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
import uk.co.objectconnexions.expressiveobjects.object.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.object.ObjectSpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.object.reflect.ObjectField;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ObjectContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.basic.SimpleIdentifier;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.core.AbstractCompositeViewSpecification;

import org.apache.log4j.Logger;

public class ScheduleBlockSpecification extends AbstractCompositeViewSpecification{
	private static final Logger LOG = Logger.getLogger(ScheduleBlockView.class);

	public View createView(Content content, ViewAxis axis) {
    	ObjectSpecification nc = ((ObjectContent) content).getObject().getSpecification();
    	ObjectField[] flds = nc.getFields();
    	ObjectField timePeriodField = null;
    	ObjectField colorField = null;
    	for (int i = 0; i < flds.length; i++) {
			ObjectField field = flds[i];
			if(field.getType().isOfType(ExpressiveObjects.getSpecificationLoader().loadSpecification(TimePeriod.class))) {
				LOG.debug("found TimePeriod field " + field);
				timePeriodField = field;
			}
			if(field.getType().isOfType(ExpressiveObjects.getSpecificationLoader().loadSpecification(uk.co.objectconnexions.expressiveobjects.application.value.Color.class))) {
				LOG.debug("found Color field " + field);
				colorField = field;
			}
		}
    	if(timePeriodField == null) {
        	throw new ObjectAdapterRuntimeException("Can't create Shedule view without a TimePeriod");
    	} else {
    		return new SimpleIdentifier(new ScheduleBlockView(content, this, axis, timePeriodField, colorField));
    	}
	}
	
	public String getName() {
		return "Schedule Block";
	}
}

