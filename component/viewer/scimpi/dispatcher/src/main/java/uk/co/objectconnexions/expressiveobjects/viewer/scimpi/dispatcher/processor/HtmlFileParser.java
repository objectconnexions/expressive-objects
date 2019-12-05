/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Remark;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.ParserException;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.Resources;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.StringUtils;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ScimpiException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.action.Attributes;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.HtmlSnippet;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.Snippet;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.SwfTag;

public class HtmlFileParser {
    private static final Logger LOG = Logger.getLogger(HtmlFileParser.class);
    private final ProcessorLookup processors;

    public HtmlFileParser(final ProcessorLookup processors) {
        this.processors = processors;
    }

    public Stack<Snippet> parseHtmlFile(final String filePath, final RequestContext context) {
        final Stack<Snippet> tagsBeforeContent = new Stack<Snippet>();
        final Stack<Snippet> tagsAfterContent = new Stack<Snippet>();
        parseHtmlFile("/", filePath, context, tagsBeforeContent, tagsAfterContent);
        return tagsBeforeContent;
    }

    public void parseHtmlFile(final String parentPath, final String filePath, final RequestContext context, final Stack<Snippet> allTags, final Stack<Snippet> tagsForPreviousTemplate) {
        LOG.debug("parent/file: " + parentPath + " & " + filePath);
        final File directory = filePath.startsWith("/") ? new File(".") : new File(parentPath);
        final File loadFile = new File(directory.getParentFile(), filePath);
        final String loadPath = loadFile.getPath().replace('\\', '/');
        LOG.debug("loading template '" + loadPath + "'");
        final InputStream in = getInputStream(context, loadPath);
        if (in == null) {
            throw new ScimpiException("Failed to load scimpi file " + filePath);
        }

        Page page;
        try {
            page = new Page(in, null);
        } catch (final UnsupportedEncodingException e) {
            throw new ScimpiException(e);
        }
        final Lexer lexer = new Lexer(page);

        Node node = null;
        try {
            Stack<Snippet> tags = allTags;
            String lineNumbers = "1";
            String template = null;
            tags.push(new HtmlSnippet(lineNumbers, filePath));

            // NOTE done like this the tags can be cached for faster processing
            while ((node = lexer.nextNode()) != null) {
                if (node instanceof Remark) {
                    // TODO need to pick up on comments within tags; at the
                    // moment this splits a tag into two causing a
                    // failure later
                    continue;

                } else if (node instanceof TagNode && ((TagNode) node).getTagName().startsWith("SWF:")) {
                    final TagNode tagNode = (TagNode) node;
                    final String tagName = tagNode.getTagName().toUpperCase();
                    LOG.debug(tagName);

                    // TODO remove context & request from Attributes -- the tags
                    // will be re-used across
                    // requests
                    final Attributes attributes = new Attributes(tagNode, context);
                    int type = 0;
                    if (tagNode.isEndTag()) {
                        type = SwfTag.END;
                    } else {
                        type = tagNode.isEmptyXmlTag() ? SwfTag.EMPTY : SwfTag.START;
                    }
                    testForProcessorForTag(lexer, tagName);
                    lineNumbers = lineNumbering(node);
                    final SwfTag tag = new SwfTag(tagName, attributes, type, lineNumbers, loadFile.getCanonicalPath());
                    tags.push(tag);

                    if (tagName.equals("SWF:IMPORT")) {
                        if (!tagNode.isEmptyXmlTag()) {
                            throw new ScimpiException("Import tag must be empty");
                        }
                        String importFile = tagNode.getAttribute("file");
                        if (context.isDebug()) {
                            context.getWriter().println("<!-- " + "import file " + importFile + " -->");
                        }
                        importFile = context.replaceVariables(importFile);
                        parseHtmlFile(loadPath, importFile, context, tags, tagsForPreviousTemplate);
                    }

                    if (tagName.equals("SWF:TEMPLATE")) {
                        if (!tagNode.isEmptyXmlTag()) {
                            throw new ScimpiException("Template tag must be empty");
                        }
                        if (template != null) {
                            throw new ScimpiException("Template tag can only be used once within a file");
                        }
                        template = tagNode.getAttribute("file");
                        template = context.replaceVariables(template);
                        if (context.isDebug()) {
                            context.getWriter().println("<!-- " + "apply template " + template + " -->");
                        }
                        tags = new Stack<Snippet>();
                    }

                    if (tagName.equals("SWF:CONTENT")) {
                        if (!tagNode.isEmptyXmlTag()) {
                            throw new ScimpiException("Content tag must be empty");
                        }
                        if (context.isDebug()) {
                            context.getWriter().println("<!-- " + "insert content into template -->");
                        }
                        tags.addAll(tagsForPreviousTemplate);
                    }
                } else {
                    final Snippet snippet = tags.size() == 0 ? null : tags.peek();
                    if (snippet instanceof HtmlSnippet) {
                        ((HtmlSnippet) snippet).append(node.toHtml());
                    } else {
                        final HtmlSnippet htmlSnippet = new HtmlSnippet(lineNumbers, filePath);
                        htmlSnippet.append(node.toHtml());
                        tags.push(htmlSnippet);
                    }
                }

            }
            in.close();

            if (template != null) {
                final String filePathRoot = loadPath.startsWith("/") ? "" : "/";
                parseHtmlFile(filePathRoot + loadPath, template, context, allTags, tags);
            }

        } catch (final ParserException e) {
            exception(loadPath, node, e);
            // throw new ScimpiException(e);
        } catch (final RuntimeException e) {
            // TODO: extend to deal with other exceptions
            exception(loadPath, node, e);
        } catch (final IOException e) {
            throw new ScimpiException(e);
        }
    }

    private InputStream getInputStream(final RequestContext context, final String request) {
   //     return context.openStream(loadPath);
        

//        private void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
            final String servletPath = StringUtils.stripLeadingSlash(request);
            if (LOG.isInfoEnabled()) {
                LOG.info("request: " + servletPath);
            }

            // try to load from filesystem
            final InputStream is2 = context.openStream(request); //getRealPath(context, servletPath);
            if (is2 != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("request: " + servletPath + " loaded from filesystem");
                }
                return is2;
            }

            // otherwise, try to load from classpath
            final InputStream is = Resources.getResourceAsStream(servletPath);
            if (is != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("request: " + servletPath + " loaded from classpath");
                }
                return is;
            }

            LOG.warn("failed to load resource from classpath or file system: " + servletPath);
            
            return null;
        }
/*
        private FileInputStream getRealPath(RequestContext context, final String path) {
            final String realPath = context..getRealPath(path);
            if (realPath == null) {
                return null;
            }
            try {
                return new FileInputStream(realPath);
            } catch (final FileNotFoundException e) {
                return null;
            }
        }
    
*/
    private void exception(final String filePath, final Node node, final Exception e) {
        String lineNumbers = "";
        String element = ("" + node).toLowerCase();
        if (node instanceof TagNode) {
            lineNumbers = ":" + lineNumbering(node);
            element = "tag &lt;" + node.getText() + "&gt;";
        }
        throw new ScimpiException("Error processing " + element + " in " + filePath + lineNumbers, e);
    }

    private String lineNumbering(final Node node) {
        String lineNumbers;
        final int startingLine = ((TagNode) node).getStartingLineNumber() + 1;
        final int endingLine = ((TagNode) node).getStartingLineNumber() + 1;
        if (startingLine == endingLine) {
            lineNumbers = "" + startingLine;
        } else {
            lineNumbers = startingLine + "-" + endingLine;
        }
        return lineNumbers;
    }

    private void testForProcessorForTag(final Lexer lexer, final String tagName) {
        final ElementProcessor elementProcessor = processors.getFor(tagName);
        if (elementProcessor == null) {
            throw new ScimpiException("No processor for tag " + tagName.toLowerCase());
        }
    }
}
