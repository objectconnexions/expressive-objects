package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.breadcrumbs;

import java.io.Serializable;

public class BreadCrumb implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String title;
    private final String url;
    private final String separator;
    private final String hint;

    public BreadCrumb(String url, String id, String title, String hint, String separator) {
        this.url = url;
        this.id = id;
        this.title = title;
        this.hint = hint;
        this.separator = separator;
    }

    public String getId() {
        return id;
    }

    public String displayFirst() {
        return "<span class=\"scimpi-crumb scimpi-available\"><a href=\"" + url + "\""  + tooltip() +">" + "<span class=\"name\">"
                + title + "</span></a></span/>";
    }

    public String displayNormal() {
        return "<span class=\"scimpi-crumb scimpi-available\">" + separator() + "<a href=\"" + url + "\"" + tooltip() + ">"
                + "<span class=\"name\">" + title + "</span></a></span/>";
    }

    public String displayLast() {
        return "<span class=\"scimpi-crumb scimpi-current\"" + tooltip() + ">" + separator() + "<span class=\"name\">" + title
                + "</span></span/>";
    }

    private String tooltip() {
       if(hint != null) {
           return " title=\"" + hint + "\"";
       }
        return "";
    }

    private String separator() {
        return "<span class=\"scimpi-crumb scimpi-separator\">" + separator + "</span>";
    }

    @Override
    public String toString() {
        return title + "|" + url;
    }
}
