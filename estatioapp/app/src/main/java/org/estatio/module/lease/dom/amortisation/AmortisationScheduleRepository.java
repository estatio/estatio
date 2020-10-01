package org.estatio.module.lease.dom.amortisation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.Lease;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = AmortisationSchedule.class
)
public class AmortisationScheduleRepository {

    @Programmatic
    public List<AmortisationSchedule> listAll() {
        return repositoryService.allInstances(AmortisationSchedule.class);
    }

    @Programmatic
    public AmortisationSchedule findUnique(final Lease lease, final Charge charge, final LocalDate startDate) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        AmortisationSchedule.class,
                        "findUnique",
                        "lease", lease,
                        "charge", charge,
                        "startDate", startDate));
    }

    @Programmatic
    public List<AmortisationSchedule> findByLease(final Lease lease) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationSchedule.class,
                        "findByLease",
                        "lease", lease));
    }

    @Programmatic
    public AmortisationSchedule findOrCreate(
            final Lease lease,
            final Charge charge,
            final BigDecimal scheduledAmount,
            final Frequency frequency,
            final LocalDate startDate,
            final LocalDate endDate){
        final AmortisationSchedule result = findUnique(lease, charge, startDate);
        if (result == null) return create(lease, charge, scheduledAmount, frequency, startDate, endDate);
        return result;
    }

    private AmortisationSchedule create(
            final Lease lease,
            final Charge charge,
            final BigDecimal scheduledAmount,
            final Frequency frequency,
            final LocalDate startDate,
            final LocalDate endDate){
        if (scheduledAmount.compareTo(BigDecimal.ZERO)<0){
            messageService.raiseError("Scheduled amount cannot be negative");
            return null;
        }
        final AmortisationSchedule newSchedule = new AmortisationSchedule(lease, charge, scheduledAmount, frequency, startDate, endDate);
        serviceRegistry2.injectServicesInto(newSchedule);
        repositoryService.persistAndFlush(newSchedule);
        return newSchedule;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject MessageService messageService;

}
