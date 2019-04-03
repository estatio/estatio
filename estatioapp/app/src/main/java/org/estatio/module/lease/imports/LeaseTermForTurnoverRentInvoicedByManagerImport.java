package org.estatio.module.lease.imports;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.xactn.TransactionService3;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseItemStatus;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForTurnoverRent;
import org.estatio.module.lease.dom.LeaseTermStatus;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.LeaseTermForTurnoverRentInvoicedByManagerImport"
)
public class LeaseTermForTurnoverRentInvoicedByManagerImport implements ExcelFixtureRowHandler, Importable {

    @Deprecated
    public LeaseTermForTurnoverRentInvoicedByManagerImport() {
    }

    public LeaseTermForTurnoverRentInvoicedByManagerImport(
            final String leaseReference,
            final String chargeReference,
            final LocalDate termStartDate,
            final BigDecimal rentNetAmount
    ) {
        this.leaseReference = leaseReference;
        this.chargeReference = chargeReference;
        this.termStartDate = termStartDate;
        this.rentNetAmount = rentNetAmount;
    }

    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String chargeReference;

    @Getter @Setter
    private LocalDate termStartDate;

    @Getter @Setter
    private BigDecimal rentNetAmount;

    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: other import view models have @Action annotation here...  but in any case, is this view model actually ever surfaced in the UI?
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {
        LeaseItem item = importItem();
        LeaseTermForTurnoverRent term = (LeaseTermForTurnoverRent) item.findTerm(termStartDate);
        if (term==null) {
            if (!item.getTerms().isEmpty()){
                LocalDate nextStartDate = item.getTerms().last().getStartDate().plusYears(1);
                while (nextStartDate.isBefore(termStartDate)){
                    LeaseTermForTurnoverRent emptyTerm = (LeaseTermForTurnoverRent) item.newTerm(nextStartDate, null);
                    emptyTerm.setManualTurnoverRent(BigDecimal.ZERO);
                    emptyTerm.setStatus(LeaseTermStatus.APPROVED);
                    nextStartDate = nextStartDate.plusYears(1);
                    transactionService3.nextTransaction(); // needed because of DN behaviour...
                }
            }
            term = (LeaseTermForTurnoverRent) item.newTerm(termStartDate, null);
            term.setManualTurnoverRent(rentNetAmount);
            term.setStatus(LeaseTermStatus.APPROVED);
        }
        return Lists.newArrayList(term);
    }

    @Programmatic
    public LeaseItem importItem() {
        final Lease lease = fetchLease(leaseReference);
        final Charge charge = fetchCharge(chargeReference);
        final boolean itemManagedByManager = termStartDate.isBefore(new LocalDate(2018, 1, 1));
        LeaseItem item = itemManagedByManager ?
                leaseItemRepository.findByLeaseAndTypeAndStartDateAndInvoicedBy(lease, LeaseItemType.TURNOVER_RENT, lease.getStartDate(), LeaseAgreementRoleTypeEnum.MANAGER) :
                leaseItemRepository.findByLeaseAndTypeAndInvoicedBy(lease, LeaseItemType.TURNOVER_RENT, LeaseAgreementRoleTypeEnum.LANDLORD);
        if (item == null) {
            item = lease.newItem(LeaseItemType.TURNOVER_RENT, itemManagedByManager ? LeaseAgreementRoleTypeEnum.MANAGER : LeaseAgreementRoleTypeEnum.LANDLORD, charge, InvoicingFrequency.YEARLY_IN_ARREARS, itemManagedByManager ? PaymentMethod.MANUAL_PROCESS : PaymentMethod.CHEQUE, lease.getStartDate());
            item.setSequence(BigInteger.ONE);
            item.setApplicationTenancyPath(lease.getAtPath());
            item.setStatus(LeaseItemStatus.ACTIVE);
        }
        return item;
    }

    private Lease fetchLease(final String leaseReference) {
        final Lease lease;
        lease = leaseRepository.findLeaseByReference(leaseReference.trim().replaceAll("~", "+"));
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    private Charge fetchCharge(final String chargeReference) {
        final Charge charge = chargeRepository
                .findByReference(chargeReference);
        if (charge == null) {
            throw new ApplicationException(String.format("Charge with reference %s not found.", chargeReference));
        }
        return charge;
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseItemRepository leaseItemRepository;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject TransactionService3 transactionService3;

}
