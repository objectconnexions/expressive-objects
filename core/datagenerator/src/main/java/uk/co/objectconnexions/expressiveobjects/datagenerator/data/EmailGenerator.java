package uk.co.objectconnexions.expressiveobjects.datagenerator.data;


import uk.co.objectconnexions.expressiveobjects.datagenerator.Generator;
import uk.co.objectconnexions.expressiveobjects.datagenerator.NameGenerator;

public class EmailGenerator extends Generator<String> {

    private NameGenerator tlds = new NameGenerator(new String[] {"co.uk", "com", "net"});
    private NameGenerator domains = new NameGenerator(new String[] {"running", "shopping", "bizclub", "laptopstore"});

    @Override
    public String generate() {
        // TODO Auto-generated method stub
        return null;
    }

    public String generate(String first, String last) {
        NameGenerator names = new NameGenerator(last.substring(0, 1), last + first.substring(0, 1),
                first + "." + last, first.substring(0, 1) + last.substring(0, 1));
        return  names.generate() + "@" + domains.generate() + "." + tlds.generate();
    }

}
