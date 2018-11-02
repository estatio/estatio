package org.estatio.module.capex.restapi;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.value.Blob;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

public class DocumentServiceRestApi_uploadGeneric_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock DocumentTypeRepository mockDocumentTypeRepository;

    @Test
    public void when_using_non_existing_doc_type_data() throws Exception {

        // given
        DocumentServiceRestApi menu = new DocumentServiceRestApi();

        // expect
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No enum constant org.estatio.module.invoice.dom.DocumentTypeData.NON_EXISTING_TYPE");

        // when
        menu.uploadGeneric(null, "NON_EXISTING_TYPE", true, null);

    }

    @Test
    public void when_not_supported() throws Exception {

        // given
        DocumentServiceRestApi menu = new DocumentServiceRestApi();
        Blob blob = new Blob("some_name", "application/pdf", new byte[0]);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockDocumentTypeRepository).findByReference("INCOMING_ORDER");
        }});
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Combination documentType =  INCOMING_ORDER, barcodeInDocName = true and atPath = /ITA is not supported");

        // when
        menu.uploadGeneric(blob, "INCOMING_ORDER", true, "/ITA");

    }
}