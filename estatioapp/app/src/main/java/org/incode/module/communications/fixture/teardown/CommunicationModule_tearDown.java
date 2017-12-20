package org.incode.module.communications.fixture.teardown;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

public class CommunicationModule_tearDown extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        isisJdoSupport.executeUpdate("delete from \"IncodeCommunications\".\"CommChannelRole\"");
        isisJdoSupport.executeUpdate("delete from \"IncodeCommunications\".\"PaperclipForCommunication\"");
        isisJdoSupport.executeUpdate("delete from \"IncodeCommunications\".\"Communication\"");

        isisJdoSupport.executeUpdate("delete from \"IncodeCommunications\".\"CommunicationChannelOwnerLink\"");
        isisJdoSupport.executeUpdate("delete from \"IncodeCommunications\".\"CommunicationChannel\"");

    }


    @javax.inject.Inject
    private IsisJdoSupport isisJdoSupport;

}
