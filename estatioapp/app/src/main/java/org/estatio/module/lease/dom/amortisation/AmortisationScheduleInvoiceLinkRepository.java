package org.estatio.module.lease.dom.amortisation;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = AmortisationScheduleInvoiceLink.class
)
public class AmortisationScheduleInvoiceLinkRepository {

    @Programmatic
    public List<AmortisationScheduleInvoiceLink> listAll() {
        return repositoryService.allInstances(AmortisationScheduleInvoiceLink.class);
    }

    @Programmatic
    public AmortisationScheduleInvoiceLink findUnique(final AmortisationSchedule schedule, final InvoiceForLease invoice) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        AmortisationScheduleInvoiceLink.class,
                        "findUnique",
                        "amortisationSchedule", schedule,
                        "invoice", invoice));
    }

    @Programmatic
    public List<AmortisationScheduleInvoiceLink> findBySchedule(final AmortisationSchedule schedule) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationScheduleInvoiceLink.class,
                        "findBySchedule",
                        "amortisationSchedule", schedule));
    }

    @Programmatic
    public List<AmortisationScheduleInvoiceLink> findByInvoice(final InvoiceForLease invoice) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationScheduleInvoiceLink.class,
                        "findByInvoice",
                        "invoice", invoice));
    }

    @Programmatic
    public AmortisationScheduleInvoiceLink findOrCreate(
            final AmortisationSchedule schedule,
            final InvoiceForLease invoice){
        final AmortisationScheduleInvoiceLink result = findUnique(schedule, invoice);
        if (result == null) return create(schedule, invoice);
        return result;
    }

    private AmortisationScheduleInvoiceLink create(
            final AmortisationSchedule schedule,
            final InvoiceForLease invoice){
        final AmortisationScheduleInvoiceLink newLink = new AmortisationScheduleInvoiceLink(schedule, invoice);
        repositoryService.persistAndFlush(newLink);
        return newLink;
    }

    @Inject
    RepositoryService repositoryService;

}
