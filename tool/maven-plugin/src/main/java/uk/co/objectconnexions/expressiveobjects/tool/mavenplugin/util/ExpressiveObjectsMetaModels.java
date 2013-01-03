package uk.co.objectconnexions.expressiveobjects.tool.mavenplugin.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.progmodel.app.ExpressiveObjectsMetaModel;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.NoSuchRealmException;

public final class ExpressiveObjectsMetaModels {
    
    private ExpressiveObjectsMetaModels(){}

    public static void disposeSafely(ExpressiveObjectsMetaModel expressiveObjectsMetaModel) {
        if (expressiveObjectsMetaModel == null) {
            return;
        }
        expressiveObjectsMetaModel.shutdown();
    }



}
