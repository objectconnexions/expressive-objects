package uk.co.objectconnexions.expressiveobjects.datagenerator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class NameGeneratorTest {

    private NameGenerator generator;
    protected int index;

    @Before
    public void setUp() throws Exception {
        boolean unique = false;
        createGenerator(unique);
    }

    private void createGenerator(boolean unique) {
        generator = new NameGenerator(unique, new String[] {"one", "two", "three"}) {
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
        assertThat(generator.generate(), is("one"));
    }

    @Test
    public void lastEntry() {
        index = 2;
        assertThat(generator.generate(), is("three"));
        assertThat(generator.generate(), is("three"));
    }

    @Test(expected = DataGenerationException.class)
    public void noMoreEntries() {
        index = 3;
        assertThat(generator.generate(), is("three"));
    }

    @Test
    public void uniqueEntrie() {
        createGenerator(true);
        index = 0;
        assertThat(generator.generate(), is("one"));
        assertThat(generator.generate(), is("two"));
        assertThat(generator.generate(), is("three"));
    }

}
