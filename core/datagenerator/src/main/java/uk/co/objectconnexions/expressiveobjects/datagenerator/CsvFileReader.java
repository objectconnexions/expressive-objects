package uk.co.objectconnexions.expressiveobjects.datagenerator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.opencsv.CSVReader;

public class CsvFileReader {

    public void read(String fileName) {
        CSVReader csvReader = null;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(fileName));
            csvReader = new CSVReader(reader);

            // Reading Records One by One in a String array
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                System.out.println("Name : " + nextRecord[0]);
                System.out.println("Email : " + nextRecord[1]);
                System.out.println("Phone : " + nextRecord[2]);
                System.out.println("Country : " + nextRecord[3]);
                System.out.println("==========================");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not open file " + fileName);
        } catch (IOException e) {
            throw new RuntimeException("Could read CSV file " + fileName, e);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException e) {
                    throw new RuntimeException("Could not close file " + fileName, e);
                }
            }
        }
    }
}
