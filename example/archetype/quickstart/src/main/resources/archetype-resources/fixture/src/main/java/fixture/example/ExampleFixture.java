#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package fixture.example;

import dom.example.Example;

import uk.co.objectconnexions.expressiveobjects.applib.fixtures.AbstractFixture;

public class ExampleFixture extends AbstractFixture {

    @Override
    public void install() {
        addExample("Object one");
        addExample("Object two");
        addExample("Object three");
    }

    private void addExample(String description) {
        Example example = newTransientInstance(Example.class);
        example.setDescription(description);
        persist(example);
    }

}
