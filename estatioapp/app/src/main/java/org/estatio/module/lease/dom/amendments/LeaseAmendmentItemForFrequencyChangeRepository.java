package org.estatio.module.lease.dom.amendments;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.LeaseItemType;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = LeaseAmendmentItemForFrequencyChange.class
)
public class LeaseAmendmentItemForFrequencyChangeRepository {

    @Programmatic
    public List<LeaseAmendmentItemForFrequencyChange> listAll() {
        return repositoryService.allInstances(LeaseAmendmentItemForFrequencyChange.class);
    }

    @Programmatic
    public LeaseAmendmentItemForFrequencyChange create(
            final LeaseAmendment leaseAmendment,
            final InvoicingFrequency invoicingFrequencyOnLease,
            final InvoicingFrequency amendedInvoicingFrequency,
            final List<LeaseItemType> applicableToTypes,
            final LocalDate startDate,
            final LocalDate endDate) {
        final LeaseAmendmentItemForFrequencyChange amendmentItem = new LeaseAmendmentItemForFrequencyChange();
        amendmentItem.setLeaseAmendment(leaseAmendment);
        amendmentItem.setInvoicingFrequencyOnLease(invoicingFrequencyOnLease);
        amendmentItem.setAmendedInvoicingFrequency(amendedInvoicingFrequency);
        amendmentItem.setApplicableTo(LeaseAmendmentItem.applicableToToString(applicableToTypes));
        amendmentItem.setStartDate(startDate);
        amendmentItem.setEndDate(endDate);
        serviceRegistry2.injectServicesInto(amendmentItem);
        repositoryService.persistAndFlush(amendmentItem);
        return amendmentItem;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
