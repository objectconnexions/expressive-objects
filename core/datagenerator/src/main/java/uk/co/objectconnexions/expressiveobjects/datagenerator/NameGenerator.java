package uk.co.objectconnexions.expressiveobjects.datagenerator;

import java.util.ArrayList;
import java.util.List;

public class NameGenerator extends Generator<String> {
    final private List<String> options = new ArrayList<>();
    final private boolean unique;

    public NameGenerator() {
        unique = false;
    }

    public NameGenerator(final boolean unique) {
        this.unique = unique;
    }

    public NameGenerator(final String... options) {
        this(false, options);
    }

    public NameGenerator(final boolean unique, final String options[]) {
        this.unique = unique;
        for (String name : options) {
            add(name);
        }
    }

    public void add(final String name) {
        options.add(name);
    }

    @Override
    public String generate() {
        int index = deviation(options.size());
        if (index >= options.size()) {
            throw new DataGenerationException("Now more elements available");
        }
        String element = options.get(index);
        if (unique) {
            options.remove(element);
        }
        return element;
    }
}
