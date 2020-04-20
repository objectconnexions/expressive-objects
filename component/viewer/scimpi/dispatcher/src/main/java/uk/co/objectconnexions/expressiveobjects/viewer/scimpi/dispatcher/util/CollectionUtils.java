package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.value.Date;
import uk.co.objectconnexions.expressiveobjects.applib.value.DateTime;
import uk.co.objectconnexions.expressiveobjects.applib.value.Money;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.CurrentHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;

public class CollectionUtils {
    
    private static class TitleComparator implements Comparator<ObjectAdapter> {
        
        private final CurrentHolder orderField;
        
        public TitleComparator(ObjectAssociation orderField) {
            this.orderField = orderField;
        }

        public int compare(ObjectAdapter o1, ObjectAdapter o2) {
            ObjectAdapter field1 = orderField.get(o1);
            String field1Title = field1 == null ? "" : field1.titleString();
            ObjectAdapter field2 = orderField.get(o2);
            String field2Title = field2 == null ? "" : field2.titleString();
            return field1Title.compareTo(field2Title);
        }
    }

    private static class FieldComparator implements Comparator<ObjectAdapter> {
        
        private final CurrentHolder orderField;
        private final Comparator<ObjectAdapter> comparator;
        
        public FieldComparator(ObjectAssociation orderField, final Comparator<ObjectAdapter> comparator) {
            this.orderField = orderField;
            this.comparator = comparator;
        }

        public int compare(ObjectAdapter o1, ObjectAdapter o2) {
            ObjectAdapter field1 = orderField.get(o1);
            ObjectAdapter field2 = orderField.get(o2);
            if (field1 == null && field2 != null) {
                return -1;
            } else if (field2 == null && field1 != null) {
                return 1;
            } else if (field1 == null && field2 == null) {
                return 0;
            }
            return comparator.compare(field1, field2);
        }
    }

    private static class DateComparator implements Comparator<ObjectAdapter> {

        @Override
        public int compare(ObjectAdapter arg0, ObjectAdapter arg1) {
            Date obj0 = (Date) arg0.getObject();
            Date obj1 = (Date) arg1.getObject();
            return obj0.compareTo(obj1);
        }
        
    }
    
    private static class DateTimeComparator implements Comparator<ObjectAdapter> {
        
        @Override
        public int compare(ObjectAdapter arg0, ObjectAdapter arg1) {
            DateTime obj0 = (DateTime) arg0.getObject();
            DateTime obj1 = (DateTime) arg1.getObject();
            return obj0.compareTo(obj1);
        }
        
    }

    private static class MoneyComparator implements Comparator<ObjectAdapter> {

        @Override
        public int compare(ObjectAdapter arg0, ObjectAdapter arg1) {
            Money obj0 = (Money) arg0.getObject();
            Money obj1 = (Money) arg1.getObject();
            return obj0.compareTo(obj1);
        }
        
    }

    private static class NumberComparator implements Comparator<ObjectAdapter> {

        @Override
        public int compare(ObjectAdapter arg0, ObjectAdapter arg1) {
            Long obj0 = (Long) arg0.getObject();
            Long obj1 = (Long) arg1.getObject();
            return obj0.compareTo(obj1);
        }
        
    }
    
    private static class Reverse implements Comparator<ObjectAdapter> {
        
        private Comparator<ObjectAdapter> wrappedComparator;
        
        public Reverse(Comparator<ObjectAdapter> wrappedComparator) {
            this.wrappedComparator = wrappedComparator;
        }
        
        @Override
        public int compare(ObjectAdapter arg0, ObjectAdapter arg1) {
            return 0 - wrappedComparator.compare(arg0, arg1);
        }
        
    }

	public static void sort(final List<ObjectAdapter> elements, ObjectSpecification elementSpecification, final String order) {
        if (order != null && order.equals("reversed")) {
        	Collections.reverse(elements);
        } else if (order != null && order.trim().length() > 0) {
        	final boolean reverse;
        	final String field;
        	// TODO allow multiple fields to be defined
        	if (order.endsWith(":reversed")) {
        		field = order.substring(0, order.length() - 9);
        		reverse = true;
        	} else {
        		field = order;
        		reverse = false;
        	}
        	
        	final ObjectAssociation orderField = elementSpecification.getAssociation(field);
        	
        	Comparator<ObjectAdapter> comparator = null;
        	if (orderField.getSpecification().isValue()) {
            	Class<?> cls = orderField.getSpecification().getCorrespondingClass();
                if (cls.isAssignableFrom(Date.class)) {
                    comparator = new FieldComparator(orderField, new DateComparator());
                } else if (cls.isAssignableFrom(DateTime.class)) {
                    comparator = new FieldComparator(orderField, new DateTimeComparator());
                } else if (cls.isAssignableFrom(Money.class)) {
                    comparator = new FieldComparator(orderField, new MoneyComparator());
                } else if (cls.isAssignableFrom(Integer.class)) {
                    comparator = new FieldComparator(orderField, new NumberComparator());
            	}
        	} 
        	
        	if (comparator == null) {
        	    comparator = new TitleComparator(orderField);
        	}
        	
        	if (reverse) {
        	    comparator = new Reverse(comparator);
        	}
        	Collections.sort(elements, comparator);
        }

	}
}
