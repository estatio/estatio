package org.estatio.module.capex.restapi;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.value.Blob;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.MimeTypeData;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

public class DocumentServiceRestApi_uploadGeneric_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock DocumentTypeRepository mockDocumentTypeRepository;

    DocumentServiceRestApi service;

    @Before
    public void setUp() throws Exception {
        service = new DocumentServiceRestApi();
        service.documentTypeRepository = mockDocumentTypeRepository;
    }

    @Test
    public void when_using_non_existing_doc_type_data() throws Exception {

        // expect
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No enum constant org.estatio.module.invoice.dom.DocumentTypeData.NON_EXISTING_TYPE");

        // when
        service.uploadGeneric(null, "NON_EXISTING_TYPE", null);

    }

    @Test
    public void when_using_unsupported_doc_type_data() throws Exception {

        // given
        final Blob blob = new Blob("Foo", MimeTypeData.APPLICATION_PDF.asStr(), new byte[20]);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockDocumentTypeRepository).findByReference("INCOMING_ORDER");
        }});
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("DocumentType INCOMING_ORDER is not supported");

        // when
        service.uploadGeneric(blob, "INCOMING_ORDER", "/ITA");

    }

}