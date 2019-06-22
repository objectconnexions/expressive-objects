package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.breadcrumbs;

import java.io.File;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;

public abstract class Files extends AbstractElementProcessor {

    protected String breadcrumbs(String requestPath, String interactionParameters, File file) {
        return parentBreadcrumbs(requestPath, interactionParameters, file.getParentFile()) + "/" + file.getName();
    }
        
    private String parentBreadcrumbs(String requestPath, String interactionParameters, File file) {
        String fileName = file.getName();
        File parent = file.getParentFile();
        if (parent != null) {
            return parentBreadcrumbs(requestPath, interactionParameters, parent) + "/" + "<a href=\"" + requestPath + "?path=" + file.getAbsolutePath() + interactionParameters + "\">" + fileName + "</a>";
        }
        return fileName;
    }

}

