package org.estatio.module.lease.fixture.rowhandlers;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.dom.Importable;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.BankMandateRepository;
import org.estatio.dom.bankmandate.Scheme;
import org.estatio.dom.bankmandate.SequenceType;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.BankMandateImport"
)
public class BankMandateImport implements ExcelFixtureRowHandler, Importable {

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String sepaMandateIdentifier;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String debtorReference;

    @Getter @Setter
    private String creditorReference;

    @Getter @Setter
    private String bankAccountReference;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private String sequenceType;

    @Getter @Setter
    private String scheme;

    @Getter @Setter
    private LocalDate signatureDate;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(BankAccountImport.class, LeaseImport.class, OrganisationImport.class);
//    }

    @Programmatic
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

        final Lease lease = fetchLease(leaseReference);
        BankAccount bankAccount;
        BankMandate bankMandate = null;

        // find or create bank account
        final Party owner = lease.getSecondaryParty();
        bankAccount = (BankAccount) financialAccountRepository.findByOwnerAndReference(owner, bankAccountReference);
        if (bankAccount == null) {
            bankAccount = bankAccountRepository.newBankAccount(owner, bankAccountReference, null);
        }

        if (reference != null) {
            bankMandate = bankMandateRepository.findByReference(reference);
        }

        if (bankMandate == null) {
            lease.newMandate(bankAccount, reference, startDate, endDate, SequenceType.valueOf(sequenceType), Scheme.valueOf(scheme), signatureDate);
            bankMandate = lease.getPaidBy();
        }

        bankMandate.setBankAccount(bankAccount);
        bankMandate.setReference(reference);
        bankMandate.setName(name);
        bankMandate.setStartDate(startDate);
        bankMandate.setEndDate(endDate);
        bankMandate.setSepaMandateIdentifier(sepaMandateIdentifier);
        bankMandate.setSequenceType(SequenceType.valueOf(sequenceType));
        bankMandate.setScheme(Scheme.valueOf(scheme));
        bankMandate.setSignatureDate(signatureDate);
        lease.paidBy(bankMandate);

        return Lists.newArrayList(bankMandate);

    }

    private Lease fetchLease(final String leaseReference) {
        final Lease lease;
        lease = leaseRepository.findLeaseByReference(leaseReference.trim().replaceAll("~", "+"));
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private FinancialAccountRepository financialAccountRepository;

    @Inject
    private BankMandateRepository bankMandateRepository;

    @Inject
    private BankAccountRepository bankAccountRepository;
}
