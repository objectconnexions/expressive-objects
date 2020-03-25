package uk.co.objectconnexions.expressiveobjects.datagenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.opencsv.CSVReader;

public class CsvFileReader {

    private Map<String, Integer> keys = new HashMap<>();
    private CSVReader csvReader = null;
    private String fileName;
    private String[] record;
    private Iterator<String[]> iterator;

    public CsvFileReader loadFile(String filePath) {
        this.fileName = filePath;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            load(reader, filePath);
        } catch (IOException e) {
            throw new DataGenerationException("Could not open file " + filePath);
        }

        return this;
    }

    private void load(Reader reader, String path) {
        csvReader = new CSVReader(reader);

        String[] firstRecord;
        iterator = csvReader.iterator();
        if (iterator.hasNext()) {

        }
        firstRecord = iterator.next();
        for (int i = 0; i < firstRecord.length; i++) {
            keys.put(firstRecord[i], i);
        }
        next();
    }

    public CsvFileReader loadResource(String path) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
        Reader reader = new InputStreamReader(stream);
        load(reader, path);
        return this;
    }

    public boolean hasMore() {
        return iterator.hasNext();
    }

    public CsvFileReader next() {
        record = iterator.next();
        return this;
    }

    public String value(final String column) {
        if (!keys.containsKey(column)) {
            throw new DataGenerationException("No column '" + column + "' in file " + fileName);
        }
        int index = keys.get(column);
        return record[index ];
    }

    public int intValue(final String column) {
        String value = value(column);
        return Integer.valueOf(value).intValue();
    }

    public void close() {
        if (csvReader != null) {
            try {
                csvReader.close();
            } catch (IOException e) {
                throw new DataGenerationException("Could not close file " + fileName, e);
            }
        }
    }
}
