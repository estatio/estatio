package org.estatio.module.guarantee.fixture.rowhandlers;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.dom.Importable;
import org.estatio.dom.financial.FinancialAccountTransaction;
import org.estatio.dom.financial.FinancialAccountTransactionRepository;
import org.estatio.module.guarantee.dom.Guarantee;
import org.estatio.module.guarantee.dom.GuaranteeRepository;
import org.estatio.module.guarantee.dom.GuaranteeType;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.GuaranteeImport"
)
public class GuaranteeImport implements ExcelFixtureRowHandler, Importable {

    private static final Logger LOG = LoggerFactory.getLogger(GuaranteeImport.class);

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private LocalDate terminationDate;

    @Getter @Setter
    private GuaranteeType guaranteeType;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private BigDecimal monthsRent;

    @Getter @Setter
    private BigDecimal monthsServiceCharge;

    @Getter @Setter
    private BigDecimal maximumAmount;

    // Transaction
    @Getter @Setter
    private LocalDate transactionDate;

    @Getter @Setter
    private String transactionDescription;

    @Getter @Setter
    private BigDecimal amount;

    @Getter @Setter
    private String comments;

    @Getter @Setter
    private String number;

    static int counter = 0;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(OrganisationImport.class, LeaseImport.class);
//    }

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

        counter++;
        if (counter == 1) {
            System.out.println();
            LOG.info("importing");
        }

        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        Guarantee guarantee = guaranteeRepository.findByReference(reference);
        if (guarantee == null) {
            final Lease lease = fetchLease(leaseReference);
            guarantee = guaranteeRepository.newGuarantee(
                    lease,
                    reference,
                    name,
                    guaranteeType,
                    startDate,
                    endDate,
                    description,
                    maximumAmount, null);
        }
        guarantee.setTerminationDate(terminationDate);
        guarantee.setDescription(description);
        guarantee.setComments(comments);

        FinancialAccountTransaction transaction = financialAccountTransactionRepository.findTransaction(guarantee.getFinancialAccount(), transactionDate);
        if (transaction == null && ObjectUtils.compare(amount, BigDecimal.ZERO) > 0 && guarantee.getFinancialAccount() != null) {
            transaction = financialAccountTransactionRepository.newTransaction(guarantee.getFinancialAccount(), transactionDate, transactionDescription, amount);
        }

        //            LOG.info("guarantee " + counter + " : " + guarantee.getReference() + " - " + guarantee.getLease().getReference());
        System.out.print(".");

        return Lists.newArrayList(guarantee);

    }

    private Lease fetchLease(final String leaseReference) {
        final Lease lease;
        lease = leaseRepository.findLeaseByReference(leaseReference.trim().replaceAll("~", "+"));
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    //region > injected services
    @Inject
    private GuaranteeRepository guaranteeRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    private FinancialAccountTransactionRepository financialAccountTransactionRepository;
    //endregion

}
