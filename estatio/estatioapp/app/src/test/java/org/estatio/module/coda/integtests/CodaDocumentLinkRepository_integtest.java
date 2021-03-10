package org.estatio.module.coda.integtests;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.coda.dom.CodaDocumentType;
import org.estatio.module.coda.dom.LineType;
import org.estatio.module.coda.dom.codadocument.AmortisationEntryCodaDocumentLineLink;
import org.estatio.module.coda.dom.codadocument.AmortisationScheduleCodaDocumentLineLink;
import org.estatio.module.coda.dom.codadocument.CodaDocument;
import org.estatio.module.coda.dom.codadocument.CodaDocumentLine;
import org.estatio.module.coda.dom.codadocument.CodaDocumentLineRepository;
import org.estatio.module.coda.dom.codadocument.CodaDocumentLinkRepository;
import org.estatio.module.coda.dom.codadocument.CodaDocumentRepository;
import org.estatio.module.coda.dom.codadocument.InvoiceForLeaseCodaDocumentLineLink;
import org.estatio.module.lease.dom.amortisation.AmortisationEntry;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.fixtures.amortisation.enums.AmortisationSchedule_enum;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;

import static org.estatio.module.lease.fixtures.docfrag.enums.DocFragment_demo_enum.InvoiceDescription_DemoGbr;
import static org.estatio.module.lease.fixtures.docfrag.enums.DocFragment_demo_enum.InvoiceItemDescription_DemoGbr;
import static org.estatio.module.lease.fixtures.docfrag.enums.DocFragment_demo_enum.InvoicePreliminaryLetterDescription_DemoGbr;

public class CodaDocumentLinkRepository_integtest extends CodaModuleIntegTestAbstract {

    @Before
    public void setup() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChildren(this,
                        InvoicePreliminaryLetterDescription_DemoGbr,
                        InvoiceDescription_DemoGbr,
                        InvoiceItemDescription_DemoGbr
                );
                executionContext.executeChild(this, InvoiceForLease_enum.OxfPoison003Gb);
                executionContext.executeChild(this, AmortisationSchedule_enum.OxfTopModel);
            }
        });

    }

    @Test
    public void create_and_finders_work_for_invoice_link() throws Exception {

        // given
        InvoiceForLease invoice = InvoiceForLease_enum.OxfPoison003Gb.findUsing(serviceRegistry);

        final CodaDocument doc = codaDocumentRepository
                .findOrCreateForAmortisation(CodaDocumentType.INITIAL_COVID_AMORTISATION, "cmpCode", "docCode",
                        "period", new LocalDate(2020, 1, 1), "atPath");
        final CodaDocumentLine line1 = codaDocumentLineRepository.create(doc, 1, LineType.ANALYSIS);
        final CodaDocumentLine line2 = codaDocumentLineRepository.create(doc, 2, LineType.ANALYSIS);

        Assertions.assertThat(codaDocumentLinkRepository.listAllInvoiceForLeaseLinks()).isEmpty();

        // when
        final InvoiceForLeaseCodaDocumentLineLink link1 = codaDocumentLinkRepository.findOrCreate(invoice, line1);
        final InvoiceForLeaseCodaDocumentLineLink link2 = codaDocumentLinkRepository.findOrCreate(invoice, line2);

        Assertions.assertThat(link1.getCodaDocumentLine()).isEqualTo(line1);
        Assertions.assertThat(link1.getInvoice()).isEqualTo(invoice);
        Assertions.assertThat(link2.getCodaDocumentLine()).isEqualTo(line2);
        Assertions.assertThat(link2.getInvoice()).isEqualTo(invoice);

        Assertions.assertThat(codaDocumentLinkRepository.listAllInvoiceForLeaseLinks()).hasSize(2);
        Assertions.assertThat(codaDocumentLinkRepository.findUnique(invoice, line1)).isEqualTo(link1);
        Assertions.assertThat(codaDocumentLinkRepository.findUnique(invoice, line2)).isEqualTo(link2);
        final List<InvoiceForLeaseCodaDocumentLineLink> byInvoice = codaDocumentLinkRepository.findByInvoice(invoice);
        Assertions.assertThat(byInvoice).hasSize(2);
        Assertions.assertThat(byInvoice).contains(link1);
        Assertions.assertThat(byInvoice).contains(link2);
        final List<InvoiceForLeaseCodaDocumentLineLink> byDocumentLine1 = codaDocumentLinkRepository
                .findInvoiceLinkByDocumentLine(line1);
        Assertions.assertThat(byDocumentLine1).hasSize(1);
        Assertions.assertThat(byDocumentLine1).contains(link1);
        final List<InvoiceForLeaseCodaDocumentLineLink> byDocumentLine2 = codaDocumentLinkRepository
                .findInvoiceLinkByDocumentLine(line2);
        Assertions.assertThat(byDocumentLine2).hasSize(1);
        Assertions.assertThat(byDocumentLine2).contains(link2);

        // when again
        codaDocumentLinkRepository.findOrCreate(invoice, line1);
        codaDocumentLinkRepository.findOrCreate(invoice, line2);
        // then still (idempotent)
        Assertions.assertThat(codaDocumentLinkRepository.listAllInvoiceForLeaseLinks()).hasSize(2);

    }

    @Test
    public void create_and_finders_work_for_schedule_link() throws Exception {

        // given
        final AmortisationSchedule amortisationSchedule = AmortisationSchedule_enum.OxfTopModel.findUsing(serviceRegistry);
        final CodaDocument doc = codaDocumentRepository
                .findOrCreateForAmortisation(CodaDocumentType.INITIAL_COVID_AMORTISATION, "cmpCode", "docCode",
                        "period", new LocalDate(2020, 1, 1), "atPath");
        final CodaDocumentLine line1 = codaDocumentLineRepository.create(doc, 1, LineType.ANALYSIS);
        final CodaDocumentLine line2 = codaDocumentLineRepository.create(doc, 2, LineType.ANALYSIS);

        Assertions.assertThat(codaDocumentLinkRepository.listAllInvoiceForLeaseLinks()).isEmpty();

        // when
        final AmortisationScheduleCodaDocumentLineLink link1 = codaDocumentLinkRepository.findOrCreate(amortisationSchedule, line1);
        final AmortisationScheduleCodaDocumentLineLink link2 = codaDocumentLinkRepository.findOrCreate(amortisationSchedule, line2);

        Assertions.assertThat(link1.getCodaDocumentLine()).isEqualTo(line1);
        Assertions.assertThat(link1.getAmortisationSchedule()).isEqualTo(amortisationSchedule);
        Assertions.assertThat(link2.getCodaDocumentLine()).isEqualTo(line2);
        Assertions.assertThat(link2.getAmortisationSchedule()).isEqualTo(amortisationSchedule);

        Assertions.assertThat(codaDocumentLinkRepository.listAllScheduleLinks()).hasSize(2);
        Assertions.assertThat(codaDocumentLinkRepository.findUnique(amortisationSchedule, line1)).isEqualTo(link1);
        Assertions.assertThat(codaDocumentLinkRepository.findUnique(amortisationSchedule, line2)).isEqualTo(link2);
        final List<AmortisationScheduleCodaDocumentLineLink> byAmortisationSchedule = codaDocumentLinkRepository.findByAmortisationSchedule(amortisationSchedule);
        Assertions.assertThat(byAmortisationSchedule).hasSize(2);
        Assertions.assertThat(byAmortisationSchedule).contains(link1);
        Assertions.assertThat(byAmortisationSchedule).contains(link2);
        final List<AmortisationScheduleCodaDocumentLineLink> byDocumentLine1 = codaDocumentLinkRepository
                .findAmortisationScheduleLinkByDocumentLine(line1);
        Assertions.assertThat(byDocumentLine1).hasSize(1);
        Assertions.assertThat(byDocumentLine1).contains(link1);
        final List<AmortisationScheduleCodaDocumentLineLink> byDocumentLine2 = codaDocumentLinkRepository
                .findAmortisationScheduleLinkByDocumentLine(line2);
        Assertions.assertThat(byDocumentLine2).hasSize(1);
        Assertions.assertThat(byDocumentLine2).contains(link2);

        // when again
        codaDocumentLinkRepository.findOrCreate(amortisationSchedule, line1);
        codaDocumentLinkRepository.findOrCreate(amortisationSchedule, line2);
        // then still (idempotent)
        Assertions.assertThat(codaDocumentLinkRepository.listAllScheduleLinks()).hasSize(2);

    }

    @Test
    public void create_and_finders_work_for_schedule_entry_link() throws Exception {

        // given
        final AmortisationSchedule amortisationSchedule = AmortisationSchedule_enum.OxfTopModel.findUsing(serviceRegistry);
        final AmortisationEntry entry = amortisationSchedule.getEntries().first();
        final CodaDocument doc = codaDocumentRepository
                .findOrCreateForAmortisation(CodaDocumentType.INITIAL_COVID_AMORTISATION, "cmpCode", "docCode",
                        "period", new LocalDate(2020, 1, 1), "atPath");
        final CodaDocumentLine line1 = codaDocumentLineRepository.create(doc, 1, LineType.ANALYSIS);
        final CodaDocumentLine line2 = codaDocumentLineRepository.create(doc, 2, LineType.ANALYSIS);

        Assertions.assertThat(codaDocumentLinkRepository.listAllInvoiceForLeaseLinks()).isEmpty();

        // when
        final AmortisationEntryCodaDocumentLineLink link1 = codaDocumentLinkRepository.findOrCreate(entry, line1);
        final AmortisationEntryCodaDocumentLineLink link2 = codaDocumentLinkRepository.findOrCreate(entry, line2);

        Assertions.assertThat(link1.getCodaDocumentLine()).isEqualTo(line1);
        Assertions.assertThat(link1.getAmortisationEntry()).isEqualTo(entry);
        Assertions.assertThat(link2.getCodaDocumentLine()).isEqualTo(line2);
        Assertions.assertThat(link2.getAmortisationEntry()).isEqualTo(entry);

        Assertions.assertThat(codaDocumentLinkRepository.listAllEntryLinks()).hasSize(2);
        Assertions.assertThat(codaDocumentLinkRepository.findUnique(entry, line1)).isEqualTo(link1);
        Assertions.assertThat(codaDocumentLinkRepository.findUnique(entry, line2)).isEqualTo(link2);
        final List<AmortisationEntryCodaDocumentLineLink> byAmortisationEntry = codaDocumentLinkRepository.findByAmortisationEntry(entry);
        Assertions.assertThat(byAmortisationEntry).hasSize(2);
        Assertions.assertThat(byAmortisationEntry).contains(link1);
        Assertions.assertThat(byAmortisationEntry).contains(link2);
        final List<AmortisationEntryCodaDocumentLineLink> byDocumentLine1 = codaDocumentLinkRepository
                .findAmortisationEntryLinkByDocumentLine(line1);
        Assertions.assertThat(byDocumentLine1).hasSize(1);
        Assertions.assertThat(byDocumentLine1).contains(link1);
        final List<AmortisationEntryCodaDocumentLineLink> byDocumentLine2 = codaDocumentLinkRepository
                .findAmortisationEntryLinkByDocumentLine(line2);
        Assertions.assertThat(byDocumentLine2).hasSize(1);
        Assertions.assertThat(byDocumentLine2).contains(link2);

        // when again
        codaDocumentLinkRepository.findOrCreate(entry, line1);
        codaDocumentLinkRepository.findOrCreate(entry, line2);
        // then still (idempotent)
        Assertions.assertThat(codaDocumentLinkRepository.listAllEntryLinks()).hasSize(2);

    }

    @Inject CodaDocumentLinkRepository codaDocumentLinkRepository;

    @Inject CodaDocumentRepository codaDocumentRepository;

    @Inject CodaDocumentLineRepository codaDocumentLineRepository;

}
