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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.awt;

import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Text;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.util.Properties;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewConstants;
import org.apache.log4j.Logger;

public class AwtColorsAndFonts implements ColorsAndFonts {
	private static final Logger LOG = Logger.getLogger(AwtColorsAndFonts.class);
    private final Map<String, AwtColor> defaultColors = new HashMap<String, AwtColor>();
    private final Map<String, AwtColor> colors = new HashMap<String, AwtColor>();
    private int defaultBaseline;
    private int defaultFieldHeight;
    private final Map<String, AwtText> fonts = new HashMap<String, AwtText>();
	private Object antiAliasing;

    @Override
    public int defaultBaseline() {
        if (defaultBaseline == 0) {
            final int iconSize = getText(TEXT_NORMAL).getAscent();
            defaultBaseline = ViewConstants.VPADDING + iconSize;
        }
        return defaultBaseline;
    }

    @Override
    public int defaultFieldHeight() {
        if (defaultFieldHeight == 0) {
            defaultFieldHeight = getText(TEXT_NORMAL).getTextHeight();
        }
        return defaultFieldHeight;
    }

    @Override
    public Color getColor(final String name) {
    	Color color = colors.get(name);
        if (color == null) {
        	if (name.startsWith(COLOR_WINDOW + ".")) {
        			String nextName = name.substring(0, name.lastIndexOf('.'));
        			AwtColor defaultColor = defaultColors.get(nextName);
                    final AwtColor c = new AwtColor(name, defaultColor);
        			colors.put(name, (AwtColor) c);
					return c;
        	}
        	
        	AwtColor defaultColor = defaultColors.get(name);
            final AwtColor c = new AwtColor(name, defaultColor);
            colors.put(name, c);
            LOG.info("adding color " + c);
            return c;
        } else {
        	return color;
        }
/*
        
        
        
        
    	if (color == null && name.startsWith(COLOR_WINDOW + ".")) {
//                    AwtColor awtColor = new AwtColor(name, (AwtColor) getColor(COLOR_WINDOW));
     //               colors.put(name, awtColor);
            		String nextName = name.substring(0, name.lastIndexOf('.'));
            		color = colors.get(nextName);
            		if (color == null) {
            			throw new ExpressiveObjectsException("No such color: " + name);
            			//AwtColor awtColor = new AwtColor(name, (AwtColor) getColor(COLOR_WINDOW));
                	//colors.put(name, awtColor);
                	//color = awtColor;
            		}
            	} else { 
            		color = new AwtColor(name, defaultColor);
            	}
            	

        final AwtColor color = new AwtColor(name, defaultColor);
        colors.put(name, color);
        LOG.info("adding wndow color " + color);
    }
        		
    	
        		
        		
        return color;
        */
    }

    @Override
    public Color getColor(final int rgbColor) {
        return new AwtColor(rgbColor);
    }

    @Override
    public Text getText(final String name) {
        return (Text) fonts.get(name);
    }

    @Override
    public Object getAntiAliasing() {
		return antiAliasing;
    }
    
    @Override
    public void init() {
        // core color scheme
        setColor(COLOR_BLACK, "#000000");
        setColor(COLOR_WHITE, "#ffffff");
        setColor(COLOR_PRIMARY1, "#666699");
        setColor(COLOR_PRIMARY2, "#9999cc");
        setColor(COLOR_PRIMARY3, "#ccccff");
        setColor(COLOR_SECONDARY1, "#666666");
        setColor(COLOR_SECONDARY2, "#999999");
        setColor(COLOR_SECONDARY3, "#cccccc");

        // background colors
        setColor(COLOR_APPLICATION, "#e0e0e0");
        setColor(COLOR_WINDOW, getColor(COLOR_WHITE));
        setColor(COLOR_MENU_VALUE, getColor(COLOR_PRIMARY3)); // "#ccffcc");
        setColor(COLOR_MENU_CONTENT, getColor(COLOR_PRIMARY3)); // "#ffcccc");
        setColor(COLOR_MENU_VIEW, getColor(COLOR_SECONDARY3)); // "#ffccff");
        setColor(COLOR_MENU_WORKSPACE, getColor(COLOR_SECONDARY3)); // "#cccccc");

        // menu colors
        setColor(COLOR_MENU, getColor(COLOR_BLACK));
        setColor(COLOR_MENU_DISABLED, getColor(COLOR_SECONDARY1));
        setColor(COLOR_MENU_REVERSED, getColor(COLOR_WHITE));

        // label colors
        setColor(COLOR_LABEL_MANDATORY, getColor(COLOR_BLACK));
        setColor(COLOR_LABEL, getColor(COLOR_SECONDARY1));
        setColor(COLOR_LABEL_DISABLED, getColor(COLOR_SECONDARY2));

        // state colors
        setColor(COLOR_IDENTIFIED, getColor(COLOR_PRIMARY1)); // "#0099ff");
        setColor(COLOR_VALID, "#32CD32");
        setColor(COLOR_INVALID, "#ee1919");
        setColor(COLOR_ERROR, "#ee1919");
        setColor(COLOR_ACTIVE, "#ff0000");
        setColor(COLOR_OUT_OF_SYNC, "#662200");

        // text colors
        setColor(COLOR_TEXT_SAVED, getColor(COLOR_SECONDARY1));
        setColor(COLOR_TEXT_EDIT, getColor(COLOR_PRIMARY1)); // "#009933");
        setColor(COLOR_TEXT_CURSOR, getColor(COLOR_PRIMARY1));
        setColor(COLOR_TEXT_HIGHLIGHT, getColor(COLOR_PRIMARY3));

        // debug outline colors
        setColor(COLOR_DEBUG_BASELINE, AwtColor.DEBUG_BASELINE);
        setColor(COLOR_DEBUG_BOUNDS_BORDER, AwtColor.DEBUG_BORDER_BOUNDS);
        setColor(COLOR_DEBUG_BOUNDS_DRAW, AwtColor.DEBUG_DRAW_BOUNDS);
        setColor(COLOR_DEBUG_BOUNDS_REPAINT, AwtColor.DEBUG_REPAINT_BOUNDS);
        setColor(COLOR_DEBUG_BOUNDS_VIEW, AwtColor.DEBUG_VIEW_BOUNDS);

        // fonts
        final String defaultFontFamily = AwtText.defaultFontFamily();
        final int defaultFontSizeSmall = AwtText.defaultFontSizeSmall();
        final int defaultFontSizeMedium = AwtText.defaultFontSizeMedium();
        final int defaultFontSizeLarge = AwtText.defaultFontSizeLarge();

        setText(TEXT_TITLE, defaultFontFamily + "-bold-" + defaultFontSizeLarge);
        setText(TEXT_TITLE_SMALL, defaultFontFamily + "-bold-" + defaultFontSizeMedium);
        setText(TEXT_NORMAL, defaultFontFamily + "-plain-" + defaultFontSizeMedium);
        setText(TEXT_CONTROL, defaultFontFamily + "-bold-" + defaultFontSizeMedium);
        setText(TEXT_STATUS, defaultFontFamily + "--" + defaultFontSizeMedium);
        setText(TEXT_ICON, defaultFontFamily + "-bold-" + defaultFontSizeMedium);
        setText(TEXT_MENU, defaultFontFamily + "--" + defaultFontSizeMedium);

        setText(TEXT_LABEL_MANDATORY, defaultFontFamily + "--" + defaultFontSizeSmall);
        setText(TEXT_LABEL_DISABLED, defaultFontFamily + "--" + defaultFontSizeSmall);
        setText(TEXT_LABEL, defaultFontFamily + "--" + defaultFontSizeSmall);
        
        String antiAliasProperty = ExpressiveObjectsContext.getConfiguration().getString( Properties.PROPERTY_BASE + "font.anti-alias", "DEFAULT").toLowerCase();
        if (antiAliasProperty.equals("on")) {
        	antiAliasing = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
        } else if (antiAliasProperty.equals("off")) {
        	antiAliasing = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
        } else if (antiAliasProperty.equals("default")) {
        	antiAliasing = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
        } else if (antiAliasProperty.equals("gasp")) {
        	antiAliasing = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
        } else if (antiAliasProperty.equals("hbgr")) {
        	antiAliasing = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
        } else if (antiAliasProperty.equals("hrgb")) {
        	antiAliasing = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
        } else if (antiAliasProperty.equals("vbgr")) {
        	antiAliasing = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
        } else if (antiAliasProperty.equals("vrgb")) {
        	antiAliasing = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
        } else {
        	LOG.warn("Unrecognised setting for anti-aliasing (" + antiAliasProperty + ") using default");
        	antiAliasing = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
        }
    }

    private void setColor(final String name, final Color defaultColor) {
        setColor(name, (AwtColor) defaultColor);
    }

    private void setColor(final String name, final AwtColor defaultColor) {
    //    if (getColor(name) == null) {
            final AwtColor color = new AwtColor(name, defaultColor);
            defaultColors.put(name, color);
    //        LOG.info("adding color " + color);
  //      }
    }

    private void setColor(final String name, final String defaultColor) {
   //     if (getColor(name) == null) {
            final AwtColor color = new AwtColor(name, defaultColor);
            defaultColors.put(name, color);
  //      }
    }

    private void setText(final String name, final String defaultFont) {
        if (getText(name) == null) {
            final AwtText font = new AwtText(name, defaultFont);
            fonts.put(name, font);
        }
    }
}
