#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package fixture;

import uk.co.objectconnexions.expressiveobjects.applib.fixtures.LogonFixture;

public class LogonUser extends LogonFixture {

    public LogonUser() {
        super("sven");
    }

}
