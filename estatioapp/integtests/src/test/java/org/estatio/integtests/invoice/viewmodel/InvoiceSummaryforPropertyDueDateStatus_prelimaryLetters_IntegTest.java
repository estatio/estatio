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
package org.estatio.integtests.invoice.viewmodel;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

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

import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatusRepository;
import org.estatio.dom.lease.invoicing.viewmodel.dnc.DocAndCommForPrelimLetter;
import org.estatio.dom.lease.invoicing.viewmodel.dnc.DocAndCommForPrelimLetter_communication;
import org.estatio.dom.lease.invoicing.viewmodel.dnc.DocAndCommForPrelimLetter_communicationState;
import org.estatio.dom.lease.invoicing.viewmodel.dnc.DocAndCommForPrelimLetter_document;
import org.estatio.dom.lease.invoicing.viewmodel.dnc.DocAndCommForPrelimLetter_documentState;
import org.estatio.dom.lease.invoicing.viewmodel.dnc.InvoiceSummaryForPropertyDueDateStatus_preliminaryLetters;
import org.estatio.dom.lease.invoicing.viewmodel.dnc.InvoiceSummaryForPropertyDueDateStatus_backgroundPreparePreliminaryLetters;
import org.estatio.dom.lease.invoicing.viewmodel.dnc.InvoiceSummaryForPropertyDueDateStatus_sendByEmailPreliminaryLetters;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixturescripts.DocumentTypesAndTemplatesFixture;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceSummaryforPropertyDueDateStatus_prelimaryLetters_IntegTest extends EstatioIntegrationTest {

    public static class ActionInvocationIntegTest extends
            InvoiceSummaryforPropertyDueDateStatus_prelimaryLetters_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());

                    executionContext.executeChild(this, new DocumentTypesAndTemplatesFixture());
                }
            });
        }

        @Ignore // EST-1154
        @Test
        public void happy_case() throws Exception {

            // given
            InvoiceSummaryForPropertyDueDateStatus summary = findSummary();
            DocAndCommForPrelimLetter prelimLetterViewModel = prelimLetterViewModelOf(summary);

            Invoice invoice = prelimLetterViewModel.getInvoice();
            assertThat(invoice).isNotNull();
            assertThat(prelimLetterViewModel.getSendTo()).isNotNull();
            assertThat(prelimLetterViewModel.getSendTo()).isInstanceOf(EmailAddress.class);

            assertThat(mixin(DocAndCommForPrelimLetter_document.class, prelimLetterViewModel).$$()).isNull();
            assertThat(mixin(DocAndCommForPrelimLetter_documentState.class, prelimLetterViewModel).$$()).isNull();
            assertThat(mixin(DocAndCommForPrelimLetter_communication.class, prelimLetterViewModel).$$()).isNull();
            assertThat(mixin(DocAndCommForPrelimLetter_communicationState.class, prelimLetterViewModel).$$()).isNull();

            // when prepare
            mixin(InvoiceSummaryForPropertyDueDateStatus_backgroundPreparePreliminaryLetters.class, summary).$$();

            // (clearing queryResultsCache)
            summary = findSummary();
            prelimLetterViewModel = prelimLetterViewModelOf(summary);

            // then
            Document prelimLetterDoc = mixin(DocAndCommForPrelimLetter_document.class, prelimLetterViewModel).$$();
            assertThat(prelimLetterDoc).isNotNull();
            assertThat(mixin(DocAndCommForPrelimLetter_documentState.class, prelimLetterViewModel).$$()).isEqualTo(DocumentState.NOT_RENDERED);

            assertThat(mixin(DocAndCommForPrelimLetter_communication.class, prelimLetterViewModel).$$()).isNull();
            assertThat(mixin(DocAndCommForPrelimLetter_communicationState.class, prelimLetterViewModel).$$()).isNull();

            // and also
            assertThat(prelimLetterDoc.getName()).isNotNull();
            assertThat(prelimLetterDoc.getId()).isNotNull();
            assertThat(prelimLetterDoc.getCreatedAt()).isNotNull();
            final DocumentType docTypePrelimLetter =  DocumentTypeData.PRELIM_LETTER.findUsing(documentTypeRepository);
            assertThat(prelimLetterDoc.getType()).isEqualTo(docTypePrelimLetter);

            assertThat(prelimLetterDoc.getState()).isEqualTo(DocumentState.NOT_RENDERED);
            assertThat(prelimLetterDoc.getRenderedAt()).isNull();
            assertThat(prelimLetterDoc.getSort()).isEqualTo(DocumentSort.EMPTY);
            assertThat(prelimLetterDoc.getMimeType()).isEqualTo("application/pdf");

            // and also attached to only invoice
            List<Paperclip> paperclips = paperclipRepository.findByDocument(prelimLetterDoc);
            assertThat(paperclips).hasSize(1);
            assertThat(paperclips).extracting(x -> x.getAttachedTo()).contains(invoice);

            // and when rendered
            runBackgroundCommandsService.runBackgroundCommands();

            summary = findSummary();
            prelimLetterViewModel = prelimLetterViewModelOf(summary);

            // then
            assertThat(mixin(DocAndCommForPrelimLetter_documentState.class, prelimLetterViewModel).$$()).isEqualTo(DocumentState.RENDERED);

            prelimLetterDoc = mixin(DocAndCommForPrelimLetter_document.class, prelimLetterViewModel).$$();
            assertThat(prelimLetterDoc.getState()).isEqualTo(DocumentState.RENDERED);
            assertThat(prelimLetterDoc.getRenderedAt()).isNotNull();
            assertThat(prelimLetterDoc.getSort()).isEqualTo(DocumentSort.BLOB);

            //
            // and when send by email
            //
            mixin(InvoiceSummaryForPropertyDueDateStatus_sendByEmailPreliminaryLetters.class, summary).$$();

            summary = findSummary();
            prelimLetterViewModel = prelimLetterViewModelOf(summary);

            // then
            final Communication prelimLetterComm = mixin(DocAndCommForPrelimLetter_communication.class, prelimLetterViewModel).$$();
            assertThat(prelimLetterComm).isNotNull();
            assertThat(mixin(DocAndCommForPrelimLetter_communicationState.class, prelimLetterViewModel).$$()).isEqualTo(CommunicationState.PENDING);

            // and PL doc now also attached to comm, invoice.buyer and invoice.seller (as well as invoice)
            paperclips = paperclipRepository.findByDocument(prelimLetterDoc);
            assertThat(paperclips).hasSize(4);
            assertThat(paperclips).extracting(x -> x.getAttachedTo()).contains(invoice, invoice.getBuyer(), invoice.getSeller(), prelimLetterComm);

            // and comm attached to PL and also to a new covernote
            paperclips = paperclipRepository.findByAttachedTo(prelimLetterComm);
            assertThat(paperclips).hasSize(2);
            assertThat(paperclips).extracting(x -> x.getDocument()).contains(prelimLetterDoc);

            final DocumentAbstract prelimLetterDocF = prelimLetterDoc;
            final Optional<Paperclip> paperclipToCoverNoteIfAny = paperclips.stream().filter(x -> x.getDocument() != prelimLetterDocF) .findFirst();
            assertThat(paperclipToCoverNoteIfAny.isPresent()).isTrue();
            final Paperclip paperclip = paperclipToCoverNoteIfAny.get();
            assertThat(paperclip.getDocument()).isInstanceOf(Document.class);
            final Document coverNote = (Document) paperclip.getDocument();

            // and also cover note is populated
            assertThat(coverNote.getName()).isNotNull();
            assertThat(coverNote.getId()).isNotNull();
            assertThat(coverNote.getCreatedAt()).isNotNull();
            final DocumentType docTypePrelimLetterCoverNote =
                    DocumentTypeData.COVER_NOTE_PRELIM_LETTER.findUsing(documentTypeRepository);
            assertThat(coverNote.getType()).isEqualTo(docTypePrelimLetterCoverNote);

            assertThat(coverNote.getState()).isEqualTo(DocumentState.RENDERED);
            assertThat(coverNote.getRenderedAt()).isNotNull();
            assertThat(coverNote.getSort()).isEqualTo(DocumentSort.CLOB);
            assertThat(coverNote.getMimeType()).isEqualTo("text/html");

            // and when comm sent
            runBackgroundCommandsService.runBackgroundCommands();

            summary = findSummary();
            prelimLetterViewModel = prelimLetterViewModelOf(summary);

            // then
            assertThat(mixin(DocAndCommForPrelimLetter_communicationState.class, prelimLetterViewModel).$$()).isEqualTo(CommunicationState.SENT);
        }

    }


    InvoiceSummaryForPropertyDueDateStatus findSummary() {

        // clears out queryResultsCache
        transactionService.nextTransaction();

        List<InvoiceSummaryForPropertyDueDateStatus> summaries = invoiceSummaryRepository.findInvoicesByStatus(InvoiceStatus.NEW);
        assertThat(summaries).hasSize(1);
        return summaries.get(0);
    }

    DocAndCommForPrelimLetter prelimLetterViewModelOf(final InvoiceSummaryForPropertyDueDateStatus summary) {
        List<DocAndCommForPrelimLetter> viewModels =
                mixin( InvoiceSummaryForPropertyDueDateStatus_preliminaryLetters.class, summary).$$();
        assertThat(viewModels).hasSize(1);

        return viewModels.get(0);
    }


    @Inject
    InvoiceSummaryForPropertyDueDateStatusRepository invoiceSummaryRepository;

    @Inject
    PaperclipRepository paperclipRepository;
    @Inject
    DocumentTypeRepository documentTypeRepository;
}
