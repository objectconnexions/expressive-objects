package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.breadcrumbs;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class PageCrumb extends AbstractCrumbElement {

    public String getName() {
        return "page-crumb";
    }
    
    @Override
    protected void process(Request request, CrumbCollection crumbs) {
        RequestContext context = request.getContext();
        final String id = request.getOptionalProperty("id", context.getRequestedFile());
        final String url = request.getOptionalProperty("page", context.fullUriPath(context.getRequestedFile()));
        final String title = request.getOptionalProperty("title", context.getStringVariable("title"));
        final String hint = request.getOptionalProperty("hint");
        final String separator = request.getOptionalProperty("separator", ">");
        if (request.isRequested("reset", true)) {
            crumbs.clear();
        }
        crumbs.append(new BreadCrumb(url, id, title, hint, separator));
    }
}
