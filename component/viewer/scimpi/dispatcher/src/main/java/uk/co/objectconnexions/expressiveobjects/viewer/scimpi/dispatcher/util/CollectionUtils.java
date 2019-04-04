package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;

public class CollectionUtils {

	public static void sort(final List<ObjectAdapter> elements, ObjectSpecification elementSpecification, final String order) {
        if (order != null && order.equals("reversed")) {
        	Collections.reverse(elements);
        } else if (order != null && order.trim().length() > 0) {
        	final boolean reverse;
        	final String field;
        	// TODO allow multiple fields to be defined
        	if (order.startsWith("reversed ")) {
        		field = order.substring(9);
        		reverse = true;
        	} else {
        		field = order;
        		reverse = false;
        	}
        	final ObjectAssociation orderField = elementSpecification.getAssociation(field);
        	Collections.sort(elements, new Comparator<ObjectAdapter>() {
        		public int compare(ObjectAdapter o1, ObjectAdapter o2) {
        			ObjectAdapter field1 = orderField.get(o1);
        			String field1Title = field1 == null ? "" : field1.titleString();
        			ObjectAdapter field2 = orderField.get(o2);
        			String field2Title = field2 == null ? "" : field2.titleString();
        			int compareTo = field1Title.compareTo(field2Title);
        			if (reverse) {
        				compareTo = 0 - compareTo;
        			}
					return compareTo;
        		}
        	});
        }

	}
}
