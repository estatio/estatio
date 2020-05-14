package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.lease.dom.LeaseItemType;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = AmendmentItemForDiscount.class
)
public class AmendmentItemForDiscountRepository {

    @Programmatic
    public List<AmendmentItemForDiscount> listAll() {
        return repositoryService.allInstances(AmendmentItemForDiscount.class);
    }

    @Programmatic
    public AmendmentItemForDiscount create(
            final Amendment amendment,
            final BigDecimal discountPercentage,
            final List<LeaseItemType> applicableToTypes,
            final LocalDate startDate,
            final LocalDate endDate) {

        final AmendmentItemForDiscount amendmentItem = new AmendmentItemForDiscount();
        amendmentItem.setAmendment(amendment);
        amendmentItem.setDiscountPercentage(discountPercentage);
        amendmentItem.setApplicableTo(AmendmentItem.applicableToToString(applicableToTypes));
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
