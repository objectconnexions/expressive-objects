package uk.co.objectconnexions.expressiveobjects.datagenerator.data;

import uk.co.objectconnexions.expressiveobjects.datagenerator.NameGenerator;

public class FirstNameGenerator extends NameGenerator {
    private final static String[] NAMES =new String[] {"Fred", "Jack", "Bob", "Lil", "Andy", "Mary"};
    
    public FirstNameGenerator() {
        super(NAMES);
    }
}
