package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.LeaseItemType;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = LeaseAmendmentItem.class
)
public class LeaseAmendmentItemRepository {

    @Programmatic
    public LeaseAmendmentItemForFrequencyChange upsert(
            final LeaseAmendment leaseAmendment,
            final InvoicingFrequency invoicingFrequencyOnLease,
            final InvoicingFrequency amendedInvoicingFrequency,
            final List<LeaseItemType> applicableToTypes,
            final LocalDate startDate,
            final LocalDate endDate
    ){
        final LeaseAmendmentItemForFrequencyChange item = Lists
                .newArrayList(leaseAmendment.getItems()).stream()
                .filter(li -> li.getClass().isAssignableFrom(LeaseAmendmentItemForFrequencyChange.class))
                .map(LeaseAmendmentItemForFrequencyChange.class::cast)
                .findFirst().orElse(null);
        if (leaseAmendment.getState()==LeaseAmendmentState.APPLIED) return item;
        if (item==null){
            return create(leaseAmendment, invoicingFrequencyOnLease, amendedInvoicingFrequency, applicableToTypes, startDate, endDate);
        } else {
            item.setInvoicingFrequencyOnLease(invoicingFrequencyOnLease);
            item.setAmendedInvoicingFrequency(amendedInvoicingFrequency);
            item.setApplicableTo(LeaseAmendmentItem.applicableToToString(applicableToTypes));
            item.setStartDate(startDate);
            item.setEndDate(endDate);
            return item;
        }
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


    @Programmatic
    public LeaseAmendmentItemForDiscount upsert(
            final LeaseAmendment leaseAmendment,
            final BigDecimal discountPercentage,
            final BigDecimal manualDiscountAmount,
            final List<LeaseItemType> applicableToTypes,
            final LocalDate startDate,
            final LocalDate endDate
    ){
        final LeaseAmendmentItemForDiscount item = Lists
                .newArrayList(leaseAmendment.getItems()).stream()
                .filter(li -> li.getClass().isAssignableFrom(LeaseAmendmentItemForDiscount.class))
                .map(LeaseAmendmentItemForDiscount.class::cast)
                .filter(li->li.getStartDate().equals(startDate))
                .findFirst().orElse(null);
        if (leaseAmendment.getState()==LeaseAmendmentState.APPLIED) return item;
        if (item==null){
            return create(leaseAmendment, discountPercentage, manualDiscountAmount, applicableToTypes, startDate, endDate);
        } else {
            if (validateUpsertItemForDiscount(item, discountPercentage, manualDiscountAmount, applicableToTypes, startDate, endDate)!=null){
                throw new IllegalArgumentException(
                        validateCreateItemForDiscount(leaseAmendment, discountPercentage, manualDiscountAmount, applicableToTypes, startDate, endDate)
                );
            }
            item.setDiscountPercentage(discountPercentage);
            item.setManualDiscountAmount(manualDiscountAmount);
            item.setApplicableTo(LeaseAmendmentItem.applicableToToString(applicableToTypes));
            item.setStartDate(startDate);
            item.setEndDate(endDate);
            item.setAmortisationEndDate(leaseAmendmentService.getAmortisationEndDateFor(item));
            return item;
        }
    }

    @Programmatic
    public String validateUpsertItemForDiscount(
            final LeaseAmendmentItemForDiscount leaseAmendmentItemForDiscount,
            final BigDecimal discountPercentage,
            final BigDecimal manualDiscountAmount,
            final List<LeaseItemType> applicableToTypes,
            final LocalDate startDate,
            final LocalDate endDate){
        if (leaseAmendmentItemForDiscount.getLeaseAmendment().findItemsOfType(LeaseAmendmentItemType.DISCOUNT)
                .stream()
                .filter(lai->!lai.equals(leaseAmendmentItemForDiscount))
                .filter(lai->lai.getInterval().overlaps(LocalDateInterval.including(startDate, endDate)))
                .findFirst()
                .isPresent()
        ){
            return String.format("Overlapping item for discount found on amendment %s for startdate %s and enddate %s", leaseAmendmentItemForDiscount.getLeaseAmendment().getReference(), startDate, endDate);
        }
        return null;
    }

    @Programmatic
    public LeaseAmendmentItemForDiscount create(
            final LeaseAmendment leaseAmendment,
            final BigDecimal discountPercentage,
            final BigDecimal manualDiscountAmount,
            final List<LeaseItemType> applicableToTypes,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (validateCreateItemForDiscount(leaseAmendment, discountPercentage, manualDiscountAmount, applicableToTypes, startDate, endDate)!=null) {
            throw new IllegalArgumentException(
                    validateCreateItemForDiscount(leaseAmendment, discountPercentage, manualDiscountAmount, applicableToTypes, startDate, endDate)
            );
        }
        final LeaseAmendmentItemForDiscount amendmentItem = new LeaseAmendmentItemForDiscount();
        amendmentItem.setLeaseAmendment(leaseAmendment);
        amendmentItem.setDiscountPercentage(discountPercentage);
        amendmentItem.setManualDiscountAmount(manualDiscountAmount);
        amendmentItem.setApplicableTo(LeaseAmendmentItem.applicableToToString(applicableToTypes));
        amendmentItem.setStartDate(startDate);
        amendmentItem.setEndDate(endDate);
        amendmentItem.setAmortisationEndDate(leaseAmendmentService.getAmortisationEndDateFor(amendmentItem));
        serviceRegistry2.injectServicesInto(amendmentItem);
        repositoryService.persistAndFlush(amendmentItem);
        return amendmentItem;
    }

    @Programmatic
    public String validateCreateItemForDiscount(
            final LeaseAmendment amendment,
            final BigDecimal discountPercentage,
            final BigDecimal manualDiscountAmount,
            final List<LeaseItemType> applicableToTypes,
            final LocalDate startDate,
            final LocalDate endDate){
        if (amendment.findItemsOfType(LeaseAmendmentItemType.DISCOUNT)
                .stream()
                .filter(lai->lai.getInterval().overlaps(LocalDateInterval.including(startDate, endDate)))
                .findFirst()
                .isPresent()
        ){
            return String.format("Overlapping item for discount found on amendment %s for startdate %s and enddate %s", amendment.getReference(), startDate, endDate);
        }
        return null;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    LeaseAmendmentService leaseAmendmentService;

}
