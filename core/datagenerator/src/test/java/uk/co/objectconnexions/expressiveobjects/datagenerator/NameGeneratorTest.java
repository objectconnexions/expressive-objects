package uk.co.objectconnexions.expressiveobjects.datagenerator;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

public class NameGeneratorTest {

    private NameGenerator generator;
    protected int index;

    @Before
    public void setUp() throws Exception {
        generator = new NameGenerator(new String[] {"one", "two", "three"}) {
            @Override
            protected int deviation(int range) {
                return index;
            }
        };
    }

    @Test
    public void firstEntry() {
        index = 0;
       assertThat(generator.generate(), is("one"));
    }

    @Test
    public void lastEntry() {
        index = 2;
       assertThat(generator.generate(), is("three"));
    }

}
