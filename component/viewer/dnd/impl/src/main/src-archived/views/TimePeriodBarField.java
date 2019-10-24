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


package uk.co.objectconnexions.expressiveobjects.viewer.dnd.value;

import uk.co.objectconnexions.expressiveobjects.object.InvalidEntryException;
import uk.co.objectconnexions.expressiveobjects.object.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.extensions.dndviewer.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.utility.NotImplementedException;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.InternalDrag;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ObjectContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Style;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.core.AbstractFieldSpecification;

import java.sql.Time;

import org.apache.log4j.Logger;


public class TimePeriodBarField extends AbstractField {

    public static class Specification extends AbstractFieldSpecification {
        public View createView(Content content, ViewAxis axis) {
            return new TimePeriodBarField(content, this, axis);
        }

        public String getName() {
            return "Period graph";
        }
	    
	    public boolean canDisplay(ObjectAdapter object) {
	    	return object instanceof TimePeriod;
		}
   }
    private static final Logger LOG = Logger.getLogger(TimePeriodBarField.class);
    private int endTime;
  //  private int startTime;

    protected TimePeriodBarField(Content content, ViewSpecification specification, ViewAxis axis) {
        super(content, specification, axis);
    }

    public void drag(InternalDrag drag) {
        float x = drag.getLocation().getX() - 2;
        float max = getSize().getWidth() - 4;

        if ((x >= 0) && (x <= max)) {
            int time = (int) (x / max * 3600 * 24);
            endTime = time;
            initiateSave();
        }
    }
    
    protected void save() {
        Time end = getPeriod().getEnd();
        end.setValue(endTime);

        Time start = getPeriod().getStart();

        TimePeriod tp = new TimePeriod();
        tp.setValue(start, end);
        try {
            parseEntry(tp.title().toString());
        } catch (InvalidEntryException e) {
            throw new NotImplementedException();
        }
        LOG.debug("adjust time " + endTime + " " + getPeriod());
        markDamaged();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);

        Color color = getState().isObjectIdentified() ? Toolkit.getColor("primary2") : Toolkit.getColor("secondary1");
        Size size = getSize();
        int width = size.getWidth();
        int height = size.getHeight();
        canvas.drawRectangle(0, 0, width - 1, height - 1, color);

        TimePeriod p = getPeriod();

        int max = width - 4;
        int start = (int) ((p.isEmpty() ? 0 : (p.getStart().longValue() * max)) / (3600 * 24)) + 2;
        int end = (int) ((p.isEmpty() ? max : (p.getEnd().longValue() * max)) / (3600 * 24)) + 2;
        canvas.drawSolidRectangle(start, 2, end - start, height - 5, Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY3));
        canvas.drawRectangle(start, 2, end - start, height - 5, color);
        canvas.drawText(p.title().toString(), start + 3, height - 5 - Toolkit.getText(ColorsAndFonts.TEXT_NORMAL).getDescent(),
            color, Toolkit.getText(ColorsAndFonts.TEXT_NORMAL));
    }

     private TimePeriod getPeriod() {
        ObjectContent content = ((ObjectContent) getContent());
        TimePeriod period = (TimePeriod) content.getObject().getObject();

        return period;
    }

    public Size getRequiredSize() {
		Size size = super.getRequiredSize();
		size.extendWidth(304);
        return size; 
    }
/*
    public void refresh() {
    }
    */
}

