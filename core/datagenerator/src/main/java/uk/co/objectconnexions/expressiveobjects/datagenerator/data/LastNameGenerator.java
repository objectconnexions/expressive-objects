package uk.co.objectconnexions.expressiveobjects.datagenerator.data;

import uk.co.objectconnexions.expressiveobjects.datagenerator.NameGenerator;

public class LastNameGenerator extends NameGenerator {
    private final static String[] NAMES = new String[] {"Smith", "Jones", "Brown", "Moore", "Black", "Chan"};   

    public LastNameGenerator() {
        super(NAMES);
    }
}
