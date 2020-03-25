package uk.co.objectconnexions.expressiveobjects.datagenerator.data;

import uk.co.objectconnexions.expressiveobjects.datagenerator.CsvFileReader;
import uk.co.objectconnexions.expressiveobjects.datagenerator.NameGenerator;

public class LastNameGenerator extends NameGenerator {
    private final static String[] NAMES = new String[] {"Smith", "Jones", "Brown", "Moore", "Black", "Chan"};

    public LastNameGenerator() {
        load("name_data.csv", "last_name", NAMES);
    }

    private void load(String filePath, String columnName, String[] defaults) {
        CsvFileReader reader = new CsvFileReader();
        reader.loadResource(filePath);
        while(reader.hasMore()) {
            add(reader.next().value(columnName));
        }
        reader.close();
    }
}
