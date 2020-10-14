package org.estatio.module.lease.dom.amortisation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = AmortisationEntry.class
)
public class AmortisationEntryRepository {

    @Programmatic
    public List<AmortisationEntry> listAll() {
        return repositoryService.allInstances(AmortisationEntry.class);
    }

    @Programmatic
    public AmortisationEntry findUnique(final AmortisationSchedule schedule, final LocalDate entryDate) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        AmortisationEntry.class,
                        "findUnique",
                        "schedule", schedule,
                        "entryDate", entryDate));
    }

    @Programmatic
    public List<AmortisationEntry> findBySchedule(final AmortisationSchedule schedule) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationEntry.class,
                        "findBySchedule",
                        "schedule", schedule));
    }

    @Programmatic
    public AmortisationEntry findOrCreate(
            final AmortisationSchedule schedule,
            final LocalDate entryDate,
            final BigDecimal entryAmount){
        final AmortisationEntry result = findUnique(schedule, entryDate);
        if (result == null) return create(schedule, entryDate, entryAmount);
        return result;
    }

    private AmortisationEntry create(
            final AmortisationSchedule schedule,
            final LocalDate entryDate,
            final BigDecimal entryAmount){
        final AmortisationEntry newEntry = new AmortisationEntry(schedule, entryDate, entryAmount);
        serviceRegistry2.injectServicesInto(newEntry);
        repositoryService.persistAndFlush(newEntry);
        return newEntry;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
