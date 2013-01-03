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

import uk.co.objectconnexions.expressiveobjects.object.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.object.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.object.ObjectSpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.object.reflect.ObjectField;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ObjectContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.core.AbstractBuilderDecorator;

import java.sql.Time;

class ScheduleLayout extends AbstractBuilderDecorator {
    private final int from = 7 * Time.HOUR;
    private final int to = Time.HOUR * 10;

    public ScheduleLayout(CollectionElementBuilder viewer) {
        super(viewer);
    }

    public Size getRequiredSize(View view) {
		return new Size(155, 400);
	}
    
    public void layout(View view) {
        ObjectField field = null;
        int x = 0;
        Size size = view.getSize();
        size.contract(view.getPadding());

        int width = size.getWidth();
        int maxHeight = size.getHeight();

        View[] views = view.getSubviews();

        for (int i = 0; i < views.length; i++) {
            View v = views[i];
            ObjectAdapter object = ((ObjectContent) v.getContent()).getObject();

            if (field == null) {
                ObjectSpecification nc = object.getSpecification();
                ObjectField[] fields = nc.getFields();

                for (int j = 0; j < fields.length; j++) {
                    field = fields[j];

                    if (field.getType().isOfType(ExpressiveObjects.getSpecificationLoader().loadSpecification(TimePeriod.class))) {
                        break;
                    }
                }
            }

            TimePeriod tp = (TimePeriod) object.getField(field);
			int y = (int) (((tp.getStart().longValue() - from) * maxHeight) / to);
            int height = (int) (((tp.getEnd().longValue() - tp.getStart().longValue()) * maxHeight) / to);

            v.setLocation(new Location(x, y));
            v.setSize(new Size(width, height));
        }
    }

	public Time getTime(View view, int y) {
        Size size = view.getSize();
        int maxHeight = size.getHeight();

        int longtime = (y * to) / maxHeight + from;
        Time t = new Time();
        t.setValue(longtime);
        return  t;
	}
}

