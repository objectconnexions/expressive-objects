package uk.co.objectconnexions.expressiveobjects.datagenerator;

public class CreditCardGenerator extends Generator<String> {
    private int range;

    public CreditCardGenerator(int range) {
        this.range = range;
    }

    @Override
    public String generate() {
        StringBuffer number = new StringBuffer();
        number.append(deviation(4) + 3);
        for (int i = 0; i < range - 1; i++) {
            number.append(deviation(10));
        }
        return number.toString();
    }
}
