package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.breadcrumbs;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class ActionCrumb extends AbstractCrumbElement {

    public String getName() {
        return "action-crumb";
    }
    
    @Override
    protected void process(Request request, CrumbCollection crumbs) {
        // TODO use object and method name to derive name and hint
        final String id = request.getOptionalProperty("id", "action");
        final String url = request.getOptionalProperty("page", request.getContext().getRequestedFile());
        final String title = request.getOptionalProperty("title", request.getContext().getStringVariable("title"));
        final String separator = request.getOptionalProperty("separator", ">");
        crumbs.append(new BreadCrumb(url, id, title, null, separator));
    }
}
