package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class ShortObjectType extends AbstractElementProcessor {

    public String getName() {
        return "short-object-type";
    }

    public void process(Request request) {
        final RequestContext context = request.getContext();
        final String type = request.getOptionalProperty(TYPE, "short");
        final String name = request.getOptionalProperty(NAME, "type");
        final String id = request.getOptionalProperty(OBJECT);
        final String objectId = id != null ? id : (String) context.getVariable(RequestContext.RESULT);

        ObjectAdapter object = context.getMappedObjectOrResult(objectId);
        final String field = request.getOptionalProperty(FIELD);
        if (field != null) {
            final ObjectAssociation objectField = object.getSpecification().getAssociation(field);
            object = objectField.get(object);
        }
        
        ObjectSpecification spec = object.getSpecification();
        String value =  type.equals("long") ? spec.getFullIdentifier() : spec.getShortIdentifier();
        request.getContext().addVariable(name, value, Scope.REQUEST);
    }

}

