package org.incode.platform.dom.communications.integtests.dom.communications.fixture;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.incode.platform.dom.communications.integtests.dom.communications.fixture.data.democust2.DemoObjectWithNote_and_DemoInvoice_create3;
import org.incode.platform.dom.communications.integtests.dom.communications.fixture.data.doctypes.DocumentType_and_DocumentTemplates_createSome;

public class DemoObjectWithNotes_and_DemoInvoice_and_docs_and_comms_create extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        executionContext.executeChild(this, new DocumentType_and_DocumentTemplates_createSome());
    	queryResultsCache.resetForNextTransaction();

    	executionContext.executeChild(this, new DemoObjectWithNote_and_DemoInvoice_create3());
    }

    @Inject
    QueryResultsCache queryResultsCache;

}
