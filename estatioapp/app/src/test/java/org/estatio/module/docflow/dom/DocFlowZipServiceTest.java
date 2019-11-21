package org.estatio.module.docflow.dom;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.api.DocumentService;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.invoice.dom.DocumentTypeData;

public class DocFlowZipServiceTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    DocFlowZipFactory mockDocFlowZipFactory;

    @Mock
    DocFlowZipRepository mockDocFlowZipRepository;

    @Mock
    DocumentTypeRepository mockDocumentTypeRepository;

    @Mock
    DocumentService mockDocumentService;

    @Test
    public void handle_assigns_sdi_number_to_attached_pdf_generated_from_xml() {

        // given
        final Long sdId = Long.valueOf(12345678);
        final String expectedDocumentName = "S12345678.pdf";

        DocFlowZipService service = new DocFlowZipService();
        service.docFlowZipFactory = mockDocFlowZipFactory;
        service.docFlowZipRepository = mockDocFlowZipRepository;
        service.documentTypeRepository = mockDocumentTypeRepository;
        service.documentService = mockDocumentService;

        final String atPath = "/ITA";
        final String sha256 = "someSHA";
        final DocFlowZip docFlowZip = new DocFlowZip(sdId, atPath, sha256);
        final DocumentType documentTypeForXml = new DocumentType(null, null);
        final DocumentType documentTypeForPdf = new DocumentType(null, null);
        char[] chars =  new char[0];
        final Clob xml = new Clob("xml-file-name.xml", "application/xml", chars);
        final Blob pdf = new Blob("pdf-file-name.pdf", "application/pdf", new byte[0]);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockDocFlowZipFactory).createTransient(sdId, atPath, sha256);
            will(returnValue(docFlowZip));
            oneOf(mockDocFlowZipRepository).findBySdiId(sdId);
            will(returnValue(null));
            oneOf(mockDocFlowZipRepository).persist(docFlowZip);
            oneOf(mockDocumentTypeRepository).findByReference(DocumentTypeData.DOCFLOW_FATURRA_XML.getRef());
            will(returnValue(documentTypeForXml));
            oneOf(mockDocumentService).createAndAttachDocumentForClob(documentTypeForXml, atPath, xml.getName(), xml, null, docFlowZip);
            oneOf(mockDocumentTypeRepository).findByReference(DocumentTypeData.INCOMING_INVOICE.getRef());
            will(returnValue(documentTypeForPdf));
            oneOf(mockDocumentService).createAndAttachDocumentForBlob(documentTypeForPdf, atPath, expectedDocumentName, pdf, "generated from xml", docFlowZip);
        }});

        // when
        service.handle(sdId, null, xml, pdf, null, null, atPath, sha256);

    }

}