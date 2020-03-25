package uk.co.objectconnexions.expressiveobjects.datagenerator;

import java.util.ArrayList;
import java.util.List;

public class ListGenerator<T> extends Generator<T> {
    final private List<T> options;

    public ListGenerator(final List<T> options) {
        this.options = options;
    }

    public ListGenerator(final T options[]) {
        this.options = new ArrayList<T>();
        for (T t : options) {
            add(t);
        }
    }

    public void add(final T name) {
        options.add(name);
    }

    @Override
    public T generate() {
        int size = options.size();
        if (size < 1) throw new DataGenerationException("There are no entries in the data generator");
        int index = deviation(size);
        T element = options.get(index);
        return element;
    }
}
