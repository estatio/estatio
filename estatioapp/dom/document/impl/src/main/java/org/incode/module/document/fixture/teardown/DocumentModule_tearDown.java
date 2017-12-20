package org.incode.module.document.fixture.teardown;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

public class DocumentModule_tearDown extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"Paperclip\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"Applicability\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"Document\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"DocumentTemplate\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"DocumentAbstract\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"DocumentType\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"RenderingStrategy\"");
    }


    @javax.inject.Inject
    private IsisJdoSupport isisJdoSupport;

}
