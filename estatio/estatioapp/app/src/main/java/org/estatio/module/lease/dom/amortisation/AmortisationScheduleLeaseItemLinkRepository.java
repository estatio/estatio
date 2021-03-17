package org.estatio.module.lease.dom.amortisation;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.lease.dom.LeaseItem;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = AmortisationScheduleLeaseItemLink.class
)
public class AmortisationScheduleLeaseItemLinkRepository {

    @Programmatic
    public List<AmortisationScheduleLeaseItemLink> listAll() {
        return repositoryService.allInstances(AmortisationScheduleLeaseItemLink.class);
    }

    @Programmatic
    public AmortisationScheduleLeaseItemLink findUnique(final AmortisationSchedule schedule, final LeaseItem leaseItem) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        AmortisationScheduleLeaseItemLink.class,
                        "findUnique",
                        "amortisationSchedule", schedule,
                        "leaseItem", leaseItem));
    }

    @Programmatic
    public List<AmortisationScheduleLeaseItemLink> findBySchedule(final AmortisationSchedule schedule) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationScheduleLeaseItemLink.class,
                        "findBySchedule",
                        "amortisationSchedule", schedule));
    }

    @Programmatic
    public List<AmortisationScheduleLeaseItemLink> findByLeaseItem(final LeaseItem leaseItem) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationScheduleLeaseItemLink.class,
                        "findByLeaseItem",
                        "leaseItem", leaseItem));
    }

    @Programmatic
    public AmortisationScheduleLeaseItemLink findOrCreate(
            final AmortisationSchedule schedule,
            final LeaseItem leaseItem){
        final AmortisationScheduleLeaseItemLink result = findUnique(schedule, leaseItem);
        if (result == null) return create(schedule, leaseItem);
        return result;
    }

    private AmortisationScheduleLeaseItemLink create(
            final AmortisationSchedule schedule,
            final LeaseItem leaseItem){
        final AmortisationScheduleLeaseItemLink newLink = new AmortisationScheduleLeaseItemLink(schedule, leaseItem);
        repositoryService.persistAndFlush(newLink);
        return newLink;
    }

    @Inject
    RepositoryService repositoryService;

}
