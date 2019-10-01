package uk.co.objectconnexions.expressiveobjects.datagenerator;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class CsvFileReaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        System.out.println(new File(".").getAbsolutePath());
        
        new CsvFileReader().read("src/test/resources/test.csv");
    }

}
