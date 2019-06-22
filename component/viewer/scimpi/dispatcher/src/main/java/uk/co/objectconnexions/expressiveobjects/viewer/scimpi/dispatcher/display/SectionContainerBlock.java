package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display;

import java.util.ArrayList;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.BlockContent;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request.RepeatMarker;


class SectionContainerBlock implements BlockContent {

    private final String parentId;
    private final String containerId;
    private final List<SectionHeader> sections = new ArrayList<SectionHeader>();
    private int nextId;
    private SectionHeader selectedSection;

    public SectionContainerBlock(String parentId, String containerId) {
        this.parentId = parentId;
        this.containerId = containerId;
    }

    public void processContent(Request request) {
        RepeatMarker content = getSelectedSection().content;
        RepeatMarker resumeFrom = request.createMarker();
        content.repeat();
        request.processUtilCloseTag();
        resumeFrom.repeat();
    }

    public void addSection(String name, String id, String itemClass, String divClass, RepeatMarker marker, String view, boolean isDefault, boolean isDisabled) {
        SectionHeader header = new SectionHeader();
        header.parentId = parentId;
        header.id = id == null ? (containerId + "_" + nextId++) : id;
        header.divClass = divClass;
        header.itemClass = itemClass;
        header.name = name;
        header.content = marker;
        header.view = view;
        header.isDisabled = isDisabled;
        sections.add(header);

        if (isDefault) {
            makeActive(header);
        }
    }

    public List<SectionHeader> getSections() {
        return sections;
    }

    public SectionHeader getSelectedSection() {
        if (selectedSection == null) {
            selectedSection = getSections().get(0);
        }
        return selectedSection;
    }
/*
    public void activate(String id) {
        for (SectionHeader header : sections) {
            if (header.id.equals(id)) {
                makeActive(header);
                break;
            }
        }
    }
*/
    public boolean activate2(String id) {
        for (SectionHeader header : sections) {
            if (header.id.equals(id)) {
                makeActive(header);
                return true;
            }
        }
        return false;
    }

    private void makeActive(SectionHeader header) {
        selectedSection = header;
    }

    public boolean isEmpty() {
        return getSections().size() == 0;
    }
}

class SectionHeader {
    String parentId;
    String divClass;
    String itemClass;
    RepeatMarker content;
    String name;
    String id;
    String view;
    boolean isDisabled;

    public String getSectionCode() {
        return (parentId == null ? "" : parentId + "|") + id;
    }

}
