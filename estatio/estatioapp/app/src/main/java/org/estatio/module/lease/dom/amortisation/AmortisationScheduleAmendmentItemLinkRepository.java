package org.estatio.module.lease.dom.amortisation;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = AmortisationScheduleAmendmentItemLink.class
)
public class AmortisationScheduleAmendmentItemLinkRepository {

    @Programmatic
    public List<AmortisationScheduleAmendmentItemLink> listAll() {
        return repositoryService.allInstances(AmortisationScheduleAmendmentItemLink.class);
    }

    @Programmatic
    public AmortisationScheduleAmendmentItemLink findUnique(final AmortisationSchedule schedule, final LeaseAmendmentItemForDiscount itemForDiscount) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        AmortisationScheduleAmendmentItemLink.class,
                        "findUnique",
                        "amortisationSchedule", schedule,
                        "leaseAmendmentItemForDiscount", itemForDiscount));
    }

    @Programmatic
    public List<AmortisationScheduleAmendmentItemLink> findBySchedule(final AmortisationSchedule schedule) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationScheduleAmendmentItemLink.class,
                        "findBySchedule",
                        "amortisationSchedule", schedule));
    }

    @Programmatic
    public List<AmortisationScheduleAmendmentItemLink> findByAmendmentItem(final LeaseAmendmentItemForDiscount leaseAmendmentItemForDiscount) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationScheduleAmendmentItemLink.class,
                        "findByAmendmentItem",
                        "leaseAmendmentItemForDiscount", leaseAmendmentItemForDiscount));
    }

    @Programmatic
    public AmortisationScheduleAmendmentItemLink findOrCreate(
            final AmortisationSchedule schedule,
            final LeaseAmendmentItemForDiscount itemForDiscount){
        final AmortisationScheduleAmendmentItemLink result = findUnique(schedule, itemForDiscount);
        if (result == null) return create(schedule, itemForDiscount);
        return result;
    }

    private AmortisationScheduleAmendmentItemLink create(
            final AmortisationSchedule schedule,
            final LeaseAmendmentItemForDiscount itemForDiscount){
        final AmortisationScheduleAmendmentItemLink newLink = new AmortisationScheduleAmendmentItemLink(schedule, itemForDiscount);
        repositoryService.persistAndFlush(newLink);
        return newLink;
    }

    @Inject
    RepositoryService repositoryService;

}
