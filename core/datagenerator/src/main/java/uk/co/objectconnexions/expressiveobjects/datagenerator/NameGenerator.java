package uk.co.objectconnexions.expressiveobjects.datagenerator;

public class NameGenerator extends Generator<String> {
    private String[] options;

    public NameGenerator(String options[]) {
        this.options = options;
    }
    
    @Override
    public String generate() {
        int index = deviation(options.length);
        return options[index];        
    }
}
