package org.estatio.module.lease.dom.party;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.services.message.MessageService2;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, objectType = "party.TenantAdministrationRecordVM")
public class TenantAdministrationRecordVM implements Importable {

    public TenantAdministrationRecordVM(){}

    public TenantAdministrationRecordVM(
            final String tenantReference,
            final String administrationStatus,
            final LocalDate judicialRedressDate,
            final LocalDate liquidationDate,
            final String comments,
            final String leaseReference,
            final BigDecimal declaredAmountOfClaim,
            final Boolean debtAdmitted,
            final BigDecimal admittedAmountOfClaim,
            final Boolean leaseContinued
    ){
        this.tenantReference = tenantReference;
        this.administrationStatus = administrationStatus;
        this.judicialRedressDate = judicialRedressDate;
        this.liquidationDate = liquidationDate;
        this.comments = comments;
        this.leaseReference = leaseReference;
        this.declaredAmountOfClaim = declaredAmountOfClaim;
        this.debtAdmitted = debtAdmitted;
        this.admittedAmountOfClaim = admittedAmountOfClaim;
        this.leaseContinued = leaseContinued;
    }

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String tenantReference;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String administrationStatus;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private LocalDate judicialRedressDate;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private LocalDate liquidationDate;

    @Getter @Setter
    @MemberOrder(sequence = "5")
    private String comments;

    @Getter @Setter
    @MemberOrder(sequence = "6")
    private String leaseReference;

    @Getter @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "7")
    private BigDecimal declaredAmountOfClaim;

    @Getter @Setter
    @MemberOrder(sequence = "8")
    private Boolean debtAdmitted;

    @Getter @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "9")
    private BigDecimal admittedAmountOfClaim;

    @Getter @Setter
    @MemberOrder(sequence = "10")
    private Boolean leaseContinued;

    @Override
    public List<Object> importData(final Object previousRow) {
        List<Object> result = new ArrayList<>();

        final AdministrationStatus status = fetchStatus(getAdministrationStatus());
        TenantAdministrationRecord record = tenantAdministrationRecordRepository.upsertOrCreateNext(status, fetchTenant(getTenantReference()), getJudicialRedressDate());
        if (record == null) {
            if (status.equals(AdministrationStatus.LIQUIDATION)) {
                messageService2.raiseError(String.format("Status %s is final on tenant with reference %s; cannot be changed", getAdministrationStatus(), getTenantReference()));
            } else {
                messageService2.raiseError(String.format("Status %s has already been set once before on tenant with reference %s", getAdministrationStatus(), getTenantReference()));
            }
            return null;
        }
        record.setLiquidationDate(getLiquidationDate());
        record.setComments(getComments());

        if (getLeaseReference()!=null){
            if (getDeclaredAmountOfClaim()==null) messageService2.raiseError(String.format("Declared amount should not be empty for lease with reference %s", getLeaseReference()));
            record.addLeaseDetails(
                    fetchLease(getLeaseReference()),
                    getDeclaredAmountOfClaim(),
                    getDebtAdmitted(),
                    getAdmittedAmountOfClaim(),
                    getLeaseContinued()
                    );
        }

        result.add(record);

        return result;
    }

    Party fetchTenant(final String tenantReference){
        final Party tenantIfAny = partyRepository.findPartyByReference(tenantReference);
        if (tenantIfAny!=null) return tenantIfAny;
        messageService2.raiseError(String.format("Tenant with reference %s could not be found", tenantReference));
        return null;
    }

    AdministrationStatus fetchStatus(final String administrationStatus){
        try {
            return AdministrationStatus.valueOf(administrationStatus);
        } catch (Exception e){
            List<String> validVals = new ArrayList<>();
            Arrays.stream(AdministrationStatus.values()).forEach(v->validVals.add(v.toString()));
            messageService2.raiseError(String.format("No status found for %s; valid are : %s", administrationStatus, validVals));
            return null;
        }
    }

    Lease fetchLease(final String leaseReference){
        final Lease leaseIfAny = leaseRepository.findLeaseByReference(getLeaseReference());
        if (leaseIfAny!=null) return leaseIfAny;
        messageService2.raiseError(String.format("Lease with reference %s could not be found", leaseReference));
        return null;
    }

    @Inject PartyRepository partyRepository;

    @Inject MessageService2 messageService2;

    @Inject TenantAdministrationRecordRepository tenantAdministrationRecordRepository;

    @Inject LeaseRepository leaseRepository;

}
