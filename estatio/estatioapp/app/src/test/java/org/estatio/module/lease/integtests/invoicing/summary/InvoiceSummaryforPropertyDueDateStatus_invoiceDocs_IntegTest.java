/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.lease.integtests.invoicing.summary;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.base.dom.MimeTypeData;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.comms.CommunicationState;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.base.platform.integtestsupport.RunBackgroundCommandsService;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatusRepository;
import org.estatio.module.lease.dom.invoicing.summary.comms.DocAndCommForInvoiceDoc;
import org.estatio.module.lease.dom.invoicing.summary.comms.DocAndCommForInvoiceDoc_communication;
import org.estatio.module.lease.dom.invoicing.summary.comms.DocAndCommForInvoiceDoc_communicationState;
import org.estatio.module.lease.dom.invoicing.summary.comms.DocAndCommForInvoiceDoc_document;
import org.estatio.module.lease.dom.invoicing.summary.comms.DocAndCommForInvoiceDoc_documentState;
import org.estatio.module.lease.dom.invoicing.summary.comms.InvoiceSummaryForPropertyDueDateStatus_backgroundPrepareInvoiceDocs;
import org.estatio.module.lease.dom.invoicing.summary.comms.InvoiceSummaryForPropertyDueDateStatus_invoiceDocs;
import org.estatio.module.lease.dom.invoicing.summary.comms.InvoiceSummaryForPropertyDueDateStatus_sendByEmailInvoiceDocs;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.lease.seed.DocumentTypesAndTemplatesForLeaseFixture;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceSummaryforPropertyDueDateStatus_invoiceDocs_IntegTest extends LeaseModuleIntegTestAbstract {

    public static class ActionInvocationIntegTest extends
            InvoiceSummaryforPropertyDueDateStatus_invoiceDocs_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext ec) {
                    ec.executeChildren(this,
                            InvoiceForLease_enum.OxfPoison003Gb);
                    ec.executeChild(this, new DocumentTypesAndTemplatesForLeaseFixture());
                }
            });
        }

        @Ignore // EST-1154
        @Test
        public void happy_case() throws Exception {

            // given
            InvoiceSummaryForPropertyDueDateStatus summary = findSummary();
            DocAndCommForInvoiceDoc invoiceDocViewModel = invoiceDocViewModelOf(summary);

            Invoice invoice = invoiceDocViewModel.getInvoice();
            assertThat(invoice).isNotNull();
            assertThat(invoiceDocViewModel.getSendTo()).isNotNull();
            assertThat(invoiceDocViewModel.getSendTo()).isInstanceOf(EmailAddress.class);

            Assertions.assertThat(mixin(DocAndCommForInvoiceDoc_document.class, invoiceDocViewModel).$$()).isNull();
            Assertions.assertThat(mixin(DocAndCommForInvoiceDoc_documentState.class, invoiceDocViewModel).$$()).isNull();
            Assertions.assertThat(mixin(DocAndCommForInvoiceDoc_communication.class, invoiceDocViewModel).$$()).isNull();
            Assertions.assertThat(mixin(DocAndCommForInvoiceDoc_communicationState.class, invoiceDocViewModel).$$()).isNull();

            // when prepare
            mixin(InvoiceSummaryForPropertyDueDateStatus_backgroundPrepareInvoiceDocs.class, summary).$$();

            // (clearing queryResultsCache)
            summary = findSummary();
            invoiceDocViewModel = invoiceDocViewModelOf(summary);

            // then still null
            Document invoiceDoc = mixin(DocAndCommForInvoiceDoc_document.class, invoiceDocViewModel).$$();
            assertThat(invoiceDoc).isNull();


            // given invoiced
            approveAndInvoice(invoice);

            summary = findSummary(InvoiceStatus.INVOICED);
            invoiceDocViewModel = invoiceDocViewModelOf(summary);


            // when prepare
            mixin(InvoiceSummaryForPropertyDueDateStatus_backgroundPrepareInvoiceDocs.class, summary).$$();


            // then now populated
            invoiceDoc = mixin(DocAndCommForInvoiceDoc_document.class, invoiceDocViewModel).$$();
            assertThat(invoiceDoc).isNotNull();

            Assertions.assertThat(mixin(DocAndCommForInvoiceDoc_documentState.class, invoiceDocViewModel).$$()).isEqualTo(DocumentState.NOT_RENDERED);

            Assertions.assertThat(mixin(DocAndCommForInvoiceDoc_communication.class, invoiceDocViewModel).$$()).isNull();
            Assertions.assertThat(mixin(DocAndCommForInvoiceDoc_communicationState.class, invoiceDocViewModel).$$()).isNull();

            // and also
            assertThat(invoiceDoc.getName()).isNotNull();
            assertThat(invoiceDoc.getId()).isNotNull();
            assertThat(invoiceDoc.getCreatedAt()).isNotNull();
            final DocumentType docTypeInvoiceNote = DocumentTypeData.INVOICE.findUsing(documentTypeRepository);
            assertThat(invoiceDoc.getType()).isEqualTo(docTypeInvoiceNote);

            assertThat(invoiceDoc.getState()).isEqualTo(DocumentState.NOT_RENDERED);
            assertThat(invoiceDoc.getRenderedAt()).isNull();
            assertThat(invoiceDoc.getSort()).isEqualTo(DocumentSort.EMPTY);
            assertThat(invoiceDoc.getMimeType()).isEqualTo(MimeTypeData.APPLICATION_PDF.asStr());

            // and also attached to only invoice
            List<Paperclip> paperclips = paperclipRepository.findByDocument(invoiceDoc);
            assertThat(paperclips).hasSize(1);
            assertThat(paperclips).extracting(x -> x.getAttachedTo()).contains(invoice);

            // and when rendered
            runBackgroundCommandsService.runBackgroundCommands();

            summary = findSummary(InvoiceStatus.INVOICED);
            invoiceDocViewModel = invoiceDocViewModelOf(summary);

            // then
            Assertions.assertThat(mixin(DocAndCommForInvoiceDoc_documentState.class, invoiceDocViewModel).$$()).isEqualTo(DocumentState.RENDERED);

            invoiceDoc = mixin(DocAndCommForInvoiceDoc_document.class, invoiceDocViewModel).$$();
            assertThat(invoiceDoc.getState()).isEqualTo(DocumentState.RENDERED);
            assertThat(invoiceDoc.getRenderedAt()).isNotNull();
            assertThat(invoiceDoc.getSort()).isEqualTo(DocumentSort.BLOB);

            //
            // and when send by email
            //
            mixin(InvoiceSummaryForPropertyDueDateStatus_sendByEmailInvoiceDocs.class, summary).$$();

            summary = findSummary(InvoiceStatus.INVOICED);
            invoiceDocViewModel = invoiceDocViewModelOf(summary);

            // then
            final Communication invoiceDocComm = mixin(DocAndCommForInvoiceDoc_communication.class, invoiceDocViewModel).$$();
            assertThat(invoiceDocComm).isNotNull();
            Assertions.assertThat(mixin(DocAndCommForInvoiceDoc_communicationState.class, invoiceDocViewModel).$$()).isEqualTo(CommunicationState.PENDING);

            // and PL doc now also attached to comm, invoice.buyer and invoice.seller (as well as invoice)
            paperclips = paperclipRepository.findByDocument(invoiceDoc);
            assertThat(paperclips).hasSize(4);
            assertThat(paperclips).extracting(x -> x.getAttachedTo()).contains(invoice, invoice.getBuyer(), invoice.getSeller(), invoiceDocComm);

            // and comm attached to PL and also to a new covernote
            paperclips = paperclipRepository.findByAttachedTo(invoiceDocComm);
            assertThat(paperclips).hasSize(2);
            assertThat(paperclips).extracting(x -> x.getDocument()).contains(invoiceDoc);

            final DocumentAbstract invoiceDocAbs = invoiceDoc;
            final Optional<Paperclip> paperclipToCoverNoteIfAny = paperclips.stream().filter(x -> x.getDocument() != invoiceDocAbs) .findFirst();
            assertThat(paperclipToCoverNoteIfAny.isPresent()).isTrue();
            final Paperclip paperclip = paperclipToCoverNoteIfAny.get();
            assertThat(paperclip.getDocument()).isInstanceOf(Document.class);
            final Document coverNote = (Document) paperclip.getDocument();

            // and also cover note is populated
            assertThat(coverNote.getName()).isNotNull();
            assertThat(coverNote.getId()).isNotNull();
            assertThat(coverNote.getCreatedAt()).isNotNull();
            final DocumentType docTypeInvoiceNoteCoverNote = DocumentTypeData.COVER_NOTE_INVOICE.findUsing(documentTypeRepository);
            assertThat(coverNote.getType()).isEqualTo(docTypeInvoiceNoteCoverNote);

            assertThat(coverNote.getState()).isEqualTo(DocumentState.RENDERED);
            assertThat(coverNote.getRenderedAt()).isNotNull();
            assertThat(coverNote.getSort()).isEqualTo(DocumentSort.CLOB);
            assertThat(coverNote.getMimeType()).isEqualTo(MimeTypeData.TEXT_HTML.asStr());

            // and when comm sent
            runBackgroundCommandsService.runBackgroundCommands();

            summary = findSummary(InvoiceStatus.INVOICED);
            invoiceDocViewModel = invoiceDocViewModelOf(summary);

            // then
            Assertions.assertThat(mixin(DocAndCommForInvoiceDoc_communicationState.class, invoiceDocViewModel).$$()).isEqualTo(CommunicationState.SENT);
        }

    }

    //region > helpers

    InvoiceSummaryForPropertyDueDateStatus findSummary() {
        return findSummary(InvoiceStatus.NEW);
    }

    InvoiceSummaryForPropertyDueDateStatus findSummary(final InvoiceStatus invoiceStatus) {

        // clears out queryResultsCache
        transactionService.nextTransaction();

        List<InvoiceSummaryForPropertyDueDateStatus> summaries = invoiceSummaryRepository.findInvoicesByStatus(
                invoiceStatus);
        assertThat(summaries).hasSize(1);
        return summaries.get(0);
    }

    DocAndCommForInvoiceDoc invoiceDocViewModelOf(final InvoiceSummaryForPropertyDueDateStatus summary) {
        List<DocAndCommForInvoiceDoc> viewModels =
                mixin( InvoiceSummaryForPropertyDueDateStatus_invoiceDocs.class, summary).$$();
        assertThat(viewModels).hasSize(1);

        return viewModels.get(0);
    }

    void approveAndInvoice(final Invoice invoice) {
        wrap(mixin(InvoiceForLease._approve.class, invoice)).$$();
        wrap(mixin(InvoiceForLease._invoice.class, invoice)).$$(invoice.getDueDate().minusDays(1));
    }
    //endregion


    @Inject
    ClockService clockService;

    @Inject
    InvoiceSummaryForPropertyDueDateStatusRepository invoiceSummaryRepository;

    @Inject
    PaperclipRepository paperclipRepository;
    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    protected RunBackgroundCommandsService runBackgroundCommandsService;

}
