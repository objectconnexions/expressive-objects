#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package service.example;

import java.util.List;

import dom.example.Example;

import uk.co.objectconnexions.expressiveobjects.applib.AbstractFactoryAndRepository;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.ActionSemantics;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.ActionSemantics.Of;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.MemberOrder;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Named;

@Named("Example Repo")
public class Repository extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "examples";
    }

    public String iconName() {
        return "Example";
    }
    // }}

    // {{ AllInstances
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public List<Example> allInstances() {
        return allInstances(Example.class);
    }
    // }}

    // {{ newToDo  (action)
    @MemberOrder(sequence = "2")
    public Example newExample(
            @Named("Description") String description) {
        final Example example = newTransientInstance(Example.class);
        example.setDescription(description);
        persist(example);
        return example;
    }
    // }}

}
