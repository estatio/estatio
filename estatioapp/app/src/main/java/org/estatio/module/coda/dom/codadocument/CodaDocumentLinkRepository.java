package org.estatio.module.coda.dom.codadocument;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.lease.dom.amortisation.AmortisationEntry;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "codadocument.CodaDocumentLinkRepository"
)
public class CodaDocumentLinkRepository {

    @Programmatic
    public List<AmortisationScheduleCodaDocumentLineLink> listAllScheduleLinks() {
        return repositoryService.allInstances(AmortisationScheduleCodaDocumentLineLink.class);
    }

    @Programmatic
    public List<AmortisationEntryCodaDocumentLineLink> listAllEntryLinks() {
        return repositoryService.allInstances(AmortisationEntryCodaDocumentLineLink.class);
    }

    @Programmatic
    public List<InvoiceForLeaseCodaDocumentLineLink> listAllInvoiceForLeaseLinks() {
        return repositoryService.allInstances(InvoiceForLeaseCodaDocumentLineLink.class);
    }

    @Programmatic
    public List<AmortisationScheduleCodaDocumentLineLink> findByAmortisationSchedule(final AmortisationSchedule amortisationSchedule) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationScheduleCodaDocumentLineLink.class,
                        "findByAmortisationSchedule",
                        "amortisationSchedule", amortisationSchedule));
    }

    @Programmatic
    public List<AmortisationScheduleCodaDocumentLineLink> findAmortisationScheduleLinkByDocumentLine(final CodaDocumentLine codaDocumentLine) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationScheduleCodaDocumentLineLink.class,
                        "findByDocumentLine",
                        "codaDocumentLine", codaDocumentLine));
    }

    @Programmatic
    public AmortisationScheduleCodaDocumentLineLink findUnique(final AmortisationSchedule amortisationSchedule, final CodaDocumentLine codaDocumentLine){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        AmortisationScheduleCodaDocumentLineLink.class,
                        "findUnique",
                        "codaDocumentLine", codaDocumentLine,
                        "amortisationSchedule", amortisationSchedule));
    }

    @Programmatic
    public AmortisationScheduleCodaDocumentLineLink findOrCreate(final AmortisationSchedule amortisationSchedule, final CodaDocumentLine codaDocumentLine){
        AmortisationScheduleCodaDocumentLineLink result = findUnique(amortisationSchedule, codaDocumentLine);
        if (result ==null) {
            result = create(amortisationSchedule, codaDocumentLine);
        }
        return result;
    }

    private AmortisationScheduleCodaDocumentLineLink create(final AmortisationSchedule schedule, final CodaDocumentLine codaDocumentLine){
        AmortisationScheduleCodaDocumentLineLink link = new AmortisationScheduleCodaDocumentLineLink(schedule, codaDocumentLine);
        repositoryService.persistAndFlush(link);
        return link;
    }

    @Programmatic
    public List<AmortisationEntryCodaDocumentLineLink> findByAmortisationEntry(final AmortisationEntry amortisationEntry) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationEntryCodaDocumentLineLink.class,
                        "findByEntry",
                        "amortisationEntry", amortisationEntry));
    }

    @Programmatic
    public List<AmortisationEntryCodaDocumentLineLink> findAmortisationEntryLinkByDocumentLine(final CodaDocumentLine codaDocumentLine) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationEntryCodaDocumentLineLink.class,
                        "findByDocumentLine",
                        "codaDocumentLine", codaDocumentLine));
    }

    @Programmatic
    public AmortisationEntryCodaDocumentLineLink findUnique(final AmortisationEntry amortisationEntry, final CodaDocumentLine codaDocumentLine){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        AmortisationEntryCodaDocumentLineLink.class,
                        "findUnique",
                        "codaDocumentLine", codaDocumentLine,
                        "amortisationEntry", amortisationEntry));
    }

    @Programmatic
    public AmortisationEntryCodaDocumentLineLink findOrCreate(final AmortisationEntry amortisationEntry, final CodaDocumentLine codaDocumentLine){
        AmortisationEntryCodaDocumentLineLink result = findUnique(amortisationEntry, codaDocumentLine);
        if (result ==null) {
            result = create(amortisationEntry, codaDocumentLine);
        }
        return result;
    }

    private AmortisationEntryCodaDocumentLineLink create(final AmortisationEntry amortisationEntry, final CodaDocumentLine codaDocumentLine){
        AmortisationEntryCodaDocumentLineLink link = new AmortisationEntryCodaDocumentLineLink(amortisationEntry, codaDocumentLine);
        repositoryService.persistAndFlush(link);
        return link;
    }

    @Programmatic
    public List<InvoiceForLeaseCodaDocumentLineLink> findByInvoice(final InvoiceForLease invoice) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        InvoiceForLeaseCodaDocumentLineLink.class,
                        "findByInvoice",
                        "invoice", invoice));
    }

    @Programmatic
    public List<InvoiceForLeaseCodaDocumentLineLink> findInvoiceLinkByDocumentLine(final CodaDocumentLine codaDocumentLine) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        InvoiceForLeaseCodaDocumentLineLink.class,
                        "findByDocumentLine",
                        "codaDocumentLine", codaDocumentLine));
    }

    @Programmatic
    public InvoiceForLeaseCodaDocumentLineLink findUnique(final InvoiceForLease invoice, final CodaDocumentLine codaDocumentLine){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        InvoiceForLeaseCodaDocumentLineLink.class,
                        "findUnique",
                        "codaDocumentLine", codaDocumentLine,
                        "invoice", invoice));
    }

    @Programmatic
    public InvoiceForLeaseCodaDocumentLineLink findOrCreate(final InvoiceForLease invoice, final CodaDocumentLine codaDocumentLine){
        InvoiceForLeaseCodaDocumentLineLink result = findUnique(invoice, codaDocumentLine);
        if (result ==null) {
            result = create(invoice, codaDocumentLine);
        }
        return result;
    }

    private InvoiceForLeaseCodaDocumentLineLink create(final InvoiceForLease invoice, final CodaDocumentLine codaDocumentLine){
        InvoiceForLeaseCodaDocumentLineLink link = new InvoiceForLeaseCodaDocumentLineLink(invoice, codaDocumentLine);
        repositoryService.persistAndFlush(link);
        return link;
    }

    @Inject
    RepositoryService repositoryService;

}
