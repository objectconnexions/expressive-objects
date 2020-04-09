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

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class CsvFileReader {

    private Map<String, Integer> keys = new HashMap<>();
    private CSVReader csvReader = null;
    private String fileName;
    private String[] record;
    private Iterator<String[]> iterator;
    private int line = 0;
    private char separatorChar;
    private char quoteChar;
    private int skipLines = 0;
    private boolean includesHeaderLine = true;

    public CsvFileReader() {
        this (',', '"');
    }
    
    public CsvFileReader skipLines(int noLines) {
        skipLines = noLines;
        return this;
    }

    public CsvFileReader header(String[] headers) {
        includesHeaderLine = false;
        for (int i = 0; i < headers.length; i++) {
            keys.put(headers[i], i);
        }
        return this;
    }

    public CsvFileReader(char separator, char quote) {
        this.separatorChar = separator;
        this.quoteChar = quote;
    }

    public CsvFileReader loadFile(String filePath) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            return load(reader, filePath);
        } catch (IOException e) {
            throw new DataGenerationException("Could not open file " + filePath);
        }
    }

    public CsvFileReader load(Reader reader, String name) throws IOException {
        this.fileName = name;
        CSVParser parser = new CSVParserBuilder().withSeparator(separatorChar).withQuoteChar(quoteChar).build();
        csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build(); 
        iterator = csvReader.iterator();
        while (skipLines > 0 && hasMore()) {
            next();
            skipLines--;
        }
        if (includesHeaderLine && hasMore()) {
            next();
            for (int i = 0; i < record.length; i++) {
                keys.put(record[i], i);
            }
        }
        
        return this;
    }

    public CsvFileReader loadResource(String path) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
        Reader reader = new InputStreamReader(stream);
        try {
            return load(reader, path);
        } catch (IOException e) {
            throw new DataGenerationException("Could not open file " + path);
        }
    }

    public int getLineNo() {
        return line;
    }
    
    public boolean hasMore() {
        return iterator.hasNext();
    }

    public CsvFileReader next() {
        do {
            record = iterator.next();
            line++;
        } while (record != null && record.length == 0);
        return this;
    }

    public String value(final String column) {
        if (!keys.containsKey(column)) {
            throw new DataGenerationException("No column '" + column + "' in file " + fileName);
        }
        int index = keys.get(column);
        if (index >= record.length) {
            return "";
        }
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
