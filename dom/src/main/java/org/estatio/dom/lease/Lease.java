package org.estatio.dom.lease;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.clock.Clock;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.agreement.Agreements;
import org.estatio.dom.financial.BankAccount;
import org.estatio.dom.financial.BankMandate;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.financial.FinancialConstants;
import org.estatio.dom.invoice.InvoiceSource;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(name = "findByReference", language = "JDOQL", value = "SELECT FROM org.estatio.dom.lease.Lease WHERE reference.matches(:reference)"),
        @javax.jdo.annotations.Query(name = "findByAssetAndActiveOnDate", language = "JDOQL", value = "SELECT FROM org.estatio.dom.lease.Lease WHERE units.contains(lu) && (terminationDate == null || terminationDate <= :activeOnDate) && (lu.unit == :asset || lu.unit.property == :asset) VARIABLES org.estatio.dom.lease.LeaseUnit lu") })
@Bookmarkable
@MemberGroups({"General", "Dates", "Lease Details", "Related"})
public class Lease extends Agreement implements InvoiceSource {

    @Override
    @NotPersisted
    @MemberOrder(sequence = "3")
    public Party getPrimaryParty() {
        return findParty(LeaseConstants.ART_LANDLORD);
    }

    @Override
    @NotPersisted
    @MemberOrder(sequence = "4")
    public Party getSecondaryParty() {
        return findParty(LeaseConstants.ART_TENANT);
    }

    // //////////////////////////////////////

    private LeaseType type;

    @MemberOrder(name="Lease Details", sequence = "8")
    public LeaseType getType() {
        return type;
    }

    public void setType(final LeaseType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "lease")
    private SortedSet<LeaseUnit> units = new TreeSet<LeaseUnit>();

    @MemberOrder(name = "Units", sequence = "20")
    @Render(Type.EAGERLY)
    public SortedSet<LeaseUnit> getUnits() {
        return units;
    }

    public void setUnits(final SortedSet<LeaseUnit> units) {
        this.units = units;
    }

    public void addToUnits(final LeaseUnit leaseUnit) {
        if (leaseUnit == null || getUnits().contains(leaseUnit)) {
            return;
        }
        leaseUnit.clearLease();
        leaseUnit.setLease(this);
        getUnits().add(leaseUnit);
    }

    public void removeFromUnits(final LeaseUnit leaseUnit) {
        if (leaseUnit == null || !getUnits().contains(leaseUnit)) {
            return;
        }
        leaseUnit.setLease(null);
        getUnits().remove(leaseUnit);
    }

    @MemberOrder(name = "Units", sequence = "21")
    public LeaseUnit addUnit(@Named("unit") UnitForLease unit) {
        LeaseUnit leaseUnit = leaseUnits.newLeaseUnit(this, unit);
        units.add(leaseUnit);
        return leaseUnit;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "lease")
    private SortedSet<LeaseItem> items = new TreeSet<LeaseItem>();

    @Render(Type.EAGERLY)
    @MemberOrder(name = "Items", sequence = "30")
    public SortedSet<LeaseItem> getItems() {
        return items;
    }

    public void setItems(final SortedSet<LeaseItem> items) {
        this.items = items;
    }

    public void addToItems(final LeaseItem leaseItem) {
        if (leaseItem == null || getItems().contains(leaseItem)) {
            return;
        }
        leaseItem.clearLease();
        leaseItem.setLease(this);
        getItems().add(leaseItem);
    }

    public void removeFromItems(final LeaseItem leaseItem) {
        if (leaseItem == null || !getItems().contains(leaseItem)) {
            return;
        }
        leaseItem.setLease(null);
        getItems().remove(leaseItem);
    }

    @MemberOrder(name = "Items", sequence = "31")
    public LeaseItem newItem(LeaseItemType type) {
        LeaseItem leaseItem = leaseItems.newLeaseItem(this, type);
        return leaseItem;
    }

    @Hidden
    public LeaseItem findItem(LeaseItemType type, LocalDate startDate, BigInteger sequence) {
        // TODO: better/faster filter options? -> Use predicate
        for (LeaseItem item : getItems()) {
            LocalDate itemStartDate = item.getStartDate();
            LeaseItemType itemType = item.getType();
            if (itemType.equals(type) && itemStartDate.equals(startDate) && item.getSequence().equals(sequence)) {
                return item;
            }
        }
        return null;
    }

    @Hidden
    public LeaseItem findFirstItemOfType(LeaseItemType type) {
        for (LeaseItem item : getItems()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
    
    // //////////////////////////////////////

    private BankMandate paidBy;

    @Optional
    @Disabled
    @MemberOrder(name="Lease Details", sequence = "10")
    public BankMandate getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(final BankMandate paidBy) {
        this.paidBy = paidBy;
    }

    
    // //////////////////////////////////////

    @MemberOrder(name="paidBy", sequence = "1")
    public Lease paidBy(final BankMandate bankMandate) {
        setPaidBy(bankMandate);
        return this;
    }
    public String disablePaidBy(final BankMandate bankMandate) {
        final List<BankMandate> validMandates = existingBankMandatesForTenant();
        if(validMandates.isEmpty()) {
            return "There are no valid mandates; set one up using 'New Mandate'";
        }
        return null;
    }
    public List<BankMandate> choices0PaidBy() {
        return existingBankMandatesForTenant();
    }

    public BankMandate default0PaidBy() {
        final List<BankMandate> choices = existingBankMandatesForTenant();
        return !choices.isEmpty() ? choices.get(0) : null;
    }
    public String validatePaidBy(final BankMandate bankMandate) {
        final List<BankMandate> validMandates = existingBankMandatesForTenant();
        if(validMandates.contains(bankMandate)) {
            return null;
        } else {
            return "Invalid mandate; the mandate's debtor must be this lease's tenant";
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<BankMandate> existingBankMandatesForTenant() {
        final Party tenant = getSecondaryParty();
        
        if(tenant != null) {
            final AgreementType bankMandateAgreementType = bankMandateAgreementType();
            final AgreementRoleType debtorRoleType = debtorRoleType();
            
            return (List)agreements.findByAgreementTypeAndRoleTypeAndParty(bankMandateAgreementType, debtorRoleType, tenant);
        }
        return Collections.emptyList();
    }

    
    // //////////////////////////////////////

    @MemberOrder(name="paidBy", sequence = "2")
    public Lease newMandate(
            final BankAccount bankAccount, 
            final @Named("Start Date") LocalDate startDate,
            final @Named("End Date") LocalDate endDate
            ) {
        final BankMandate bankMandate = newTransientInstance(BankMandate.class);
        final AgreementType bankMandateAgreementType = bankMandateAgreementType();
        final AgreementRoleType debtorRoleType = debtorRoleType();
        
        bankMandate.setAgreementType(bankMandateAgreementType);
        bankMandate.setBankAccount(bankAccount);
        bankMandate.setStartDate(startDate);
        bankMandate.setEndDate(endDate);
        bankMandate.setReference(bankAccount.getReference() + "-"+ startDate.toString("yyyyMMdd"));
        bankMandate.addRole(getSecondaryParty(), debtorRoleType, startDate, endDate);
        
        persist(bankMandate);
        this.setPaidBy(bankMandate);
        
        return this;
    }
    public String disableNewMandate(
            final BankAccount bankAccount, 
            final LocalDate startDate,
            final LocalDate endDate) {
        final Party tenant = getSecondaryParty();
        if (tenant == null) {
            return "Could not determine the tenant (secondary party) of this lease";
        } 
        final List<BankAccount> validBankAccounts = existingBankAccountsForTenant();
        if(validBankAccounts.isEmpty()) {
            return "There are no bank accounts available for this tenant";
        }
        return null;
    }
    public List<BankAccount> choices0NewMandate() {
        return existingBankAccountsForTenant();
    }
    public BankAccount default0NewMandate() {
        final List<BankAccount> choices = existingBankAccountsForTenant();
        return !choices.isEmpty() ? choices.get(0) : null;
    }
    public LocalDate default1NewMandate() {
        return getClockService().now();
    }
    public LocalDate default2NewMandate() {
        return getClockService().now().plusYears(1);
    }
    public String validateNewMandate(
            final BankAccount bankAccount, 
            final LocalDate startDate,
            final LocalDate endDate) {
        final List<BankAccount> validBankAccounts = existingBankAccountsForTenant();
        if(!validBankAccounts.contains(bankAccount)) {
            return "Bank account is not owned by this lease's tenant";
        } 
        return null;
    }
    
    private List<BankAccount> existingBankAccountsForTenant() {
        final Party tenant = getSecondaryParty();
        if(tenant != null) {
            return financialAccounts.findBankAccountsByParty(tenant);
        } else {
            return Collections.emptyList();
        }
    }
    
    private AgreementRoleType debtorRoleType() {
        return agreementRoleTypes.findByTitle(FinancialConstants.ART_DEBTOR);
    }

    private AgreementType bankMandateAgreementType() {
        return agreementTypes.find(FinancialConstants.AT_MANDATE);
    }
    

    // //////////////////////////////////////

    @Bulk
    @Prototype
    @MemberOrder(name = "Items", sequence = "1")
    public Lease approveAllTermsOfThisLease() {
        for (LeaseItem item : getItems()) {
            for (LeaseTerm term : item.getTerms()) {
                term.approve();
            }
        }
        return this;
    }

    // //////////////////////////////////////

    @Bulk
    @MemberOrder(name = "Items", sequence = "2")
    public Lease verify() {
        for (LeaseItem item : getItems()) {
            item.verify();
        }
        return this;
    }

    // //////////////////////////////////////

    @Bulk
    @MemberOrder(sequence = "3")
    public Lease calculate(@Named("Period Start Date") LocalDate startDate, @Named("Due date") LocalDate dueDate, @Named("Run Type") InvoiceRunType runType) {
        for (LeaseItem item : getItems()) {
            item.calculate(startDate, dueDate, runType);
        }
        return this;
    }

    // //////////////////////////////////////
    
    @MemberOrder(sequence = "4")
    public Lease terminate(@Named("Termination Date") LocalDate terminationDate, @Named("Are you sure?") boolean confirm) {
        for (LeaseItem item : getItems()) {
            LeaseTerm term = item.currentTerm(terminationDate);
            if (term == null)
                term = item.getTerms().last();
            if (term != null) {
                term.modifyEndDate(terminationDate);
                if (term.getNextTerm() != null)
                    term.getNextTerm().remove();
            }
        }
        return this;
    }

    // //////////////////////////////////////

    private LeaseItems leaseItems;

    public void injectLeaseItems(final LeaseItems leaseItems) {
        this.leaseItems = leaseItems;
    }

    private LeaseUnits leaseUnits;

    public void injectLeaseUnits(final LeaseUnits leaseUnits) {
        this.leaseUnits = leaseUnits;
    }


    private FinancialAccounts financialAccounts;
    public void injectFinancialAccounts(FinancialAccounts financialAccounts) {
        this.financialAccounts = financialAccounts;
    }

}
