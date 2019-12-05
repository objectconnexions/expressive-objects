package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ForbiddenException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ScimpiException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

import com.petebevin.markdown.MarkdownProcessor;


public class MarkdownField extends AbstractElementProcessor {

    public void process(Request request) {
        String id = request.getOptionalProperty(OBJECT);
        String fieldName = request.getRequiredProperty(FIELD);
        ObjectAdapter object = request.getContext().getMappedObjectOrResult(id);
        ObjectAssociation field = object.getSpecification().getAssociation(fieldName);
        if (field == null) {
            throw new ScimpiException("No field " + fieldName + " in " + object.getSpecification().getFullIdentifier());
        }
        if (field.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, Where.ANYWHERE).isVetoed()) {
            throw new ForbiddenException(field, ForbiddenException.VISIBLE);
        }

        ObjectAdapter fieldReference = field.get(object);

        if (fieldReference != null) {
            String value = fieldReference == null ? "" : fieldReference.titleString();

            String html = new MarkdownProcessor().markdown(value);
            request.appendHtml(html);
        }
    }

    public String getName() {
        return "markdown";
    }
}
