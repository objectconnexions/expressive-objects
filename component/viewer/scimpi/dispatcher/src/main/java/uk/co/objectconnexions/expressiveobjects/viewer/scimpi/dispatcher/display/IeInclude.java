package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.Names;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;


public class IeInclude implements ElementProcessor, Names {

    private static final String NAME = "ie-include";

    public String getName() {
        return NAME;
    }

    public void process(Request request) {
        String level = request.getOptionalProperty("less-than");
        boolean andOther = Boolean.valueOf(request.isRequested("other-browsers", false));
        if (level != null) {
            request.appendHtml("<!--[if lte IE " + level + "]>");
            if (andOther) {
                request.appendHtml("<!-->");
            }
        } else {
            level = request.getOptionalProperty("greater-than");
            if (level != null) {
                request.appendHtml("<!--[if gte IE " + level + "]>");
                if (andOther) {
                    request.appendHtml("<!-->");
                }
            } else {
                request.appendHtml("<!--[if IE]>");
            }
        }
        request.processUtilCloseTag();
        if (andOther) {
            request.appendHtml("<!--");
        }
        request.appendHtml("<![endif]-->");
    }
}
