package uk.co.objectconnexions.expressiveobjects.datagenerator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class CsvFileReaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testFileFromFileSystem() {
        CsvFileReader read = new CsvFileReader().loadFile("src/test/resources/test.csv");
        read.next();
        assertThat(read.value("name"), is("Fred Smith"));
        assertThat(read.value("phone"), is("0123456789"));
    }

    @Test
    public void testFileFromClasspath() {
        CsvFileReader read = new CsvFileReader().loadResource("test.csv");
        read.next();
        assertThat(read.value("name"), is("Fred Smith"));
        assertThat(read.value("phone"), is("0123456789"));
    }

}
