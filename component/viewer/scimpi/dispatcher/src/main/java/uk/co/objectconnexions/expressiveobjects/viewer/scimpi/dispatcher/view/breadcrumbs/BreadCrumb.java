package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.breadcrumbs;

import java.io.Serializable;

public class BreadCrumb implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final String id;
    protected final String title;
    protected String url;

    public BreadCrumb(String page, String parameter, String id, String title) {
        this.id = id;
        this.title = title;
        url = page + "?" + parameter + "=" + id;
    }

    public String getId() {
        return id;
    }

    public String displayFirst() {
        return "<a href=\"" + url + "\">" + title + "</a>";
    }

    public String displayNormal() {
        return "<a href=\"" + url + "\">" + title + "</a>";
    }

    public String displayLast() {
        return "<span class=\"scimpi-current\">" + title + "</span>";
    }
    
    @Override
    public String toString() {
        return title + "|" + url;
    }
}
