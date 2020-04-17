package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.breadcrumbs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ToString;


public class CrumbCollection implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final List<BreadCrumb> crumbs = new ArrayList<BreadCrumb>();
    
    public BreadCrumb parent() {
        int index = crumbs.size() - 2;
        return index < 0 ? null : crumbs.get(index);
    }

    public String display() {
        StringBuffer html = new StringBuffer();
        int noCrumbs = crumbs.size();
        for (int i = 0; i < noCrumbs; i++) {
            BreadCrumb crumb = crumbs.get(i);
            if (i == 0) {
                html.append(crumb.displayFirst());
            } else if (i + 1 == noCrumbs) {
                    html.append(crumb.displayLast());
            } else {
                html.append(crumb.displayNormal());
            }
        }
        return html.toString();
    }

    public void append(final BreadCrumb crumb) {
        int index = findSameElement(crumb.getId());
        if (index == -1) {
            crumbs.add(crumb);
        } else {
            removeCrumbsAfter(index);
        }
    }

    public void clear() {
        crumbs.clear();
    }
 
    public void removeCrumbsAfter(int index) {
        for (int i = crumbs.size() - 1; i > index; i--) {
            crumbs.remove(i);
        }
    }

    public int findSameElement(String id) {
        if (id != null) {
            int splitAt = id.indexOf('^');
            int end = splitAt == -1 ? id.length() : splitAt + 1;
            String id4 = id.substring(0, end);
            for (int index = 0; index < crumbs.size(); index++) {
                String id2 = (String) crumbs.get(index).getId();
                if (id2 == null || id2.startsWith(id4)) {
                    return index;
                }
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
        ToString toString = new ToString(this);
        for (BreadCrumb crumb : crumbs) {
            toString.append(crumb.toString());
            toString.append(",");
        }
        return "{" + toString + "}";
    }
}