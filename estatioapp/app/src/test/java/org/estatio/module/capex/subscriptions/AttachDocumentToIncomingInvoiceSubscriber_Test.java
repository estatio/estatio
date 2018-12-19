package org.estatio.module.capex.subscriptions;

import org.assertj.core.util.Lists;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocLine;
import org.estatio.module.coda.dom.doc.CodaDocLineRepository;
import org.estatio.module.coda.dom.doc.DerivedObjectUpdater;

import static org.assertj.core.api.Assertions.assertThat;

public class AttachDocumentToIncomingInvoiceSubscriber_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    DocumentTypeRepository mockDocumentTypeRepository;

    @Mock
    PaperclipRepository mockPaperclipRepository;

    @Mock
    CodaDocLineRepository mockCodaDocLineRepository;

    @Mock
    IncomingDocumentRepository mockIncomingDocumentRepository;

    DocumentType incomingType;
    DocumentType incomingInvoiceType;

    AttachDocumentToIncomingInvoiceSubscriber subscriber;

    public static class UploadDomainEvent extends AttachDocumentToIncomingInvoiceSubscriber_Test {

        @Before
        public void setUp() throws Exception {
            subscriber = new AttachDocumentToIncomingInvoiceSubscriber();
            subscriber.codaDocLineRepository = mockCodaDocLineRepository;
            subscriber.paperclipRepository = mockPaperclipRepository;
            subscriber.documentTypeRepository = mockDocumentTypeRepository;
            subscriber.incomingDocumentRepository = mockIncomingDocumentRepository;

            incomingType = new DocumentType("INCOMING", "Incoming");
            incomingInvoiceType = new DocumentType("INCOMING_INVOICE", "Incoming Invoice");
        }

        @Test
        public void on_document_upload_single_matching_invoice() {
            // given
            Document document = new Document(incomingType, "/ITA", "2010101234.pdf", "application/pdf", new DateTime());

            CodaDocLine codaDocLine = new CodaDocLine();
            CodaDocHead codaDocHead = new CodaDocHead();
            IncomingInvoice incomingInvoice = new IncomingInvoice();
            codaDocHead.setIncomingInvoice(incomingInvoice);
            codaDocLine.setDocHead(codaDocHead);

            IncomingDocumentRepository.UploadDomainEvent ev = new IncomingDocumentRepository.UploadDomainEvent();
            ev.setReturnValue(document);
            ev.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockDocumentTypeRepository).findByReference(incomingInvoiceType.getReference());
                will(returnValue(incomingInvoiceType));
                oneOf(mockCodaDocLineRepository).findByUserRef1("2010101234");
                will(returnValue(Lists.newArrayList(codaDocLine)));
                oneOf(mockPaperclipRepository).attach(document, null, incomingInvoice);
            }});

            // when
            subscriber.on(ev);

            // then
            assertThat(document.getType()).isEqualTo(incomingInvoiceType);
        }

        @Test
        public void on_document_upload_multiple_matching_invoices() {
            // given
            Document document = new Document(incomingType, "/ITA", "2010101234.pdf", "application/pdf", new DateTime());

            CodaDocLine codaDocLine1 = new CodaDocLine();
            CodaDocHead codaDocHead1 = new CodaDocHead();
            IncomingInvoice incomingInvoice1 = new IncomingInvoice();
            codaDocHead1.setIncomingInvoice(incomingInvoice1);
            codaDocLine1.setDocHead(codaDocHead1);
            CodaDocLine codaDocLine2 = new CodaDocLine();
            CodaDocHead codaDocHead2 = new CodaDocHead();
            IncomingInvoice incomingInvoice2 = new IncomingInvoice();
            codaDocHead2.setIncomingInvoice(incomingInvoice2);
            codaDocLine2.setDocHead(codaDocHead2);

            IncomingDocumentRepository.UploadDomainEvent ev = new IncomingDocumentRepository.UploadDomainEvent();
            ev.setReturnValue(document);
            ev.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockDocumentTypeRepository).findByReference(incomingInvoiceType.getReference());
                will(returnValue(incomingInvoiceType));
                oneOf(mockCodaDocLineRepository).findByUserRef1("2010101234");
                will(returnValue(Lists.newArrayList(codaDocLine1, codaDocLine2)));
                oneOf(mockPaperclipRepository).attach(document, null, incomingInvoice1);
                oneOf(mockPaperclipRepository).attach(document, null, incomingInvoice2);
            }});

            // when
            subscriber.on(ev);

            // then
            assertThat(document.getType()).isEqualTo(incomingInvoiceType);
        }

        @Test
        public void on_document_upload_no_matching_invoice() {
            // given
            Document document = new Document(incomingType, "/ITA", "2010101234.pdf", "application/pdf", new DateTime());

            IncomingDocumentRepository.UploadDomainEvent ev = new IncomingDocumentRepository.UploadDomainEvent();
            ev.setReturnValue(document);
            ev.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockDocumentTypeRepository).findByReference(incomingInvoiceType.getReference());
                will(returnValue(incomingInvoiceType));
                oneOf(mockCodaDocLineRepository).findByUserRef1("2010101234");
                will(returnValue(Lists.emptyList()));
            }});

            // when
            subscriber.on(ev);

            // then
            assertThat(document.getType()).isEqualTo(incomingType);
        }
    }

    public static class UpsertIncomingInvoiceEvent extends AttachDocumentToIncomingInvoiceSubscriber_Test {

        @Before
        public void setUp() throws Exception {
            subscriber = new AttachDocumentToIncomingInvoiceSubscriber();
            subscriber.codaDocLineRepository = mockCodaDocLineRepository;
            subscriber.paperclipRepository = mockPaperclipRepository;
            subscriber.documentTypeRepository = mockDocumentTypeRepository;
            subscriber.incomingDocumentRepository = mockIncomingDocumentRepository;

            incomingType = new DocumentType("INCOMING", "Incoming");
            incomingInvoiceType = new DocumentType("INCOMING_INVOICE", "Incoming Invoice");
        }

        @Test
        public void on_invoice_upsert_single_matching_document() {
            // given
            IncomingInvoice incomingInvoice = new IncomingInvoice();
            Document document = new Document(incomingType, "/ITA", "2010101234.pdf", "application/pdf", new DateTime());
            CodaDocLine codaDocLine = new CodaDocLine();
            codaDocLine.setUserRef1("2010101234");

            DerivedObjectUpdater.UpsertIncomingInvoiceEvent ev = new DerivedObjectUpdater.UpsertIncomingInvoiceEvent();
            ev.setArguments(Lists.newArrayList(codaDocLine));
            ev.setReturnValue(incomingInvoice);
            ev.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockDocumentTypeRepository).findByReference(incomingInvoiceType.getReference());
                will(returnValue(incomingInvoiceType));
                oneOf(mockIncomingDocumentRepository).findAllIncomingDocumentsByName("2010101234");
                will(returnValue(Lists.newArrayList(document)));
                oneOf(mockPaperclipRepository).attach(document, null, incomingInvoice);
            }});

            // when
            subscriber.on(ev);

            // then
            assertThat(document.getType()).isEqualTo(incomingInvoiceType);
        }

        @Test
        public void on_invoice_upsert_multiple_matching_documents() {
            // given
            IncomingInvoice incomingInvoice = new IncomingInvoice();

            Document document1 = new Document(incomingType, "/ITA", "2010101234.pdf", "application/pdf", new DateTime());
            Document document2 = new Document(incomingType, "/ITA", "2010101234.pdf", "application/pdf", new DateTime());
            CodaDocLine codaDocLine1 = new CodaDocLine();
            codaDocLine1.setUserRef1("2010101234");

            DerivedObjectUpdater.UpsertIncomingInvoiceEvent ev = new DerivedObjectUpdater.UpsertIncomingInvoiceEvent();
            ev.setArguments(Lists.newArrayList(codaDocLine1));
            ev.setReturnValue(incomingInvoice);
            ev.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockDocumentTypeRepository).findByReference(incomingInvoiceType.getReference());
                will(returnValue(incomingInvoiceType));
                oneOf(mockIncomingDocumentRepository).findAllIncomingDocumentsByName("2010101234");
                will(returnValue(Lists.newArrayList(document1, document2)));
                oneOf(mockPaperclipRepository).attach(document1, null, incomingInvoice);
                oneOf(mockPaperclipRepository).attach(document2, null, incomingInvoice);
            }});

            // when
            subscriber.on(ev);

            // then
            assertThat(document1.getType()).isEqualTo(incomingInvoiceType);
            assertThat(document2.getType()).isEqualTo(incomingInvoiceType);
        }

        @Test
        public void on_invoice_upsert_no_matching_document() {
            // given
            IncomingInvoice incomingInvoice = new IncomingInvoice();

            CodaDocLine codaDocLine = new CodaDocLine();
            codaDocLine.setUserRef1("2010101234");

            DerivedObjectUpdater.UpsertIncomingInvoiceEvent ev = new DerivedObjectUpdater.UpsertIncomingInvoiceEvent();
            ev.setArguments(Lists.newArrayList(codaDocLine));
            ev.setReturnValue(incomingInvoice);
            ev.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockDocumentTypeRepository).findByReference(incomingInvoiceType.getReference());
                will(returnValue(incomingInvoiceType));
                oneOf(mockIncomingDocumentRepository).findAllIncomingDocumentsByName("2010101234");
                will(returnValue(Lists.emptyList()));
            }});

            // when
            subscriber.on(ev);
        }
    }
}