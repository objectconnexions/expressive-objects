package uk.co.objectconnexions.expressiveobjects.datagenerator;

import uk.co.objectconnexions.expressiveobjects.applib.value.Date;

public class DateGenerator extends Generator<Date> {
    private Date from;
    private int range;

    // TODO create constructor to take two dates
    
    public DateGenerator(Date from, int range) {
        this.from = from;
        this.range = range;
    }
    
    @Override
    public Date generate() {
        int days = deviation(range);
        return from.add(0, 0, days);        
    }
}
