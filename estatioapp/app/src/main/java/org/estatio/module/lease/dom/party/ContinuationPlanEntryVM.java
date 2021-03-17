package org.estatio.module.lease.dom.party;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.message.MessageService2;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, objectType = "party.ContinuationPlanEntryVM")
public class ContinuationPlanEntryVM implements Importable {

    public ContinuationPlanEntryVM(){}

    public ContinuationPlanEntryVM(
            final String tenantReference,
            final LocalDate entryDate,
            final BigDecimal percentage,
            final String leaseReference,
            final BigDecimal amountForLease,
            final LocalDate datePaid
    ){
        this.tenantReference = tenantReference;
        this.entryDate = entryDate;
        this.percentage = percentage;
        this.leaseReference = leaseReference;
        this.amountForLease = amountForLease;
        this.datePaid = datePaid;
    }

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String tenantReference;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private LocalDate entryDate;

    @Getter @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "3")
    private BigDecimal percentage;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private String leaseReference;

    @Getter @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "5")
    private BigDecimal amountForLease;

    @Getter @Setter
    @MemberOrder(sequence = "6")
    private LocalDate datePaid;


    @Override
    public List<Object> importData(final Object previousRow) {
        List<Object> result = new ArrayList<>();
        ContinuationPlan continuationPlan = fetchContinuationPlan(fetchRecord(getTenantReference()));
        final ContinuationPlanEntry entry = continuationPlan.addEntry(getEntryDate(), getPercentage());

        if (getLeaseReference()!=null) {
            if (getAmountForLease()==null) messageService2.raiseError(String.format("Amount for lease is mandatory for lease with reference %s", getLeaseReference()));
            final EntryValueForLease valueForLease = entry
                    .addValue(fetchLease(getLeaseReference()), getAmountForLease());
            valueForLease.setDatePaid(getDatePaid());
        }

        result.add(entry);

        return result;
    }

    TenantAdministrationRecord fetchRecord(final String tenantReference){
        final Party tenantIfAny = partyRepository.findPartyByReference(tenantReference);
        if (tenantIfAny!=null) {
            final TenantAdministrationRecord recordIfAny = factoryService
                    .mixin(Party_administrationRecord.class, tenantIfAny).prop();
            if (recordIfAny!=null) return recordIfAny;
        };
        messageService2.raiseError(String.format("Tenant administration record for tenant with reference %s could not be found", tenantReference));
        return null;
    }

    ContinuationPlan fetchContinuationPlan(final TenantAdministrationRecord record){
        final ContinuationPlan planIfAny = continuationPlanRepository.findUnique(record);
        if (planIfAny!=null) return planIfAny;
        messageService2.raiseError(String.format("Continuation plan for tenant with reference %s could not be found; please create one", tenantReference));
        return null;
    }

    Lease fetchLease(final String leaseReference){
        final Lease leaseIfAny = leaseRepository.findLeaseByReference(getLeaseReference());
        if (leaseIfAny!=null) return leaseIfAny;
        messageService2.raiseError(String.format("Lease with reference %s could not be found", leaseReference));
        return null;
    }

    @Inject ContinuationPlanRepository continuationPlanRepository;

    @Inject PartyRepository partyRepository;

    @Inject MessageService2 messageService2;

    @Inject FactoryService factoryService;

    @Inject LeaseRepository leaseRepository;

}
