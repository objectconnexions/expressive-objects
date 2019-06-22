package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.breadcrumbs;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.Names;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;


public class BasicCrumbFactory implements BreadCrumbFactory {

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
        final String page = request.getOptionalProperty(Names.LINK_VIEW, request.getViewPath());
        final String parameter = request.getOptionalProperty(Names.PARAMETER, RequestContext.RESULT);

        final String label = request.getOptionalProperty("label", ">");

        return new BreadCrumb(page, parameter, id, title) {
            private static final long serialVersionUID = 1L;

            @Override
            public String displayFirst() {
                return "<span class=\"scimpi-crumb scimpi-available\"><a href=\"" + url + "\">" + "<span class=\"name\">" + title + "</span></a></span/>";
            }

            @Override
            public String displayNormal() {
                return "<span class=\"scimpi-crumb scimpi-available\"><a href=\"" + url + "\">" + title() +  "<span class=\"name\">" + title + "</span></a></span/>";
            }

            @Override
            public String displayLast() {
                return "<span class=\"scimpi-crumb scimpi-current\">" + title() +  "<span class=\"name\">" + title + "</span></span/>";
            }

            private String title() {
                return "<span class=\"scimpi-crumb scimpi-separator\">" + label + "</span>";
            }
        };
    }

}
