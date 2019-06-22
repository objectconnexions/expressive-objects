package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;


public class SectionContainer extends AbstractElementProcessor {

    private static final String PARENT_SECTION = "_parent_section";
    private static final String SECTION_SELECTED = "_section_selection";

    public String getName() {
        return "sections";
    }

    public void process(Request request) {
        String id = request.getOptionalProperty(ID, "section");
        String listClass = request.getOptionalProperty("list-class", "nav");
        String activeItemClass = request.getOptionalProperty("active-item-class", "active");
        String disabledClass = request.getOptionalProperty("disabled-class", "disabled");
        String returnTo = request.getOptionalProperty("return-to");
        String returnName = request.getOptionalProperty("return-title");

        RequestContext context = request.getContext();
        String parentId = (String) context.getVariable(PARENT_SECTION);
        String selectedSection = (String) context.getVariable(SECTION_SELECTED);
        if (selectedSection == null) {
            selectedSection = "";
        }

        SectionContainerBlock block = new SectionContainerBlock(parentId, id);
        request.setBlockContent(block);
        
        request.pushNewBuffer();
        request.processUtilCloseTag();
        String header = request.popBuffer();
        

        if (selectedSection != null) {
            String[] split = selectedSection.split("[|]");
            for (String sectionId : split) {
                if (block.activate2(sectionId)) {
                    break;
                }
            }
        }

        if (!block.isEmpty()) {
            String linkTo = context.getResourceFile();
            displaySectionHeaders(request, block, linkTo, returnTo, returnName, id, listClass, activeItemClass, disabledClass);
            context.addVariable(SECTION_SELECTED, selectedSection, Scope.INTERACTION);
            context.addVariable(PARENT_SECTION,  (parentId == null ? "" : parentId + "|") + block.getSelectedSection().id, Scope.REQUEST);
            request.appendHtml(header);
            displayCurrentSection(request, block);
        }
    }

    public SectionContainerBlock displaySectionHeaders(
            Request request,
            SectionContainerBlock block,
            String linkTo,
            String returnTo,
            String returnName,
            String id,
            String listClass,
            String activeItemClass,
            String disabledClass) {
        
        request.appendHtml("<ul id=\"" + id + "\" class=\"" + listClass + "\">");
        if (returnTo != null) {
            if (returnName == null) {
                returnName = "Back";
            }
            request.appendHtml("<li class=\"return-to\"><a href=\"" + returnTo + "\">" + returnName + "</a></li>");
        }
        
        for (SectionHeader section : block.getSections()) {
            String classes = section.itemClass == null ? "" : section.itemClass;
            classes += section.isDisabled ? " " + disabledClass : "";
            classes += section == block.getSelectedSection() ? " " + activeItemClass : "";
            /*
            String itemClass = section.isDisabled ? "class=\"" + disabledClass + " "  + section.itemClass + "\"" : "";
            itemClass = section == block.getSelectedSection() ? "class=\"" + activeItemClass + " " + section.itemClass + "\"" : itemClass + " ";
            if (itemClass.trim().equals("") && !section.itemClass.equals("")) {
                itemClass =  "class=\"" + section.itemClass + "\""; 
            }
            */
            String name = section.name;
            String href;
            if (section.view == null) {
                href = "href=\"" + linkTo + "?" + SECTION_SELECTED + "=" + section.getSectionCode() + " \"";
            } else {
                href = "href=\"" + section.view + "\"";
            }
            String link = "<a " + (section.isDisabled ? "" : href) + ">" + name + "</a>";
            String itemClass = classes.length() > 0 ? "class=\"" + classes + "\"" : "";
            request.appendHtml("<li " + itemClass + ">" + link + "</li>");
        }
        request.appendHtml("</ul>");
        return block;
    }

    public void displayCurrentSection(Request request, SectionContainerBlock block) {
        String cssClass = block.getSelectedSection().divClass;
        String classSegment =  cssClass == null ? "" : " class=\"" + cssClass + "\"";
        String cssId = block.getSelectedSection().id;
        String idSegment =  cssId == null ? "" : " id=\"" + cssId + "\"";
        request.appendHtml("<div" + idSegment + classSegment + ">");
        block.processContent(request);
        request.appendHtml("</div>");
    }

}
