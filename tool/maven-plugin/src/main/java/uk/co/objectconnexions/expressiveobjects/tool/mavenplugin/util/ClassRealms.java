package uk.co.objectconnexions.expressiveobjects.tool.mavenplugin.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.classworlds.ClassRealm;

public final class ClassRealms {
    
    private ClassRealms(){}

    public static void addFileToRealm(ClassRealm expressiveObjectsRealm, final File file, Log log) throws IOException, MalformedURLException {
        log.info(file.getCanonicalPath());
    
        final URL url = file.toURI().toURL();
        expressiveObjectsRealm.addConstituent(url);
    }


}
