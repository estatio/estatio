/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
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
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.annotation.Render.Type;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.asset.Property;
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
public class Lease extends Agreement<LeaseStatus> implements InvoiceSource {


    public Lease() {
        super(LeaseStatus.NEW, LeaseStatus.APPROVED);
    }
    
    // //////////////////////////////////////

    @Override
    public void created() {
        super.created();
        setStatus(LeaseStatus.NEW);
    }
    
    // //////////////////////////////////////
    
    private LeaseStatus status;

    @Disabled
    @Override
    public LeaseStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(final LeaseStatus status) {
        this.status = status;
    }

    
    // //////////////////////////////////////


    @Override
    @NotPersisted
    public Party getPrimaryParty() {
        return findParty(LeaseConstants.ART_LANDLORD);
    }

    @Override
    @NotPersisted
    public Party getSecondaryParty() {
        return findParty(LeaseConstants.ART_TENANT);
    }

    // //////////////////////////////////////
    
    /**
     * The {@link Property} of the (first of the) {@link #getUnits() LeaseUnit}s.
     * 
     * <p>
     * It is not possible for the {@link LeaseUnit}s to belong to different
     * {@link Property properties}, and so it is sufficient to obtain the {@link Property}
     * of the first such {@link LeaseUnit occupancy}. 
     */
    @Override
    public Property getProperty() {
        if(getUnits().isEmpty()) {
            return null;
        }
        return getUnits().first().getUnit().getProperty();
    }

    // //////////////////////////////////////

    private LeaseType type;

    public LeaseType getType() {
        return type;
    }

    public void setType(final LeaseType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "lease")
    private SortedSet<LeaseUnit> units = new TreeSet<LeaseUnit>();

    @Render(Type.EAGERLY)
    public SortedSet<LeaseUnit> getUnits() {
        return units;
    }

    public void setUnits(final SortedSet<LeaseUnit> units) {
        this.units = units;
    }


    public LeaseUnit addUnit(
            final @Named("unit") UnitForLease unit) {
        // TODO: there doesn't seem to be any disableXxx guard for this action
        LeaseUnit leaseUnit = leaseUnits.newLeaseUnit(this, unit);
        units.add(leaseUnit);
        return leaseUnit;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "lease")
    private SortedSet<LeaseItem> items = new TreeSet<LeaseItem>();

    @Render(Type.EAGERLY)
    public SortedSet<LeaseItem> getItems() {
        return items;
    }

    public void setItems(final SortedSet<LeaseItem> items) {
        this.items = items;
    }

    public LeaseItem newItem(LeaseItemType type) {
        // TODO: there doesn't seem to be any disableXxx guard for this action
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

    @javax.jdo.annotations.Column(name="PAIDBY_ID")
    private BankMandate paidBy;

    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    public BankMandate getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(final BankMandate paidBy) {
        this.paidBy = paidBy;
    }

    // //////////////////////////////////////

    public Lease paidBy(final BankMandate bankMandate) {
        setPaidBy(bankMandate);
        return this;
    }

    public String disablePaidBy(final BankMandate bankMandate) {
        final List<BankMandate> validMandates = existingBankMandatesForTenant();
        if (validMandates.isEmpty()) {
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
        if (validMandates.contains(bankMandate)) {
            return null;
        } else {
            return "Invalid mandate; the mandate's debtor must be this lease's tenant";
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<BankMandate> existingBankMandatesForTenant() {
        final Party tenant = getSecondaryParty();

        if (tenant != null) {
            final AgreementType bankMandateAgreementType = bankMandateAgreementType();
            final AgreementRoleType debtorRoleType = debtorRoleType();

            return (List) agreements.findByAgreementTypeAndRoleTypeAndParty(bankMandateAgreementType, debtorRoleType, tenant);
        }
        return Collections.emptyList();
    }

    // //////////////////////////////////////

    public Lease newMandate(final BankAccount bankAccount, final @Named("Start Date") LocalDate startDate, final @Named("End Date") LocalDate endDate) {
        final BankMandate bankMandate = newTransientInstance(BankMandate.class);
        final AgreementType bankMandateAgreementType = bankMandateAgreementType();
        final AgreementRoleType debtorRoleType = debtorRoleType();

        bankMandate.setAgreementType(bankMandateAgreementType);
        bankMandate.setBankAccount(bankAccount);
        bankMandate.setStartDate(startDate);
        bankMandate.setEndDate(endDate);
        bankMandate.setReference(bankAccount.getReference() + "-" + startDate.toString("yyyyMMdd"));
        bankMandate.createInitialRole(debtorRoleType, getSecondaryParty(), startDate, endDate);

        persist(bankMandate);
        paidBy(bankMandate);

        return this;
    }

    public String disableNewMandate(final BankAccount bankAccount, final LocalDate startDate, final LocalDate endDate) {
        final Party tenant = getSecondaryParty();
        if (tenant == null) {
            return "Could not determine the tenant (secondary party) of this lease";
        }
        final List<BankAccount> validBankAccounts = existingBankAccountsForTenant();
        if (validBankAccounts.isEmpty()) {
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

    public String validateNewMandate(final BankAccount bankAccount, final LocalDate startDate, final LocalDate endDate) {
        final List<BankAccount> validBankAccounts = existingBankAccountsForTenant();
        if (!validBankAccounts.contains(bankAccount)) {
            return "Bank account is not owned by this lease's tenant";
        }
        return null;
    }

    private List<BankAccount> existingBankAccountsForTenant() {
        final Party tenant = getSecondaryParty();
        if (tenant != null) {
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
    public Lease approveAllTermsOfThisLease() {
        for (LeaseItem item : getItems()) {
            for (LeaseTerm term : item.getTerms()) {
                term.lock();
            }
        }
        return this;
    }

    // //////////////////////////////////////

    @Bulk
    public Lease verify() {
        for (LeaseItem item : getItems()) {
            item.verify();
        }
        return this;
    }

    // //////////////////////////////////////

    @Bulk
    public Lease calculate(@Named("Period Start Date") LocalDate startDate, @Named("Due date") LocalDate dueDate, @Named("Run Type") InvoiceRunType runType) {
        for (LeaseItem item : getItems()) {
            item.calculate(startDate, dueDate, runType);
        }
        return this;
    }

    // //////////////////////////////////////

    public Lease terminate(
            @Named("Termination Date") LocalDate terminationDate, 
            @Named("Are you sure?") @Optional Boolean confirm) {
        for (LeaseItem item : getItems()) {
            LeaseTerm term = item.currentTerm(terminationDate);
            if (term == null)
                term = item.getTerms().last();
            if (term != null) {
                term.modifyEndDate(terminationDate);
                if (term.getNext() != null)
                    term.getNext().remove();
            }
        }
        return this;
    }

    // //////////////////////////////////////

    private LeaseItems leaseItems;

    public final void injectLeaseItems(final LeaseItems leaseItems) {
        this.leaseItems = leaseItems;
    }

    private LeaseUnits leaseUnits;

    public final void injectLeaseUnits(final LeaseUnits leaseUnits) {
        this.leaseUnits = leaseUnits;
    }

    private FinancialAccounts financialAccounts;

    public final void injectFinancialAccounts(FinancialAccounts financialAccounts) {
        this.financialAccounts = financialAccounts;
    }


}
