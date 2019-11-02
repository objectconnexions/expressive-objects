package uk.co.objectconnexions.expressiveobjects.formats;

import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.value.DateTime;

import static org.hamcrest.Matchers.*;

public class DateFormats {

    private static final Locale UK = Locale.ENGLISH;
    private DateFormat format;

    @Before
    public void prepare() {
        format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, UK);        
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    @Test
    public void sqlDate() {
        Date date = new Date(0);
        String formatedDate = format.format(date);
        assertThat(formatedDate, is("1/1/70 12:00 AM"));;
    }
    
    @Test
    public void sqlTimestamp() {
        Timestamp date = new Timestamp(0);
        String formatedDate = format.format(date);
        assertThat(formatedDate, is("1/1/70 12:00 AM"));;
    }
    
    @Test
    public void eofDateTime() {
        DateTime date = new DateTime(0);
        String formatedDate = format.format(date.calendarValue().getTime());
        assertThat(formatedDate, is("1/1/70 12:00 AM"));;
    }

}
