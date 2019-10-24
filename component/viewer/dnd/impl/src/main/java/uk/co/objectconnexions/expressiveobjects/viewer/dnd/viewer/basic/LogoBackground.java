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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.basic;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Background;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Image;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ImageFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.util.Properties;

public class LogoBackground implements Background {
    private static final Logger LOG = Logger.getLogger(LogoBackground.class);
    private static final String PARAMETER_BASE = Properties.PROPERTY_BASE + "logo-background.";
    private Location location;
    private Image logo;
    private Size logoSize;

    public LogoBackground() {
        final ExpressiveObjectsConfiguration configuration = ExpressiveObjectsContext.getConfiguration();

        final String fileName = configuration.getString(PARAMETER_BASE + "image", "background");
        logo = ImageFactory.getInstance().loadImage(fileName);

        if (logo == null) {
            logo = ImageFactory.getInstance().loadImage("poweredby-logo");
        }

        if (logo == null) {
            LOG.debug("logo image not found: " + fileName);
        } else {
            location = Properties.getLocation(PARAMETER_BASE + "location", new Location(-30, -30));
            logoSize = Properties.getSize(PARAMETER_BASE + "size", logo.getSize());
        }
    }

    @Override
    public void draw(final Canvas canvas, final Size viewSize) {
        if (logo != null) {
            int x;
            int y;

            if (location.getX() == 0 && location.getY() == 0) {
                x = viewSize.getWidth() / 2 - logoSize.getWidth() / 2;
                y = viewSize.getHeight() / 2 - logoSize.getHeight() / 2;
            } else {
                x = (location.getX() >= 0) ? location.getX() : viewSize.getWidth() + location.getX() - logoSize.getWidth();
                y = (location.getY() >= 0) ? location.getY() : viewSize.getHeight() + location.getY() - logoSize.getHeight();
            }
            canvas.drawImage(logo, x, y, logoSize.getWidth(), logoSize.getHeight());
        }
    }
}
