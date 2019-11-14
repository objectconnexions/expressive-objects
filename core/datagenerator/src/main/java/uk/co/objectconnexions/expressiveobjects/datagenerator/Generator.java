package uk.co.objectconnexions.expressiveobjects.datagenerator;

import java.util.Random;

public abstract class Generator<T> {

    public abstract T generate();

    protected int deviation(int range) {
        Random random = new Random();
        return random.nextInt(range);
    }

}
