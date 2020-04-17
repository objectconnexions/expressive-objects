package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.breadcrumbs;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.Names;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class ObjectCrumb extends AbstractCrumbElement {

    public String getName() {
        return "object-crumb";
    }

    @Override
    protected void process(Request request, CrumbCollection crumbs) {
        final String id = request.getOptionalProperty(Names.OBJECT, request.getContext().getStringVariable("_result"));
        int index = crumbs.findSameElement(id);
        if (index == -1) {
            final String page = request.getOptionalProperty(Names.LINK_VIEW, request.getViewPath());
            final String parameter = request.getOptionalProperty(Names.PARAMETER, RequestContext.RESULT);
            final String url = page + "?" + parameter + "=" + id;
            String title = request.getOptionalProperty("title");
            ObjectAdapter adapter = request.getContext().getMappedObjectOrResult(id);
            if (title == null) {
                    title = adapter.titleString();
            }                
            final String type = request.getOptionalProperty(Names.TYPE, adapter.getSpecification().getSingularName());
            final String separator = request.getOptionalProperty("separator", ">");
            final BreadCrumb crumb = new BreadCrumb(url, id, title, type, separator);
            crumbs.append(crumb);
        } else {
            crumbs.removeCrumbsAfter(index);
        }
    }
}
