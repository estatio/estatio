/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioUserRoles;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannel;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.BankMandateConstants;
import org.estatio.dom.bankmandate.BankMandates;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.financial.BankAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.invoice.viewmodel.InvoiceSummariesForInvoiceRun;
import org.estatio.dom.lease.breaks.BreakExerciseType;
import org.estatio.dom.lease.breaks.BreakOption;
import org.estatio.dom.lease.breaks.BreakType;
import org.estatio.dom.lease.invoicing.InvoiceCalculationParameters;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceRunType;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.JodaPeriodUtils;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
// no @DatastoreIdentity nor @Version, since inherited from supertype
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.Lease "
                        + "WHERE reference == :reference"),
        @javax.jdo.annotations.Query(
                name = "matchByReferenceOrName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.Lease "
                        + "WHERE reference.matches(:referenceOrName)"
                        + "|| name.matches(:referenceOrName)"),
        @javax.jdo.annotations.Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.Lease "
                        + "WHERE occupancies.contains(lu) "
                        + "   && (lu.unit.property == :property) "
                        + "VARIABLES "
                        + "org.estatio.dom.lease.Occupancy lu"),
        @javax.jdo.annotations.Query(
                name = "findByAssetAndActiveOnDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.Lease "
                        + "WHERE occupancies.contains(lu) "
                        + "&& (terminationDate == null || terminationDate <= :activeOnDate) "
                        + "&& (lu.unit == :asset || lu.unit.property == :asset) "
                        + "VARIABLES "
                        + "org.estatio.dom.lease.Occupancy lu"),
        @javax.jdo.annotations.Query(
                name = "findExpireInDateRange", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.lease.Lease " +
                        "WHERE " +
                        "endDate != null && (endDate >= :rangeStartDate && endDate < :rangeEndDate) " +
                        "ORDER BY endDate")
})
@AutoComplete(repository = Leases.class, action = "autoComplete")
@Bookmarkable
public class Lease
        extends Agreement {

    // //////////////////////////////////////

    public void created() {
        setStatus(LeaseStatus.ACTIVE);
    }

    // //////////////////////////////////////

    private LeaseStatus status;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.STATUS_ENUM)
    @Disabled
    public LeaseStatus getStatus() {
        return status;
    }

    public void setStatus(final LeaseStatus status) {
        this.status = status;
    }

    @Programmatic
    public Lease changeStatus(
            final LeaseStatus newStatus,
            final @Named("Reason") String reason
            ) {
        if (!newStatus.equals(getStatus())) {
            LeaseStatusReason leaseStatusReason = newTransientInstance(LeaseStatusReason.class);
            leaseStatusReason.setLease(this);
            leaseStatusReason.setReason(reason);
            leaseStatusReason.setUser(getUser().getName());
            leaseStatusReason.setTimestamp(getClockService().timestamp());
            persistIfNotAlready(leaseStatusReason);
            setStatus(newStatus);
        }
        return this;
    }

    @Programmatic
    public void resovleStatus(final String reason) {
        final LeaseStatus effectiveStatus = getEffectiveStatus();
        if (effectiveStatus != null && !effectiveStatus.equals(getStatus())) {
            setStatus(effectiveStatus);
        }
    }

    @Programmatic
    public LeaseStatus getEffectiveStatus() {
        ArrayList<LeaseItem> all = new ArrayList<LeaseItem>(getItems());
        int itemCount = getItems().size();
        List<LeaseItemStatus> statusList = Lists.transform(all, LeaseItem.Functions.GET_STATUS);
        int suspensionCount = Collections.frequency(statusList, LeaseItemStatus.SUSPENDED);
        if (suspensionCount > 0) {
            if (itemCount == suspensionCount) {
                return LeaseStatus.SUSPENDED;
            } else {
                return LeaseStatus.SUSPENDED_PARTIALLY;
            }
        }
        return null;
    }

    public List<LeaseStatusReason> statusHistory() {
        return leases.findLeaseStatusReasonByLease(this);
    }

    // //////////////////////////////////////

    @Override
    @NotPersisted
    @Hidden(where = Where.PARENTED_TABLES)
    public Party getPrimaryParty() {
        final AgreementRole ar = getPrimaryAgreementRole();
        return partyOf(ar);
    }

    @Override
    @NotPersisted
    public Party getSecondaryParty() {
        final AgreementRole ar = getSecondaryAgreementRole();
        return partyOf(ar);
    }

    @Programmatic
    protected AgreementRole getPrimaryAgreementRole() {
        return findCurrentOrMostRecentAgreementRole(LeaseConstants.ART_LANDLORD);
    }

    @Programmatic
    protected AgreementRole getSecondaryAgreementRole() {
        return findCurrentOrMostRecentAgreementRole(LeaseConstants.ART_TENANT);
    }

    // //////////////////////////////////////

    /**
     * The {@link Property} of the (first of the) {@link #getOccupancies()
     * LeaseUnit}s.
     * 
     * <p>
     * It is not possible for the {@link Occupancy}s to belong to different
     * {@link Property properties}, and so it is sufficient to obtain the
     * {@link Property} of the first such {@link Occupancy occupancy}.
     */
    @Hidden(where = Where.PARENTED_TABLES)
    public Property getFixedAsset() {
        if (getOccupancies().isEmpty()) {
            return null;
        }
        return getOccupancies().first().getUnit().getProperty();
    }

    // //////////////////////////////////////

    private LeaseType leaseType;

    @javax.jdo.annotations.Column(name = "leaseTypeId", allowsNull = "true")
    public LeaseType getLeaseType() {
        return leaseType;
    }

    public void setLeaseType(final LeaseType leaseType) {
        this.leaseType = leaseType;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    public LocalDate tenancyStartDate;

    @Disabled
    @Optional
    @Hidden(where = Where.ALL_TABLES)
    public LocalDate getTenancyStartDate() {
        return tenancyStartDate;
    }

    public void setTenancyStartDate(final LocalDate tenancyStartDate) {
        this.tenancyStartDate = tenancyStartDate;
    }

    @javax.jdo.annotations.Persistent
    public LocalDate tenancyEndDate;

    @Disabled
    @Optional
    @Hidden(where = Where.ALL_TABLES)
    public LocalDate getTenancyEndDate() {
        return tenancyEndDate;
    }

    public void setTenancyEndDate(final LocalDate tenancyEndDate) {
        this.tenancyEndDate = tenancyEndDate;
    }

    public Lease changeTenancyDates(
            final @Named("Start Date") LocalDate startDate,
            final @Named("End Date") @Optional LocalDate endDate
            ) {
        setTenancyStartDate(startDate);
        setTenancyEndDate(endDate);
        return this;
    }

    public LocalDate default0ChangeTenancyDates() {
        return getTenancyStartDate();
    }

    public LocalDate default1ChangeTenancyDates() {
        return getTenancyEndDate();
    }

    // //////////////////////////////////////

    @Programmatic
    @Override
    public LocalDateInterval getEffectiveInterval() {
        return new LocalDateInterval(
                getTenancyStartDate() == null ? getStartDate() : getTenancyStartDate(),
                getTenancyEndDate());
    }

    // //////////////////////////////////////

    @Programmatic
    public LocalDate getTerminationDate() {
        return getTenancyEndDate();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "lease")
    private SortedSet<Occupancy> occupancies = new TreeSet<Occupancy>();

    @Render(Type.EAGERLY)
    public SortedSet<Occupancy> getOccupancies() {
        return occupancies;
    }

    public void setOccupancies(final SortedSet<Occupancy> occupancies) {
        this.occupancies = occupancies;
    }

    /**
     * The action to relate a lease to a unit. A lease can occupy unlimited
     * units.
     * 
     * @param unit
     * @param startDate
     * @return
     */
    public Occupancy occupy(
            final @Named("Unit") Unit unit,
            final @Named("Start date") @Optional LocalDate startDate) {
        Occupancy occupancy = occupanciesRepo.newOccupancy(this, unit, startDate);
        occupancies.add(occupancy);
        return occupancy;
    }

    // //////////////////////////////////////

    private SortedSet<LeaseItem> items = new TreeSet<LeaseItem>();

    /**
     * Added to the default fetch group in an attempt to resolve pre-prod error,
     * EST-233.
     */
    @javax.jdo.annotations.Persistent(mappedBy = "lease", defaultFetchGroup = "true")
    @Render(Type.EAGERLY)
    public SortedSet<LeaseItem> getItems() {
        return items;
    }

    public void setItems(final SortedSet<LeaseItem> items) {
        this.items = items;
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    public LeaseItem newItem(
            final LeaseItemType type,
            final Charge charge,
            final InvoicingFrequency invoicingFrequency,
            final PaymentMethod paymentMethod,
            final @Named("Start date") LocalDate startDate) {
        LeaseItem leaseItem = leaseItems.newLeaseItem(this, type, charge, invoicingFrequency, paymentMethod, startDate);
        return leaseItem;
    }

    public LocalDate default4NewItem() {
        return getStartDate();
    }

    @Hidden
    public LeaseItem findItem(
            final LeaseItemType itemType,
            final LocalDate itemStartDate,
            final BigInteger sequence) {
        return leaseItems.findLeaseItem(this, itemType, itemStartDate, sequence);
    }

    @Programmatic
    public LeaseItem findFirstItemOfType(final LeaseItemType type) {
        for (LeaseItem item : getItems()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    @Programmatic
    public List<LeaseItem> findItemsOfType(final LeaseItemType type) {
        List<LeaseItem> items = new ArrayList<LeaseItem>();
        for (LeaseItem item : getItems()) {
            if (item.getType().equals(type)) {
                items.add(item);
            }
        }
        return items;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "lease")
    private SortedSet<BreakOption> breakOptions = new TreeSet<BreakOption>();

    @Render(Type.EAGERLY)
    public SortedSet<BreakOption> getBreakOptions() {
        return breakOptions;
    }

    public void setBreakOptions(final SortedSet<BreakOption> breakOptions) {
        this.breakOptions = breakOptions;
    }

    // //////////////////////////////////////

    public Lease newBreakOption(
            final @Named("Break date") LocalDate breakDate,
            final @Named("Notification period") @DescribedAs("Notification period in a text format. Example 6y5m2d") String notificationPeriodStr,
            final BreakExerciseType breakExerciseType,
            final BreakType breakType,
            final @Named("Description") @Optional String description
            ) {
        final BreakOption breakOption = newTransientInstance(breakType.getFactoryClass());
        breakOption.setType(breakType);
        breakOption.setLease(this);
        breakOption.setExerciseType(breakExerciseType);
        final LocalDate date = breakDate;
        breakOption.setBreakDate(date);
        breakOption.setNotificationPeriod(notificationPeriodStr);
        final Period notificationPeriodJoda = JodaPeriodUtils.asPeriod(notificationPeriodStr);
        final LocalDate excersiseDate = date.minus(notificationPeriodJoda);
        breakOption.setExerciseDate(excersiseDate);
        persist(breakOption);
        return this;
    }

    public LocalDate default0NewBreakOption() {
        // REVIEW: this is just a guess as to a reasonable default
        return getClockService().now().plusYears(2);
    }

    public String default1NewBreakOption() {
        return "3m";
    }

    public BreakExerciseType default2NewBreakOption() {
        return BreakExerciseType.TENANT;
    }

    public String validateNewBreakOption(
            final LocalDate breakDate,
            final String notificationPeriodStr,
            final BreakExerciseType breakExerciseType,
            final BreakType breakType,
            final String description) {

        final Period notificationPeriodJoda = JodaPeriodUtils.asPeriod(notificationPeriodStr);
        if (notificationPeriodJoda == null) {
            return "Notification period format not recognized";
        }
        final LocalDate notificationDate = breakDate.minus(notificationPeriodJoda);
        return checkNewBreakOptionDuplicate(BreakType.FIXED, notificationDate);
    }

    private String checkNewBreakOptionDuplicate(final BreakType breakType, final LocalDate breakDate) {
        final Iterable<BreakOption> duplicates =
                Iterables.filter(getBreakOptions(),
                        BreakOption.Predicates.whetherTypeAndBreakDate(breakType, breakDate));
        return duplicates.iterator().hasNext() ?
                "This lease already has a " + breakType + " break option for this date" : null;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "paidByBankMandateId")
    private BankMandate paidBy;

    @Hidden(where = Where.ALL_TABLES)
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
        final AgreementRole tenantRole = getSecondaryAgreementRole();
        if (tenantRole == null || !tenantRole.isCurrent()) {
            return Collections.emptyList();
        }
        final Party tenant = partyOf(tenantRole);
        final AgreementType bankMandateAgreementType = bankMandateAgreementType();
        final AgreementRoleType debtorRoleType = debtorRoleType();

        return (List) agreements.findByAgreementTypeAndRoleTypeAndParty(
                bankMandateAgreementType, debtorRoleType, tenant);
    }

    // //////////////////////////////////////

    public Lease newMandate(
            final BankAccount bankAccount,
            final @Named("Reference") String reference,
            final @Named("Start Date") LocalDate startDate,
            final @Named("End Date") @Optional LocalDate endDate) {

        final Party creditor = getPrimaryParty();
        final Party debtor = getSecondaryParty();

        final BankMandate bankMandate =
                bankMandates.newBankMandate(reference, reference, startDate, endDate, debtor, creditor, bankAccount);
        paidBy(bankMandate);
        return this;
    }

    public String disableNewMandate(
            final BankAccount bankAccount,
            final String reference,
            final LocalDate startDate,
            final LocalDate endDate) {
        final AgreementRole tenantRole = getSecondaryAgreementRole();
        if (tenantRole == null || !tenantRole.isCurrent()) {
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

    public LocalDate default2NewMandate() {
        return getClockService().now();
    }

    public LocalDate default3NewMandate() {
        return getClockService().now().plusYears(1);
    }

    public String validateNewMandate(
            final BankAccount bankAccount,
            final String reference,
            final LocalDate startDate,
            final LocalDate endDate) {
        final List<BankAccount> validBankAccounts = existingBankAccountsForTenant();
        if (!validBankAccounts.contains(bankAccount)) {
            return "Bank account is not owned by this lease's tenant";
        }
        if (agreements.findAgreementByReference(reference) != null) {
            return "Reference allready exists";
        }
        return null;
    }

    private List<BankAccount> existingBankAccountsForTenant() {
        final Party tenant = getSecondaryParty();
        if (tenant != null) {
            return financialAccounts.findBankAccountsByOwner(tenant);
        } else {
            return Collections.emptyList();
        }
    }

    private AgreementRoleType debtorRoleType() {
        return agreementRoleTypes.findByTitle(BankMandateConstants.ART_DEBTOR);
    }

    private AgreementType bankMandateAgreementType() {
        return agreementTypes.find(BankMandateConstants.AT_MANDATE);
    }

    // //////////////////////////////////////

    @Bulk
    @Prototype
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
    public Lease verify() {
        verifyUntil(ObjectUtils.min(getEffectiveInterval().endDateExcluding(), getClockService().now()));
        return this;
    }

    @Prototype
    public void verifyUntil(final LocalDate date) {
        for (LeaseItem item : getItems()) {
            LocalDateInterval effectiveInterval = item.getEffectiveInterval();
            item.verifyUntil(ObjectUtils.min(effectiveInterval == null ? null : effectiveInterval.endDateExcluding(), date));
        }
    }

    // //////////////////////////////////////

    @Bulk
    public Object calculate(
            final @Named("Run type") InvoiceRunType runType,
            final @Named("Selection") InvoiceCalculationSelection calculationSelection,
            final @Named("Invoice due date") LocalDate invoiceDueDate,
            final @Named("Start due date") LocalDate startDueDate,
            final @Named("Next due date") LocalDate nextDueDate) {
        String runId = invoiceCalculationService.calculateAndInvoice(
                new InvoiceCalculationParameters(
                        this,
                        calculationSelection.selectedTypes(),
                        runType,
                        invoiceDueDate,
                        startDueDate,
                        nextDueDate));
        if (runId != null) {
            return invoiceSummaries.findByRunId(runId);
        }
        getContainer().informUser("No invoices created");
        return this;
    }

    public String validateCalculate(
            final InvoiceRunType runType,
            final InvoiceCalculationSelection selection,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            return "End date cannot be before start date";
        }
        return null;
    }

    // //////////////////////////////////////

    public Lease terminate(
            final @Named("Termination Date") LocalDate terminationDate,
            final @Named("Are you sure?") Boolean confirm) {
        for (Occupancy occupancy : getOccupancies()) {
            if (occupancy.getInterval().contains(terminationDate)) {
                occupancy.terminate(terminationDate);
            }
            // TODO: remove occupancies after the termination date
        }
        // TODO: break options
        setTenancyEndDate(terminationDate);
        return this;
    }

    public LocalDate default0Terminate() {
        return getClockService().now();
    }

    public Boolean default1Terminate() {
        return false;
    }

    public String validateTerminate(
            final LocalDate terminationDate,
            final Boolean confirm) {
        if (terminationDate.isBefore(getStartDate())) {
            return "Termination date can't be before start date";
        }
        return confirm ? null : "Make sure you confirm this action";
    }

    public boolean hideTerminate() {
        return !getStatus().equals(LeaseStatus.ACTIVE);
    }

    // //////////////////////////////////////

    public Lease suspendAll(final @Named("Reason") String reason) {
        for (LeaseItem item : getItems()) {
            item.suspend(reason);
        }
        setStatus(LeaseStatus.SUSPENDED);
        return this;
    }

    public boolean hideSuspendAll() {
        return !getStatus().equals(LeaseStatus.ACTIVE) && !getStatus().equals(LeaseStatus.SUSPENDED_PARTIALLY);
    }

    // //////////////////////////////////////

    public Lease activateAll() {
        for (LeaseItem item : getItems()) {
            item.activate();
        }
        setStatus(LeaseStatus.ACTIVE);
        return this;
    }

    public boolean hideActivateAll() {
        return !getStatus().equals(LeaseStatus.SUSPENDED) && !getStatus().equals(LeaseStatus.SUSPENDED_PARTIALLY);
    }

    // //////////////////////////////////////

    public Lease assign(
            @Named("Reference") final String reference,
            @Named("Name") final String name,
            @Named("Tenant") final Party tenant,
            @Named("Start date") final LocalDate startDate,
            @Named("End date") final LocalDate endDate,
            @Named("Are you sure?") final Boolean confirm
            ) {

        Lease newLease = leases.newLease(
                reference,
                name,
                this.getLeaseType(),
                startDate,
                null, endDate,
                this.getPrimaryParty(),
                tenant);

        LocalDateInterval interval = new LocalDateInterval(startDate, endDate);
        createItemsAndTerms(newLease, startDate);
        createOccupancies(newLease, startDate);
        createBreakOptions(newLease, startDate);
        createAgreementRoleCommunicationChannels(newLease, startDate);
        this.terminate(interval.endDateFromStartDate(), true);
        this.setNext(newLease);
        return newLease;
    }

    private void createItemsAndTerms(final Lease newLease, final LocalDate startDate) {
        for (LeaseItem item : getItems()) {
            LeaseItem newItem = newLease.newItem(
                    item.getType(),
                    item.getCharge(),
                    item.getInvoicingFrequency(),
                    item.getPaymentMethod(),
                    item.getStartDate());
            item.copyTerms(startDate, newItem);
        }
    }

    private void createOccupancies(final Lease newLease, final LocalDate startDate) {
        for (Occupancy occupancy : getOccupancies()) {
            if (occupancy.getInterval().contains(startDate)) {
                Occupancy newOccupancy = newLease.occupy(occupancy.getUnit(), startDate);
                newOccupancy.setActivity(occupancy.getActivity());
                newOccupancy.setBrand(occupancy.getBrand());
                newOccupancy.setSector(occupancy.getSector());
                newOccupancy.setUnitSize(occupancy.getUnitSize());
                newOccupancy.setReportOCR(occupancy.getReportOCR());
                newOccupancy.setReportRent(occupancy.getReportRent());
                ;
                newOccupancy.setReportTurnover(occupancy.getReportTurnover());
            }
        }
    }

    private void createAgreementRoleCommunicationChannels(final Lease newLease, final LocalDate startDate) {
        for (AgreementRole role : getRoles()) {
            AgreementRole newRole = agreementRoles.findByAgreementAndPartyAndTypeAndContainsDate(newLease, role.getParty(), role.getType(), startDate);
            for (AgreementRoleCommunicationChannel agreementRoleCommunicationChannel : role.getCommunicationChannels()) {
                newRole.addCommunicationChannel(agreementRoleCommunicationChannel.getType(), agreementRoleCommunicationChannel.getCommunicationChannel());
            }
        }
    }

    private void createBreakOptions(final Lease newLease, final LocalDate startDate) {
        for (BreakOption option : getBreakOptions()) {
            if (option.getBreakDate().isAfter(startDate)) {
                newLease.newBreakOption(
                        option.getBreakDate(),
                        option.getNotificationPeriod(),
                        option.getExerciseType(),
                        option.getType(),
                        option.getDescription());
            }
        }
    }

    public LocalDate default3Assign() {
        return getClockService().now();
    }

    public LocalDate default4Assign() {
        return getEndDate();
    }

    public String validateAssign(
            final String reference,
            final String name,
            final Party tenant,
            final LocalDate startDate,
            final LocalDate endDate,
            final Boolean confirm
            ) {
        if (endDate.isBefore(startDate)) {
            return "End date can not be start date";
        }
        return leases.findLeaseByReferenceElseNull(reference) == null ? null : "Lease reference already exists,";
    }

    // //////////////////////////////////////

    public Lease renew(
            @Named("Reference") final String reference,
            @Named("Name") final String name,
            @Named("Start date") final LocalDate startDate,
            @Named("End date") final LocalDate endDate,
            @Named("Are you sure?") final Boolean confirm
            ) {
        return assign(reference, name, getSecondaryParty(), startDate, endDate, confirm);
    }

    public String default0Renew() {
        return getReference();
    }

    public String default1Renew() {
        return getName();
    }

    public LocalDate default2Renew() {
        return getInterval().endDateExcluding();
    }

    public String validateRenew(
            final String reference,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final Boolean confirm
            ) {
        if (endDate.isBefore(startDate)) {
            return "End date can not be before start date.";
        }
        return leases.findLeaseByReferenceElseNull(reference) == null ? null : "Lease reference already exists.";
    }

    // //////////////////////////////////////

    public void remove(@Named("Are you sure?") Boolean confirm) {
        if (confirm) {
            doRemove();
        }
    }

    public boolean hideRemove() {
        return !getUser().hasRole(EstatioUserRoles.ADMIN_ROLE);
    }

    @Programmatic
    public boolean doRemove() {
        boolean success = true;

        for (LeaseItem item : getItems()) {
            success = !item.doRemove() ? false : success;
        }
        if (success) {
            getContainer().remove(this);
        }
        return success;
    }

    // //////////////////////////////////////

    private LeaseItems leaseItems;

    public final void injectLeaseItems(final LeaseItems leaseItems) {
        this.leaseItems = leaseItems;
    }

    private Occupancies occupanciesRepo;

    public final void injectOccupancies(final Occupancies occupancies) {
        this.occupanciesRepo = occupancies;
    }

    private FinancialAccounts financialAccounts;

    public final void injectFinancialAccounts(final FinancialAccounts financialAccounts) {
        this.financialAccounts = financialAccounts;
    }

    private BankMandates bankMandates;

    public final void injectBankMandates(final BankMandates bankMandates) {
        this.bankMandates = bankMandates;
    }

    private Leases leases;

    public final void injectLeases(final Leases leases) {
        this.leases = leases;
    }

    private InvoiceCalculationService invoiceCalculationService;

    public final void injectInvoiceCalculationService(final InvoiceCalculationService invoiceCalculationService) {
        this.invoiceCalculationService = invoiceCalculationService;
    }

    private InvoiceSummariesForInvoiceRun invoiceSummaries;

    public final void injectInvoiceSummaries(final InvoiceSummariesForInvoiceRun invoiceSummaries) {
        this.invoiceSummaries = invoiceSummaries;
    }

}
