package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.breadcrumbs;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class ResetCrumbTrail extends AbstractCrumbElement {

    public String getName() {
        return "reset-crumb-trail";
    }
    
    @Override
    protected void process(Request request, CrumbCollection crumbs) {
        crumbs.clear();
    }
}
