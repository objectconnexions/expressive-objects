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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.text;

import java.util.StringTokenizer;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Alignment;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Text;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.VerticalAlignment;

public class TextUtils {

    private TextUtils() {
    }

    public static String limitText(final String xtext, final Text style, final int maxWidth) {
        String text = xtext;
        final int ellipsisWidth = style.stringWidth("...");
        if (maxWidth > 0 && style.stringWidth(text) > maxWidth) {
            int lastCharacterWithinAllowedWidth = 0;
            for (int textWidth = ellipsisWidth; textWidth <= maxWidth;) {
                final char character = text.charAt(lastCharacterWithinAllowedWidth);
                textWidth += style.charWidth(character);
                lastCharacterWithinAllowedWidth++;
            }

            int space = text.lastIndexOf(' ', lastCharacterWithinAllowedWidth - 1);
            if (space > 0) {
                while (space >= 0) {
                    final char character = text.charAt(space - 1);
                    if (Character.isLetterOrDigit(character)) {
                        break;
                    }
                    space--;
                }

                text = text.substring(0, space);
            } else {
                if (lastCharacterWithinAllowedWidth > 0) {
                    text = text.substring(0, lastCharacterWithinAllowedWidth - 1);
                } else {
                    text = "";
                }
            }
            text += "...";
        }
        return text;
    }
    
    /**
     * Returns the height in pixels when the specified text is wrapped at the
     * specified width.
     */
    public static int stringHeight(final String text, final Text style, final int maxWidth) {
        int noLines = 0;
        final StringTokenizer lines = new StringTokenizer(text, "\n\r");
        while (lines.hasMoreTokens()) {
            final String line = lines.nextToken();
            final StringTokenizer words = new StringTokenizer(line, " ");
            final StringBuffer l = new StringBuffer();
            int width = 0;
            while (words.hasMoreTokens()) {
                final String nextWord = words.nextToken();
                final int wordWidth = style.stringWidth(nextWord);
                width += wordWidth;
                if (width >= maxWidth) {
                    noLines++;
                    l.setLength(0);
                    width = wordWidth;
                }
                l.append(nextWord);
                l.append(" ");
                width += style.stringWidth(" ");
            }
            noLines++;
        }
        return noLines * style.getLineHeight();
    }

    public static int stringWidth(final String text, final Text style, final int maxWidth) {
        int width = 0;
        final StringTokenizer lines = new StringTokenizer(text, "\n\r");
        while (lines.hasMoreTokens()) {
            final String line = lines.nextToken();
            final StringTokenizer words = new StringTokenizer(line, " ");
            final StringBuffer l = new StringBuffer();
            int lineWidth = 0;
            while (words.hasMoreTokens()) {
                final String nextWord = words.nextToken();
                final int wordWidth = style.stringWidth(nextWord);
                lineWidth += wordWidth;
                if (lineWidth >= maxWidth) {
                    return maxWidth;
                }
                if (lineWidth > width) {
                    width = lineWidth;
                }
                l.append(nextWord);
                l.append(" ");
                lineWidth += style.stringWidth(" ");
            }
        }
        return width;
    }


    /**
     * Returns the horizontal position to draw text for the given alignment within the 
     * space between two vertical edges.
     * @param style 
     */
	public static int align(final String text, final Text style, final Alignment alignTo, final int leftEdge,
			final int height) {
		return alignTo.align(text, style, leftEdge, height);
	}

	/**
	 * Returns the baseline (vertical position) to draw text for the given alignment within
	 * the space between the two specified horizontal edges.
	 * @param titleStyle 
	 */
	public static int getBaseline(final Text style, final VerticalAlignment alignment, final int topEdge, 
			final int height) {
		return alignment.align(style, topEdge, height);
	}
}
