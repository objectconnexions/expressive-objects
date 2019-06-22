package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.breadcrumbs;

import java.io.Serializable;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public interface BreadCrumbFactory extends Serializable {

    String getId(Request request);

    BreadCrumb createCrumb(Request request, String id);
    
}
