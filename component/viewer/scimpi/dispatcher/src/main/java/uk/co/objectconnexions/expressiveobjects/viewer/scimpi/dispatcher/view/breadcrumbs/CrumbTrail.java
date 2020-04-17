package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.breadcrumbs;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class CrumbTrail extends AbstractCrumbElement {

    public String getName() {
        return "crumb-trail";
    }

    @Override
    protected void process(Request request, CrumbCollection crumbs) {
        String elementClass = request.getOptionalProperty("class", "scimpi-crumbs");
        request.appendHtml("<div class=\"" + elementClass + "\">" + crumbs.display() + "</div>");
    }
}
