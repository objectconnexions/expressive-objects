package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request.RepeatMarker;


public class Section extends AbstractElementProcessor {

    public String getName() {
        return "section";
    }

    public void process(Request request) {
        String title = request.getRequiredProperty("title");
        String id = request.getOptionalProperty(ID);
        String divClass = request.getOptionalProperty(CLASS);
        String itemClass = request.getOptionalProperty("item-" + CLASS);
        String view = request.getOptionalProperty(VIEW);
        boolean isDefault = request.isRequested("initial-section", false);
        boolean isDisabled = request.isRequested("disable", false);
        
        SectionContainerBlock block = (SectionContainerBlock) request.getBlockContent();

        RepeatMarker marker = request.createMarker();
        block.addSection(title, id, itemClass, divClass, marker, view, isDefault, isDisabled);

        request.skipUntilClose();
    }

}
