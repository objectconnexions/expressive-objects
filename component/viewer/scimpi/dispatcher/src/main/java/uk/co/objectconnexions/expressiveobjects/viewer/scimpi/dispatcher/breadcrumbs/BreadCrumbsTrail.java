package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.breadcrumbs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ToString;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.Names;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ScimpiException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;


public class BreadCrumbsTrail extends AbstractElementProcessor {

    public String getName() {
        return "breadcrumbs";
    }

    public void process(Request request) {
        String action = request.getOptionalProperty("action", "add");
        String breadcrumbsId = request.getOptionalProperty("id", "1");
    
        String variable = "_breadcrumbs_" + breadcrumbsId;
        BreadCrumbsCollection crumbs = (BreadCrumbsCollection) request.getContext().getVariable(variable);
        if (crumbs == null) {
            String factoryClassName = request.getOptionalProperty("factory", SimpleCrumbFactory.class.getName());

            BreadCrumbFactory crumbFactory;
            try {
                Class<?> cls = null;
                cls = Class.forName(factoryClassName);
                crumbFactory = (BreadCrumbFactory) cls.newInstance();
            } catch (final ClassNotFoundException e) {
                throw new ScimpiException("No class for " + factoryClassName, e);
            } catch (InstantiationException e) {
                throw new ScimpiException("Failed to instantiate an instance of " + factoryClassName, e);
            } catch (IllegalAccessException e) {
                throw new ScimpiException("No access contstructor for  " + factoryClassName, e);
            }
            
            crumbs = new BreadCrumbsCollection(crumbFactory);
            request.getContext().addVariable(variable, crumbs, Scope.SESSION);
        }
        
        if (action.equals("add")) {
            crumbs.resetTo(request);
            String elementClass = request.getOptionalProperty("class", "scimpi-breadcrumbs");
            request.appendHtml("<p class=\"" + elementClass + "\">" + crumbs.display() + "</p>");
        } else if (action.equals("parent")) {
            BreadCrumb crumb = crumbs.parent();
            if (crumb != null) {
                request.getContext().addVariable("_parent-path", crumb.url, Scope.REQUEST);
                request.getContext().addVariable("_parent-id", crumb.id, Scope.REQUEST);
                request.getContext().addVariable("_parent-title", crumb.title, Scope.REQUEST);
            }
        } else if (action.equals("clear")) {
            crumbs.clear();
        } else {
            throw new ScimpiException("No such action: " + action);
        }
    }
}

class BreadCrumbsCollection implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final BreadCrumbFactory crumbFactory;
    private final List<BreadCrumb> crumbs = new ArrayList<BreadCrumb>();
    
    public BreadCrumbsCollection(BreadCrumbFactory crumbFactory) {
        this.crumbFactory = crumbFactory;
    }
    
    public BreadCrumb parent() {
        int index = crumbs.size() - 2;
        return index < 0 ? null : crumbs.get(index);
    }

    public String display() {
        StringBuffer html = new StringBuffer();
        int noCrumbs = crumbs.size();
        for (int i = 0; i < noCrumbs; i++) {
            BreadCrumb crumb = crumbs.get(i);
            if (i == 0) {
                html.append(crumb.displayFirst());
            } else if (i + 1 == noCrumbs) {
                    html.append(crumb.displayLast());
            } else {
                html.append(crumb.displayNormal());
            }
        }
        return html.toString();
    }

    public void clear() {
        crumbs.clear();
    }

    public void resetTo(Request request) {
        String id = crumbFactory.getId(request);
        int index = findSameElement(id);
        if (index == -1) {
            BreadCrumb crumb = crumbFactory.createCrumb(request, id);
            crumbs.add(crumb);
        } else {
            removeElementsAfter(index);
        }
    }
 
    private void removeElementsAfter(int index) {
        for (int i = crumbs.size() - 1; i > index; i--) {
            crumbs.remove(i);
        }
    }

    private int findSameElement(String id) {
        if (id != null) {
            int splitAt = id.indexOf('^');
            int end = splitAt == -1 ? id.length() : splitAt + 1;
            String id4 = id.substring(0, end);
            for (int index = 0; index < crumbs.size(); index++) {
                String id2 = (String) crumbs.get(index).getId();
                if (id2 == null || id2.startsWith(id4)) {
                    return index;
                }
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
        ToString toString = new ToString(this);
        for (BreadCrumb crumb : crumbs) {
            toString.append(crumb.toString());
            toString.append(",");
        }
        return "{" + toString + "}";
    }
}

class SimpleCrumbFactory implements BreadCrumbFactory, Serializable {
    private static final long serialVersionUID = 1L;

    public String getId(Request request) {
        return request.getOptionalProperty(Names.OBJECT);
    }

    public BreadCrumb createCrumb(Request request, String id) {
        String title = request.getOptionalProperty("title");
        if (title == null) {
            ObjectAdapter adapter = request.getContext().getMappedObjectOrResult(id);
            title = adapter.titleString();
        }
        String page = request.getOptionalProperty(Names.LINK_VIEW, request.getViewPath());
        String parameter = request.getOptionalProperty(Names.PARAMETER, RequestContext.RESULT);
        
        return new BreadCrumb(page, parameter, id, title);
    }
    
}