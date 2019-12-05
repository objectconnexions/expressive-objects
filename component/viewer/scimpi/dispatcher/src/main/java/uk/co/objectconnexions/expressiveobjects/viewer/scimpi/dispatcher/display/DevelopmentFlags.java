package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class DevelopmentFlags  extends AbstractElementProcessor {

    public String getName() {
        return "development-flags";
    }

    public void process(Request request) {
        String configuration = request.getRequiredProperty("from");
        String[] lines = configuration.split("\n");
        for (String line : lines) {
            String[] l = line.split("=");
            String key = "_" + l[0];
            String value = l.length == 1 ? "" : l[1].trim();
            if (value.equals("") || value.equalsIgnoreCase("no")  || value.equalsIgnoreCase("false")) {
                request.getContext().clearVariable(key, Scope.GLOBAL);
            } else {
                request.getContext().addVariable(key, value, Scope.GLOBAL);
            }
        }
    }

}

