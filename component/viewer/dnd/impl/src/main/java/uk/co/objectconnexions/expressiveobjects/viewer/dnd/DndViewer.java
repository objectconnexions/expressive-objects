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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd;

import java.awt.Dimension;
import java.util.StringTokenizer;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationException;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.commons.factory.InstanceCreationException;
import uk.co.objectconnexions.expressiveobjects.core.commons.factory.InstanceUtil;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequest;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.exploration.AuthenticationRequestExploration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.fixtures.authentication.AuthenticationRequestLogonFixture;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfile;
import uk.co.objectconnexions.expressiveobjects.core.runtime.viewer.ExpressiveObjectsViewerAbstract;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.awt.AwtImageFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.awt.AwtToolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.awt.LoginDialog;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.awt.ViewerFrame;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.awt.XViewer;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.calendar.CalendarSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.combined.ExpandableListSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.combined.FormWithTableSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.combined.TwoPartViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.configurable.ConfigurableObjectViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.configurable.GridListSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.configurable.NewViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.configurable.PanelViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Bounds;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.field.CheckboxField;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.field.ColorField;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.field.DateFieldSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.field.EmptyField;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.field.FieldOfSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.field.ImageField;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.field.PasswordFieldSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.field.TextFieldSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.form.ExpandableFormSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.form.FormSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.form.FormWithDetailSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.form.InternalFormSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.form.SimpleFormSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.form.SummaryFormSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.grid.GridSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.help.HelpViewer;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.help.InternalHelpViewer;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.histogram.HistogramSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.icon.LargeIconSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.icon.RootIconSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.icon.SubviewIconSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.list.InternalListSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.list.SimpleListSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.service.PerspectiveContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.service.ServiceIconSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.table.WindowTableSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.tree.ListWithDetailSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.tree.TreeSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.tree.TreeWithDetailSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.tree2.CollectionTreeNodeSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.tree2.TreeNodeSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.util.Properties;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ShutdownListener;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewUpdateNotifier;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.ViewUpdateNotifierImpl;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.message.DetailedMessageViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.message.MessageDialogSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.SkylarkViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.basic.DragContentSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.basic.InnerWorkspaceSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.basic.RootWorkspaceSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.basic.WrappedTextFieldSpecification;
import org.apache.log4j.Logger;

public class DndViewer extends ExpressiveObjectsViewerAbstract {

    private static final Logger LOG = Logger.getLogger(DndViewer.class);
    private static final String SPECIFICATION_BASE = Properties.PROPERTY_BASE + "specification.";
    private ViewUpdateNotifier updateNotifier;
    private ViewerFrame frame;
    private XViewer viewer;
    private ShutdownListener shutdownListener;
    private Bounds bounds;
    private HelpViewer helpViewer;
    private boolean acceptingLogIns = true;

    // ////////////////////////////////////
    // shutdown
    // ////////////////////////////////////

    @Override
    public void shutdown() {
        System.exit(0);
    }

    private Bounds calculateInitialWindowSize(final Dimension screenSize) {
        int maxWidth = screenSize.width;
        final int maxHeight = screenSize.height;

        if ((screenSize.width / screenSize.height) >= 2) {
            final int f = screenSize.width / screenSize.height;
            maxWidth = screenSize.width / f;
        }

        final int width = maxWidth - 80;
        final int height = maxHeight - 80;
        final int x = 100;
        final int y = 100;

        final Size defaultWindowSize = new Size(width, height);
        defaultWindowSize.limitWidth(800);
        defaultWindowSize.limitHeight(600);

        final Size size = Properties.getSize(Properties.PROPERTY_BASE + "initial.size", defaultWindowSize);
        final Location location = Properties.getLocation(Properties.PROPERTY_BASE + "initial.location", new Location(x, y));
        return new Bounds(location, size);
    }

    private ViewSpecification loadSpecification(final String name, final Class<?> cls) {
        final String factoryName = ExpressiveObjectsContext.getConfiguration().getString(SPECIFICATION_BASE + name);
        ViewSpecification spec;
        if (factoryName != null) {
            spec = InstanceUtil.createInstance(factoryName, ViewSpecification.class);
        } else {
            spec = InstanceUtil.createInstance(cls.getName(), ViewSpecification.class);
        }
        return spec;
    }

    private synchronized void logOut() {
        LOG.info("user log out");
        saveDesktop();
        final AuthenticationSession session = ExpressiveObjectsContext.getAuthenticationSession();
        getAuthenticationManager().closeSession(session);
        viewer.close();
        notify();
    }

    private void saveDesktop() {
        if (!ExpressiveObjectsContext.inSession()) {
            // can't do anything
            return;
        }
        viewer.saveOpenObjects();
    }

    protected void quit() {
        LOG.info("user quit");
        saveDesktop();
        acceptingLogIns = false;
        shutdown();
    }

    @Override
    public synchronized void init() {
        super.init();

        new AwtImageFactory(ExpressiveObjectsContext.getTemplateImageLoader());
        new AwtToolkit();

        setShutdownListener(new ShutdownListener() {
            @Override
            public void logOut() {
                DndViewer.this.logOut();
            }

            @Override
            public void quit() {
                DndViewer.this.quit();
            }
        });

        updateNotifier = new ViewUpdateNotifierImpl();

        if (updateNotifier == null) {
            throw new NullPointerException("No update notifier set for " + this);
        }
        if (shutdownListener == null) {
            throw new NullPointerException("No shutdown listener set for " + this);
        }

        while (acceptingLogIns) {
            if (login()) {
                openViewer();
                try {
                    wait();
                } catch (final InterruptedException e) {
                }
            } else {
                quit();
            }
        }
    }

    // ////////////////////////////////////
    // login
    // ////////////////////////////////////

    // TODO: nasty
    private boolean loggedInUsingLogonFixture = false;

    /**
     * TODO: there is similar code in
     * <tt>AuthenticationSessionLookupStrategyDefault</tt>; should try to
     * combine somehow...
     */
    private boolean login() {
        final AuthenticationRequest request = determineRequestIfPossible();

        // we may have enough to get a session
        AuthenticationSession session = getAuthenticationManager().authenticate(request);
        clearAuthenticationRequestViaArgs();

        if (session == null) {
            session = loginDialogPrompt(request);
        }
        if (session == null) {
            return false;
        } else {
            ExpressiveObjectsContext.openSession(session);
            return true;
        }
    }

    private AuthenticationSession loginDialogPrompt(final AuthenticationRequest request) {
        AuthenticationSession session;
        final LoginDialog dialog = new LoginDialog(getAuthenticationManager());
        if (request != null) {
            dialog.setUserName(request.getName());
        }
        dialog.setVisible(true);
        dialog.toFront();
        dialog.login();
        dialog.setVisible(false);
        dialog.dispose();
        session = dialog.getSession();
        return session;
    }

    private AuthenticationRequest determineRequestIfPossible() {

        // command line args
        AuthenticationRequest request = getAuthenticationRequestViaArgs();
        ;

        // exploration & (optionally) logon fixture provided
        if (request == null) {
            if (getDeploymentType().isExploring()) {
                request = new AuthenticationRequestExploration(getLogonFixture());
            }
        }

        // logon fixture provided
        if (request == null) {
            if (getLogonFixture() != null && !loggedInUsingLogonFixture) {
                loggedInUsingLogonFixture = true;
                request = new AuthenticationRequestLogonFixture(getLogonFixture());
            }
        }
        return request;
    }

    private void openViewer() {
        frame = new ViewerFrame();

        if (bounds == null) {
            bounds = calculateInitialWindowSize(frame.getToolkit().getScreenSize());
        }

        frame.pack(); // forces insets to be calculated, hence need to then set
                      // bounds
        frame.setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());

        viewer = (XViewer) Toolkit.getViewer();
        viewer.setRenderingArea(frame);
        viewer.setUpdateNotifier(updateNotifier);
        viewer.setListener(shutdownListener);
        viewer.setExploration(isInExplorationMode());
        viewer.setPrototype(isInPrototypeMode());

        if (helpViewer == null) {
            helpViewer = new InternalHelpViewer(viewer);
        }
        viewer.setHelpViewer(helpViewer);

        frame.setViewer(viewer);

        final AuthenticationSession currentSession = ExpressiveObjectsContext.getAuthenticationSession();
        if (currentSession == null) {
            throw new NullPointerException("No session for " + this);
        }

        setupViewFactory();

        final UserProfile userProfiler = ExpressiveObjectsContext.getUserProfile();

        // TODO viewer should be shown during init() (so login can take place on
        // main window, and can quit
        // before
        // logging in), and should be updated during start to show context.

        // TODO resolving should be done by the views?
        // resolveApplicationContextCollection(rootObject, "services");
        // resolveApplicationContextCollection(rootObject, "objects");
        final RootWorkspaceSpecification spec = new RootWorkspaceSpecification();
        final PerspectiveContent content = new PerspectiveContent(userProfiler.getPerspective());
        if (spec.canDisplay(new ViewRequirement(content, ViewRequirement.CLOSED))) {
            // View view = spec.createView(new RootObject(rootObject), null);
            final View view = spec.createView(content, new Axes(), -1);
            viewer.setRootView(view);
        } else {
            throw new ExpressiveObjectsException();
        }

        viewer.init();

        final String name = userProfiler.getPerspective().getName();
        frame.setTitle(name);
        frame.init();

        viewer.initSize();
        viewer.scheduleRepaint();

        frame.setVisible(true);
        frame.toFront();
    }

    private boolean isInExplorationMode() {
        return getDeploymentType().isExploring();
    }

    private boolean isInPrototypeMode() {
        return getDeploymentType().isPrototyping();
    }

    public void setBounds(final Bounds bounds) {
        this.bounds = bounds;
    }

    public void setHelpViewer(final HelpViewer helpViewer) {
        this.helpViewer = helpViewer;
    }

    public void setShutdownListener(final ShutdownListener shutdownListener) {
        this.shutdownListener = shutdownListener;
    }

    private void setupViewFactory() throws ExpressiveObjectsConfigurationException, InstanceCreationException {
        final SkylarkViewFactory viewFactory = (SkylarkViewFactory) Toolkit.getViewFactory();

        LOG.debug("setting up default views (provided by the framework)");

        /*
         * viewFactory.addValueFieldSpecification(loadSpecification("field.option"
         * , OptionSelectionField.Specification.class));
         * viewFactory.addValueFieldSpecification
         * (loadSpecification("field.percentage",
         * PercentageBarField.Specification.class));
         * viewFactory.addValueFieldSpecification
         * (loadSpecification("field.timeperiod",
         * TimePeriodBarField.Specification.class));
         */
        viewFactory.addSpecification(loadSpecification("field.image", ImageField.Specification.class));
        viewFactory.addSpecification(loadSpecification("field.color", ColorField.Specification.class));
        viewFactory.addSpecification(loadSpecification("field.password", PasswordFieldSpecification.class));
        viewFactory.addSpecification(loadSpecification("field.wrappedtext", WrappedTextFieldSpecification.class));
        viewFactory.addSpecification(loadSpecification("field.checkbox", CheckboxField.Specification.class));
        viewFactory.addSpecification(loadSpecification("field.date", DateFieldSpecification.class));
        viewFactory.addSpecification(loadSpecification("field.text", TextFieldSpecification.class));
        viewFactory.addSpecification(new RootWorkspaceSpecification());
        viewFactory.addSpecification(new InnerWorkspaceSpecification());

        if (ExpressiveObjectsContext.getConfiguration().getBoolean(SPECIFICATION_BASE + "defaults", true)) {
            viewFactory.addSpecification(new FieldOfSpecification());

            viewFactory.addSpecification(new InternalListSpecification());
            viewFactory.addSpecification(new SimpleListSpecification());
            viewFactory.addSpecification(new GridSpecification());
            // TBA viewFactory.addSpecification(new
            // ListWithExpandableElementsSpecification());
            // TBA
            viewFactory.addSpecification(new CalendarSpecification());
            viewFactory.addSpecification(new ListWithDetailSpecification());
            viewFactory.addSpecification(new HistogramSpecification());

            viewFactory.addSpecification(new TreeWithDetailSpecification());
            viewFactory.addSpecification(new FormSpecification());
            viewFactory.addSpecification(new FormWithTableSpecification());
            viewFactory.addSpecification(new WindowTableSpecification());
            // TBA
            viewFactory.addSpecification(new ExpandableFormSpecification());
            viewFactory.addSpecification(new InternalFormSpecification());
            viewFactory.addSpecification(new TwoPartViewSpecification());
            // TBA
            viewFactory.addSpecification(new FormWithDetailSpecification());

            viewFactory.addSpecification(new SummaryFormSpecification());
            viewFactory.addSpecification(new SimpleFormSpecification());

            viewFactory.addSpecification(new TreeSpecification());
            // TODO allow window form to be used for objects with limited number
            // of collections
            // viewFactory.addSpecification(new TreeWithDetailSpecification(0,
            // 3));

            viewFactory.addDesignSpecification(new GridListSpecification());
            viewFactory.addDesignSpecification(new ConfigurableObjectViewSpecification());
            viewFactory.addDesignSpecification(new PanelViewSpecification());
            viewFactory.addDesignSpecification(new NewViewSpecification());
        }

        viewFactory.addSpecification(new MessageDialogSpecification());
        viewFactory.addSpecification(new DetailedMessageViewSpecification());

        viewFactory.addEmptyFieldSpecification(loadSpecification("field.empty", EmptyField.Specification.class));

        viewFactory.addSpecification(loadSpecification("icon.object", RootIconSpecification.class));
        viewFactory.addSpecification(loadSpecification("icon.subview", SubviewIconSpecification.class));
        viewFactory.addSpecification(loadSpecification("icon.collection", ExpandableListSpecification.class));
        viewFactory.addSpecification(new LargeIconSpecification());

        viewFactory.addSpecification(loadSpecification("icon.service", ServiceIconSpecification.class));
        viewFactory.setDragContentSpecification(loadSpecification("drag-content", DragContentSpecification.class));

        // TODO remove or move to better position
        final ViewSpecification[] specifications = CollectionTreeNodeSpecification.create();
        viewFactory.addSpecification(specifications[0]);
        viewFactory.addSpecification(specifications[1]);
        viewFactory.addSpecification(new TreeNodeSpecification());

        installSpecsFromConfiguration(viewFactory);

        viewFactory.loadUserViewSpecifications();
    }

    private void installSpecsFromConfiguration(final SkylarkViewFactory viewFactory) {
        final String viewParams = ExpressiveObjectsContext.getConfiguration().getString(SPECIFICATION_BASE + "view");
        if (viewParams != null) {
            final StringTokenizer st = new StringTokenizer(viewParams, ",");
            while (st.hasMoreTokens()) {
                final String specName = st.nextToken().trim();
                if (specName != null && !specName.trim().equals("")) {
                    viewFactory.addSpecification(specName);
                }
            }
        }
    }

}
