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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.PrintJob;
import java.awt.Toolkit;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Allow;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.option.UserActionAbstract;

public class PrintOption extends UserActionAbstract {
    private final int HEIGHT = 60;
    private final int LEFT = 60;

    public PrintOption() {
        super("Print...");
    }

    @Override
    public Consent disabled(final View component) {
        return Allow.DEFAULT;
    }

    @Override
    public void execute(final Workspace workspace, final View view, final Location at) {
        final Frame frame = new Frame();
        final PrintJob job = Toolkit.getDefaultToolkit().getPrintJob(frame, "Print object", null);

        if (job != null) {
            final Graphics pg = job.getGraphics();
            final Dimension pageSize = job.getPageDimension();

            if (pg != null) {
                pg.translate(LEFT, HEIGHT);
                pg.drawRect(0, 0, pageSize.width - LEFT - 1, pageSize.height - HEIGHT - 1);
                view.print(new PrintCanvas(pg, view));
                pg.dispose();
            }

            job.end();
        }
        frame.dispose();
    }

    private class PrintCanvas extends AwtCanvas {
        PrintCanvas(final Graphics g, final View view) {
            super(g, null, 0, 0, view.getSize().getWidth(), view.getSize().getHeight());
        }
    }
}
