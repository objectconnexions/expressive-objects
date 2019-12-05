package uk.co.objectconnexions.expressiveobjects.datagenerator;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import uk.co.objectconnexions.expressiveobjects.applib.value.Date;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class DateGeneratorTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        DateTime from = new DateTime(2001, 5, 8, 0, 0, 0, 0);
        DateGenerator generator = new DateGenerator(new Date(from), 0) {
            @Override
            protected int deviation(int range) {
                return 4;
            }
        };
        assertThat(generator.generate(), is(new Date(new DateTime(2001, 5, 8 + 4, 0, 0, 0, 0))));
    }

}
