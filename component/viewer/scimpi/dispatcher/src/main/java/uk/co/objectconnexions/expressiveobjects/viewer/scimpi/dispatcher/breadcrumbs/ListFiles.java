package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.breadcrumbs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import org.apache.log4j.Logger;


public class ListFiles extends Files {

    private static final Logger LOG = Logger.getLogger(ListFiles.class);

    public String getName() {
        return "list-files";
    }

    public void process(Request request) {
        String path = request.getRequiredProperty("path");
        String limitTo = request.getRequiredProperty("limit-crumbs");
        String listPath = request.getOptionalProperty("list-view", "list-files.shtml");
        
        
        String showFile = (String) request.getContext().getVariable("show-file");
        if (showFile != null) {
            path = limitTo + showFile;
        }
        
        final String search = (String) request.getContext().getVariable("search");
        if (search != null) {
            String device = (String) request.getContext().getVariable("device");
            final String fromDate = (String) request.getContext().getVariable("from");
            
            File directory = new File(limitTo + "/log/");
            File[] listFiles = directory.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.compareTo(fromDate) >= 0;
                }
            });
            final int searchForPacket = Integer.valueOf(search);

            Arrays.sort(listFiles);
            for (File dateDirectory : listFiles) {

                File file = new File(dateDirectory, device);
                request.appendHtml("<p>" + file.getAbsolutePath() + "</p>");
                File[] files = file.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        int packetNo = Integer.valueOf(name.split("[_-]")[1]);
                        return packetNo == searchForPacket && name.endsWith(".data");
                    }
                });
                if (files != null && files.length > 0) {
                    path = files[0].getAbsolutePath();
                    // request.appendHtml("<p>" + files[0].getAbsolutePath() + "</p>");
                    break;
                }
            }
        }
        
        File pp = new File(path);
        if (!pp.getAbsolutePath().startsWith(limitTo)) {
            request.appendHtml("<p class=\"error\">Access restricted to " + limitTo + "</p>");            
            return;
        }

        String interactionParameters = request.getContext().encodedInteractionParameters();
        
        
        if (pp.isDirectory()) {
            listDirectory(request, listPath, pp, interactionParameters);
        } else {
            String action = (String) request.getContext().getVariable("action");
             
            File file = pp;
            request.appendHtml("<h3>" + breadcrumbs(listPath, interactionParameters, file) + "</h3>");
            if (file.exists()) {
                if ("edit".equalsIgnoreCase(action)) {
                    editFile(request, path, file, interactionParameters);
                } else {
                    displayFile(request, file, path, interactionParameters);
                }
                request.appendHtml("<h3>" + breadcrumbs(listPath, interactionParameters, file) + "</h3>");
            } else {
                request.appendHtml("<p class=\"error\">No such file " + file.getAbsolutePath() + "</p>");
            }
        }
    }

    private void displayFile(Request request, File filePath, String pathTitle, String interactionParameters) {
        request.appendHtml("<p><a href=\"?action=edit&path=" + pathTitle + interactionParameters + "\">edit</a></p>");
        request.appendHtml("<pre class=\"data\">");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            int lineNo = 0;
            String device  = "";
            String date = "";
            String line;
            while ((line = reader.readLine()) != null) {
                if (lineNo == 0) {
                    request.appendAsHtmlEncoded("\n");
                }
                if (lineNo == 1) {
                    String[] split = line.split("[ /]");
                    if (split.length == 3) {
                        device = split[1];
                        date = split[2];
                    }
                }
                line = line.replace("!#", "\u2026\n\u2026\n!#");
                line = line.replace("#=", "\u2026\n#=");
                
                if (line.startsWith("# from packet")) {
                    int start = 14;
                    int end = line.indexOf(',');
                    String first = line.substring(0, start);
                    String packet = line.substring(start, end);
                    String last = line.substring(end);
                    
                    String url = "?search=" + packet + "&device=" + device + "&from=" + date + interactionParameters;
                    line = first + "<a href=\"" + url + "\">" + packet + "</a>" + last;
                }
                request.appendHtml(line);
                request.appendAsHtmlEncoded("\n");
                lineNo++;
            }
        } catch (FileNotFoundException e) {
            LOG.error("failed to open file", e);
            request.appendHtml("<p class=\"error\">File not found " + filePath.getAbsolutePath() + "</p>");
        } catch (IOException e) {
            LOG.error("failed to read file", e);
            request.appendHtml("<p class=\"error\">Failed to read file " + filePath.getAbsolutePath() + "</p>");
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                LOG.error("failed to close file", e);
                request.appendHtml("<p class=\"error\">Failed to close file " + filePath.getAbsolutePath() + "</p>");
            }
        }
        request.appendHtml("</pre>");
    }

    private void editFile(Request request, String pathTitle, File filePath, String interactionParameters) {
        request.appendHtml("<p><a href=\"?path=" + pathTitle + interactionParameters + "\">back</a></p>");
        
        request.appendHtml("<form method=\"POST\" action=\"update-file.app\">");
        request.appendHtml("<input type=\"text\" name=\"path\" value=\"" + pathTitle + "\">");
        request.appendHtml("<input type=\"text\" name=\"_section_selection\" value=\"browser\">");
        request.appendHtml("<textarea cols=\"80\" rows=\"50\" name=\"content\" class=\"data\">");

        FileReader reader = null;
        try {
            char buffer[] = new char[256];
            reader = new FileReader(filePath);
            int len = reader.read(buffer);
            while(len > 0) {
                LOG.info("buffer " + len);
                String line = new String(buffer, 0, len);
                request.appendHtml(line);
                len = reader.read(buffer);
            }
        } catch (FileNotFoundException e) {
            LOG.error("failed to open file", e);
            request.appendHtml("<p class=\"error\">File not found " + filePath.getAbsolutePath() + "</p>");
        } catch (IOException e) {
            LOG.error("failed to read file", e);
            request.appendHtml("<p class=\"error\">Failed to read file " + filePath.getAbsolutePath() + "</p>");
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                LOG.error("failed to close file", e);
                request.appendHtml("<p class=\"error\">Failed to close file " + filePath.getAbsolutePath() + "</p>");
            }
        }
        request.appendHtml("</textarea>");
        request.appendHtml("<button type=\"submit\" name=\"save\" value=\"save\">Save</button>");
        request.appendHtml("</form>");
    }

    private void listDirectory(Request request, String pathTitle, File filePath, String interactionParameters) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        
        File directory = filePath; 
        if (directory.exists()) {
            request.appendHtml("<h3>" + breadcrumbs(pathTitle, interactionParameters, directory) + "</h3><ol>");
            request.appendHtml("<li>       Date             Size                File</li>");
            request.appendHtml("<li>===================   ========   ================================</li>");
            File[] listFiles = directory.listFiles();
            Arrays.sort(listFiles);
            for (File file : listFiles) {
                String name = file.getName();
                String date = dateFormat.format(new Date(file.lastModified()));
                String size = numberFormat.format(file.length());
                int len = size.length();
                if (len < 8) {
                    size = "        ".substring(len) + size;
                }
                request.appendHtml("<li>" + date + "   " + size + "   <a href=\"" + pathTitle + "?path=" + file.getAbsolutePath() + interactionParameters + "\">" + name + "</a></li>");
            }
            request.appendHtml("</ol>");
            request.appendHtml("<h3>" + breadcrumbs(pathTitle, interactionParameters, directory) + "</h3><ol>");
        } else {
            request.appendHtml("<p class=\"error\">No such path " + directory.getAbsolutePath() + "</p>");
        }
    }
}
