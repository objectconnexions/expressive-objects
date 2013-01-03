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

import uk.co.objectconnexions.expressiveobjects.object.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.extensions.dndviewer.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Click;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.InternalDrag;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Style;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ValueContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.basic.SimpleIdentifier;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.core.AbstractFieldSpecification;


public class PercentageBarField extends AbstractField {

    public static class Specification extends AbstractFieldSpecification {
        public View createView(Content content, ViewAxis axis) {
            return new SimpleIdentifier(new PercentageBarField(content, this, axis));
        }

        public String getName() {
            return "Percentage graph";
        }
        
	    public boolean canDisplay(ObjectAdapter object) {
	    	return object.getObject() instanceof Percentage;
		}
    }
    
    protected PercentageBarField(Content content, ViewSpecification specification, ViewAxis axis) {
        super(content, specification, axis);
    }

    private Percentage entry = new Percentage();
    
    public void drag(InternalDrag drag) {
        float x = drag.getLocation().getX() - 2;
        setValue(x);
    }

    private void setValue(float x) {
        float max = getSize().getWidth() - 4;
        
        if ((x >= 0) && (x <= max)) {
            entry.setValue(x / max);
            initiateSave();
        }
    }
    
    protected void save() {
 /*       try {
            saveValue(entry);
        } catch(InvalidEntryException e) {
            throw new NotImplementedException();
        }        
   */
        }

    public void draw(Canvas canvas) {
        super.draw(canvas);

        Color color = getState().isObjectIdentified() ? Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY2) : Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY1);
        Size size = getSize();
        int width = size.getWidth();
        int height = size.getHeight();
        canvas.drawRectangle(0, 0, width - 1, height - 1, color);

        Percentage p = getPercentage();
        int length = (int) ((width - 4) * p.floatValue());
        canvas.drawSolidRectangle(2, 2, length, height - 5, Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY3));
        canvas.drawRectangle(2, 2, length, height - 5, color);
        canvas.drawText(p.title().toString(), 6, height - 5 - Toolkit.getText(ColorsAndFonts.TEXT_NORMAL).getDescent(), color,
            Toolkit.getText(ColorsAndFonts.TEXT_NORMAL));
    }

    public void firstClick(Click click) {
        float x = click.getLocation().getX() - 2;
        setValue(x);        
    } 
    
    private Percentage getPercentage() {
        ValueContent content = ((ValueContent) getContent());
        Percentage percentage = (Percentage) content.getObject().getObject();

        return percentage;
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
