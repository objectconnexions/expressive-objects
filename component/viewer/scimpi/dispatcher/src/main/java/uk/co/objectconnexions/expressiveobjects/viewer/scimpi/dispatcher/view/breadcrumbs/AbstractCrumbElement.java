package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.breadcrumbs;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public abstract class AbstractCrumbElement extends AbstractElementProcessor {

    public void process(Request request) {
        String breadcrumbsId = request.getOptionalProperty("id", "1");
        String variable = "_breadcrumbs_" + breadcrumbsId;
        CrumbCollection crumbs = (CrumbCollection) request.getContext().getVariable(variable);
        if (crumbs == null) {
            crumbs = new CrumbCollection();
            request.getContext().addVariable(variable, crumbs, Scope.SESSION);
        }
        process(request, crumbs);
    }

    protected void process(Request request, CrumbCollection crumbs) {
        String elementClass = request.getOptionalProperty("class", "scimpi-breadcrumbs");
        request.appendHtml("<p class=\"" + elementClass + "\">" + crumbs.display() + "</p>");
    }
}
