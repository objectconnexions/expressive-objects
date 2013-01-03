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

package uk.co.objectconnexions.expressiveobjects.viewer.html.servlet.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugString;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsApplicationException;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.util.Dump;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.AdapterManagerSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfile;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfilesDebugUtil;
import uk.co.objectconnexions.expressiveobjects.viewer.html.PathBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.Action;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.ActionException;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.ChangeContext;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.LogOut;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.Welcome;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.edit.AddItemToCollection;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.edit.EditObject;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.edit.RemoveItemFromCollection;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.edit.Save;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.misc.About;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.misc.SetUser;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.misc.SwapUser;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.view.CollectionView;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.view.FieldCollectionView;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.view.ObjectView;
import uk.co.objectconnexions.expressiveobjects.viewer.html.action.view.ServiceView;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.Block;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.Component;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.ComponentFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.DebugPane;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.Page;
import uk.co.objectconnexions.expressiveobjects.viewer.html.context.Context;
import uk.co.objectconnexions.expressiveobjects.viewer.html.context.ObjectLookupException;
import uk.co.objectconnexions.expressiveobjects.viewer.html.crumb.Crumb;
import uk.co.objectconnexions.expressiveobjects.viewer.html.image.ImageLookup;
import uk.co.objectconnexions.expressiveobjects.viewer.html.monitoring.servermonitor.Monitor;
import uk.co.objectconnexions.expressiveobjects.viewer.html.request.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.html.task.InvokeMethod;
import uk.co.objectconnexions.expressiveobjects.viewer.html.task.TaskLookupException;
import uk.co.objectconnexions.expressiveobjects.viewer.html.task.TaskStep;

public class WebController {

    private static final Logger LOG = Logger.getLogger(WebController.class);
    private static final Logger ACCESS_LOG = Logger.getLogger("access_log");

    private static final String ERROR_REASON = "This error occurs when you go back to a page " + "using the browsers back button.  To avoid this error in the future please avoid using the back button";

    protected class DebugView implements Action {
        public DebugView() {
        }

        @Override
        public void execute(final Request request, final Context context, final Page page) {
            page.setTitle("Debug");

            final DebugPane debugPane = context.getComponentFactory().createDebugPane();
            page.setDebug(debugPane);

            final DebugString debug = new DebugString();

            final AuthenticationSession authenticationSession = ExpressiveObjectsContext.getAuthenticationSession();
            debug.appendTitle("Session");
            if (authenticationSession != null) {
                debug.appendln("user", authenticationSession.getUserName());
                debug.appendln("roles", authenticationSession.getRoles());
            } else {
                debug.appendln("none");
            }

            final UserProfile userProfile = ExpressiveObjectsContext.getUserProfile();
            debug.appendTitle("User profile");
            if (userProfile != null) {
                UserProfilesDebugUtil.asDebuggableWithTitle(userProfile).debugData(debug);
            } else {
                debug.appendln("none");
            }

            debug.appendTitle("Actions");
            final Iterator e = actions.entrySet().iterator();
            debug.indent();
            while (e.hasNext()) {
                final Map.Entry element = (Map.Entry) e.next();
                debug.appendln(element.getKey() + " -> " + element.getValue());
            }
            debug.unindent();

            context.debug(debug);

            ImageLookup.debug(debug);

            debugPane.appendln("<pre>" + debug.toString() + "</pre>");
        }

        @Override
        public String name() {
            return "debug";
        }
    }

    protected class DebugSpecification implements Action {
        public DebugSpecification() {
        }

        @Override
        public void execute(final Request request, final Context context, final Page page) {
            final DebugPane debugPane = context.getComponentFactory().createDebugPane();
            page.setDebug(debugPane);

            debugPane.addSection("Specification");
            final ObjectAdapter object = context.getMappedObject(request.getObjectId());
            debugPane.appendln(Dump.specification(object));
        }

        @Override
        public String name() {
            return "spec";
        }
    }

    protected class DebugObject implements Action {
        public DebugObject() {
        }

        @Override
        public void execute(final Request request, final Context context, final Page page) {
            final DebugPane debugPane = context.getComponentFactory().createDebugPane();
            page.setDebug(debugPane);

            debugPane.addSection("Adapter");
            final ObjectAdapter object = context.getMappedObject(request.getObjectId());
            debugPane.appendln(Dump.adapter(object));
            debugPane.addSection("Graph");
            debugPane.appendln(Dump.graph(object, ExpressiveObjectsContext.getAuthenticationSession()));
        }

        @Override
        public String name() {
            return "dump";
        }
    }

    protected class DebugOn implements Action {
        private final WebController controller;

        public DebugOn(final WebController controller) {
            this.controller = controller;
        }

        @Override
        public void execute(final Request request, final Context context, final Page page) {
            controller.setDebug(true);
        }

        @Override
        public String name() {
            return "debugon";
        }
    }

    protected class DebugOff implements Action {
        private final WebController controller;

        public DebugOff(final WebController controller) {
            this.controller = controller;
        }

        @Override
        public void execute(final Request request, final Context context, final Page page) {
            controller.setDebug(false);
        }

        @Override
        public String name() {
            return "debugoff";
        }
    }

    private final Map actions = new HashMap();

    private boolean isDebug;
    private final PathBuilder pathBuilder;

    public WebController(final PathBuilder pathBuilder) {
        this.pathBuilder = pathBuilder;
    }

    public boolean actionExists(final Request req) {
        return actions.containsKey(req.getRequestType());
    }

    protected void addAction(final Action action) {
        actions.put(action.name(), action);
    }

    private void addCrumbs(final Context context, final Page page) {
        final Crumb[] crumbs = context.getCrumbs();
        final String[] names = new String[crumbs.length];
        final boolean[] isLinked = context.isLinked();

        for (int i = 0; i < crumbs.length; i++) {
            names[i] = crumbs[i].title();
        }

        final ComponentFactory factory = context.getComponentFactory();
        final Component breadCrumbs = factory.createBreadCrumbs(names, isLinked);

        page.setCrumbs(breadCrumbs);
    }

    public void addDebug(final Page page, final Request req) {
        page.addDebug("<a href=\"" + pathTo("debug") + "\">Debug</a>");
        final String id = req.getObjectId();
        if (id != null) {
            page.addDebug("<a href=\"" + pathTo("dump") + "?id=" + id + "\">Object</a>");
            page.addDebug("<a href=\"" + pathTo("spec") + "?id=" + id + "\">Spec</a>");
        }
        page.addDebug("<a href=\"" + pathTo("about") + "\">About</a>");
        page.addDebug("<a href=\"" + pathTo("debugoff") + "\">Debug off</a>");
    }

    public Page generatePage(final Context context, final Request request) {
        context.restoreAllObjectsToLoader();
        final Page page = context.getComponentFactory().createPage();
        pageHeader(context, page);
        final Block navigation = page.getNavigation();

        final Block optionBar = context.getComponentFactory().createBlock("options", null);
        optionBar.add(context.getComponentFactory().createHeading("Options"));

        Block block = context.getComponentFactory().createBlock("item", null);
        Component option = context.getComponentFactory().createLink("logout", "Log Out", "End the current session");
        block.add(option);
        optionBar.add(block);

        block = context.getComponentFactory().createBlock("item", null);
        option = context.getComponentFactory().createLink("about", "About", "Details about this application");
        block.add(option);
        optionBar.add(block);

        // boolean isExploring = SessionAccess.inExplorationMode();
        final boolean isExploring = ExpressiveObjectsContext.getDeploymentType().isExploring();
        if (isExploring) {
            block = context.getComponentFactory().createBlock("item", null);
            option = context.getComponentFactory().createLink("swapuser", "Swap User", "Swap the exploration user");
            block.add(option);
            optionBar.add(block);
        }

        navigation.add(optionBar);

        listServices(context, navigation);
        listHistory(context, navigation);
        Monitor.addEvent("Web", "Request " + request);
        runAction(context, request, page);
        addCrumbs(context, page);

        // The web viewer has no views of other objects, so changes can be
        // ignored
        if (ExpressiveObjectsContext.inSession() && ExpressiveObjectsContext.inTransaction()) {
            ExpressiveObjectsContext.getUpdateNotifier().clear();
        }
        // TODO deal with disposed objects

        return page;
    }

    public void init() {
        addAction(new About());
        addAction(new SwapUser());
        addAction(new SetUser());
        addAction(new DebugView());
        addAction(new DebugSpecification());
        addAction(new DebugObject());
        addAction(new Welcome());
        addAction(new ObjectView());
        addAction(new CollectionView());
        addAction(new FieldCollectionView());
        addAction(new InvokeMethod());
        addAction(new TaskStep());
        addAction(new EditObject());
        addAction(new Save());
        addAction(new ServiceView());
        addAction(new LogOut());
        addAction(new RemoveItemFromCollection());
        addAction(new AddItemToCollection());
        addAction(new ChangeContext());

        // TODO allow these to be exclude by configuration so they cannot be run
        // in a real system
        addAction(new DebugOn(this));
        addAction(new DebugOff(this));

        Logger.getLogger(this.getClass()).info("init");
    }

    public boolean isDebug() {
        return isDebug;
    }

    private void listHistory(final Context context, final Block navigation) {
        context.listHistory(context, navigation);
    }

    private void listServices(final Context context, final Block navigationBar) {
        final Block taskBar = context.getComponentFactory().createBlock("services", null);
        taskBar.add(context.getComponentFactory().createHeading("Services"));
        final AdapterManager adapterManager = ExpressiveObjectsContext.getPersistenceSession().getAdapterManager();
        final List<Object> services = getUserProfile().getPerspective().getServices();
        for (final Object service : services) {
            final ObjectAdapter serviceAdapter = adapterManager.adapterFor(service);
            if (serviceAdapter == null) {
                LOG.warn("unable to find service Id: " + service + "; skipping");
                continue;
            }
            if (isHidden(serviceAdapter)) {
                continue;
            }
            final String serviceMapId = context.mapObject(serviceAdapter);
            taskBar.add(createServiceComponent(context, serviceMapId, serviceAdapter));
        }
        navigationBar.add(taskBar);
    }

    private Component createServiceComponent(final Context context, final String serviceMapId, final ObjectAdapter serviceNO) {
        final String serviceName = serviceNO.titleString();
        final String serviceIcon = serviceNO.getIconName();
        return context.getComponentFactory().createService(serviceMapId, serviceName, serviceIcon);
    }

    private boolean isHidden(final ObjectAdapter serviceNO) {
        final ObjectSpecification serviceNoSpec = serviceNO.getSpecification();
        final boolean isHidden = serviceNoSpec.isHidden();
        return isHidden;
    }

    private void pageHeader(final Context context, final Page page) {
        page.getPageHeader().add(context.getComponentFactory().createInlineBlock("none", "", null));
    }

    private void runAction(final Context context, final Request request, final Page page) {
        try {
            ACCESS_LOG.info("request " + request.toString());
            Request r = request;
            final DebugString debug = new DebugString();
            debug.startSection("Request");
            debug.appendln("http", request.toString());
            debug.endSection();
            do {
                final Action action = (Action) actions.get(r.getRequestType());
                try {
                    action.execute(r, context, page);
                } catch (final ObjectLookupException e) {
                    final String error = "The object/service you selected has timed out.  Please navigate to the object via the history bar.";
                    displayError(context, page, error);
                } catch (final TaskLookupException e) {
                    final String error = "The task you went back to has already been completed or cancelled.  Please start the task again.";
                    displayError(context, page, error);
                }
                r = r.getForward();
                if (r != null) {
                    LOG.debug("forward to " + r);
                }
            } while (r != null);
            if (LOG.isDebugEnabled()) {
                context.debug(debug);
                debug.appendln();
                if (ExpressiveObjectsContext.inSession()) {
                    ExpressiveObjectsContext.getSession(getExecutionContextId()).debugAll(debug);
                } else {
                    debug.appendln("No session");
                }
                LOG.debug(debug.toString());
            }
        } catch (final ActionException e) {
            page.setTitle("Error");
            page.getViewPane().setTitle("Error", "Action Exception");
            LOG.error("ActionException, executing action " + request.getRequestType(), e);
            page.getViewPane().add(context.getComponentFactory().createErrorMessage(e, isDebug));
        } catch (final ExpressiveObjectsApplicationException e) {
            page.setTitle("Error");
            page.getViewPane().setTitle("Error", "Application Exception");
            LOG.error("ApplicationException, executing action " + request.getRequestType(), e);
            page.getViewPane().add(context.getComponentFactory().createErrorMessage(e, isDebug));
        } catch (final ExpressiveObjectsException e) {
            page.setTitle("Error");
            page.getViewPane().setTitle("Error", "System Exception");
            LOG.error("ObjectAdapterRuntimeException, executing action " + request.getRequestType(), e);
            page.getViewPane().add(context.getComponentFactory().createErrorMessage(e, true));
        } catch (final RuntimeException e) {
            page.setTitle("Error");
            page.getViewPane().setTitle("Error", "System Exception");
            LOG.error("RuntimeException, executing action " + request.getRequestType(), e);
            page.getViewPane().add(context.getComponentFactory().createErrorMessage(e, true));
        }
    }

    private void displayError(final Context context, final Page page, final String errorMessage) {
        page.setTitle("Error");
        page.getViewPane().setTitle("Error", "");

        final Block block1 = context.getComponentFactory().createBlock("error", "");
        block1.add(context.getComponentFactory().createInlineBlock("", errorMessage, ""));
        page.getViewPane().add(block1);

        final Block block2 = context.getComponentFactory().createBlock("text", "");
        block2.add(context.getComponentFactory().createInlineBlock("", ERROR_REASON, ""));
        page.getViewPane().add(block2);
    }

    public void setDebug(final boolean on) {
        this.isDebug = on;
    }

    protected String pathTo(final String prefix) {
        return pathBuilder.pathTo(prefix);
    }

    // ///////////////////////////////////////////////////////
    // Dependencies (from singleton)
    // ///////////////////////////////////////////////////////

    private UserProfile getUserProfile() {
        return ExpressiveObjectsContext.getUserProfile();
    }

    private String getExecutionContextId() {
        return ExpressiveObjectsContext.getSessionId();
    }
}
