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
package org.estatio.module.lease.dom;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.managed.HasManagedIn;
import org.incode.module.base.dom.managed.ManagedIn;
import org.incode.module.base.dom.types.NotesType;
import org.incode.module.base.dom.utils.JodaPeriodUtils;
import org.incode.module.base.dom.utils.StringUtils;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.AgreementRoleCommunicationChannel;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.dom.BankMandateAgreementRoleTypeEnum;
import org.estatio.module.bankmandate.dom.BankMandateAgreementTypeEnum;
import org.estatio.module.bankmandate.dom.BankMandateRepository;
import org.estatio.module.bankmandate.dom.Scheme;
import org.estatio.module.bankmandate.dom.SequenceType;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.amendments.LeaseAmendment;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentRepository;
import org.estatio.module.lease.dom.amendments.Lease_amendments;
import org.estatio.module.lease.dom.amendments.Lease_invoiceCalculations;
import org.estatio.module.lease.dom.amendments.PersistedCalculationResult;
import org.estatio.module.lease.dom.breaks.BreakOption;
import org.estatio.module.lease.dom.breaks.BreakOptionRepository;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
// no @DatastoreIdentity nor @Version, since inherited from supertype
@javax.jdo.annotations.Discriminator("org.estatio.dom.lease.Lease")
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "Lease_externalReference_IDX", members = { "externalReference" }),
        }
)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.Lease "
                        + "WHERE reference == :reference"),
        @javax.jdo.annotations.Query(
                name = "matchByExternalReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.Lease "
                        + "WHERE externalReference.indexOf(:externalReference) >= 0 "
                        + "&& status != LeaseStatus.PREVIEW "
                        + "ORDER BY externalReference DESC "), // somehow DESC in JDOQL does not yield the expected results, so now trying to order in repo
        @javax.jdo.annotations.Query(
                name = "matchByReferenceOrName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.Lease "
                        + "WHERE (reference.matches(:referenceOrName) "
                        + "|| name.matches(:referenceOrName) "
                        + "|| externalReference.matches(:referenceOrName)) "
                        + "&& (:includeTerminated || tenancyEndDate == null || tenancyEndDate >= :date) "
                        + "&& status != LeaseStatus.PREVIEW "
                        + "ORDER BY reference"),
        @javax.jdo.annotations.Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.Lease "
                        + "WHERE occupancies.contains(occ) "
                        + "&& status != LeaseStatus.PREVIEW "
                        + "&& (occ.unit.property == :property) "
                        + "VARIABLES "
                        + "org.estatio.module.lease.dom.occupancy.Occupancy occ "
                        + "ORDER BY reference"),
        @javax.jdo.annotations.Query(
                name = "findByBrand", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.Lease "
                        + "WHERE (occupancies.contains(occ) "
                        + "&& status != LeaseStatus.PREVIEW "
                        + "&& (occ.brand == :brand)) "
                        + "&& (:includeTerminated || tenancyEndDate == null || tenancyEndDate >= :date) "
                        + "VARIABLES "
                        + "org.estatio.module.lease.dom.occupancy.Occupancy occ "
                        + "ORDER BY reference"),
        @javax.jdo.annotations.Query(
                name = "findByAssetAndActiveOnDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.Lease "
                        + "WHERE occupancies.contains(occ) "
                        + "&& status != LeaseStatus.PREVIEW "
                        + "&& (tenancyStartDate == null || tenancyStartDate <= :activeOnDate) "
                        + "&& (tenancyEndDate == null || tenancyEndDate >= :activeOnDate) "
                        + "&& (occ.unit.property == :asset) "
                        + "VARIABLES "
                        + "org.estatio.module.lease.dom.occupancy.Occupancy occ "
                        + "ORDER BY reference"),
        @javax.jdo.annotations.Query(
                name = "findNotExpiredOnDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.Lease "
                        + "WHERE status != LeaseStatus.PREVIEW "
                        + "&& (tenancyEndDate == null || tenancyEndDate >= :notExpiredOnDate) "
                        + "ORDER BY reference"),
        @javax.jdo.annotations.Query(
                name = "findExpireInDateRange", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.Lease " +
                        "WHERE " +
                        "endDate != null && (endDate >= :rangeStartDate && endDate < :rangeEndDate) "
                        + " && status != LeaseStatus.PREVIEW "
                        + " ORDER BY endDate")
})
@DomainObject(autoCompleteRepository = LeaseRepository.class)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class Lease
        extends Agreement
        implements WithApplicationTenancyProperty, WithApplicationTenancyPathPersisted,
        HasManagedIn {

    public Lease() {
        super(LeaseAgreementRoleTypeEnum.LANDLORD, LeaseAgreementRoleTypeEnum.TENANT);
    }

    @Override
    public String title() {
        if (getStatus()==LeaseStatus.PREVIEW){
            return TitleBuilder.start()
                    .withName("LEASE PREVIEW")
                    .withReference(getReference())
                    .toString();
        }
        return super.title();
    }

    @Override
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION, hidden = Where.EVERYWHERE)
    public ManagedIn getManagedIn() {
        if (getAtPath().startsWith("/SWE")) return ManagedIn.FASTNET;
        return ManagedIn.ESTATIO;
    }

    public static class RemoveEvent extends ActionDomainEvent<Lease> {}

    public void created() {
        setStatus(LeaseStatus.ACTIVE);
    }

    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @org.apache.isis.applib.annotation.Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String applicationTenancyPath;

    @PropertyLayout(
            hidden = Where.ALL_TABLES,
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = LeaseStatus.Meta.MAX_LEN)
    @org.apache.isis.applib.annotation.Property(editing = Editing.DISABLED)
    @Getter @Setter
    private LeaseStatus status;

    @Programmatic
    public void resolveStatus(final String reason) {
        final LeaseStatus effectiveStatus = getEffectiveStatus();
        if (effectiveStatus != null && !effectiveStatus.equals(getStatus())) {
            setStatus(effectiveStatus);
        }
    }

    @Programmatic
    public LeaseStatus getEffectiveStatus() {
        List<LeaseItem> all = Lists.newArrayList(getItems());
        int itemCount = getItems().size();
        List<LeaseItemStatus> statusList = Lists.transform(all, leaseItem -> leaseItem.getStatus());
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

    // //////////////////////////////////////

    /**
     * The {@link Property} of the (first of the) {@link #getOccupancies()
     * LeaseUnit}s.
     * <p/>
     * <p/>
     * It is not possible for the {@link Occupancy}s to belong to different
     * {@link Property properties}, and so it is sufficient to obtain the
     * {@link Property} of the first such {@link Occupancy occupancy}.
     */

    @org.apache.isis.applib.annotation.Property(hidden = Where.PARENTED_TABLES)
    public Property getProperty() {
        if (!getOccupancies().isEmpty()) {
            return getOccupancies().first().getUnit().getProperty();
        } else {
            if (getStatus()==LeaseStatus.PREVIEW){
                final LeaseAmendment amendment = getAmendmentByLeasePreview();
                return amendment !=null ? amendment.getLease().getProperty() : null;
            }
        }
        return null;
    }

    private LeaseAmendment getAmendmentByLeasePreview() {
        return leaseAmendmentRepository.findByLeasePreview(this);
    }

    // //////////////////////////////////////

    @Getter @Setter
    @Column(allowsNull = "true", length = ExternalReferenceType.Meta.MAX_LEN)
    private String externalReference;

    public Lease changeExternalReference(final String externalReference) {
        setExternalReference(externalReference);
        return this;
    }

    public String default0ChangeExternalReference(){
        return getExternalReference();
    }

    @javax.jdo.annotations.Column(name = "leaseTypeId", allowsNull = "true")
    @Getter @Setter
    private LeaseType leaseType;

    public Lease change(
            final String name,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LeaseType leaseType) {
        setName(name);
        setLeaseType(leaseType);
        setExternalReference(externalReference);
        return this;
    }

    public String default0Change() {
        return getName();
    }

    public LeaseType default1Change() {
        return getLeaseType();
    }

    public boolean hideChange(){
        return getStatus() == LeaseStatus.PREVIEW;
    }

    @Column(allowsNull = "true", length = NotesType.Meta.MAX_LEN)
    @PropertyLayout(multiLine = 5, hidden = Where.ALL_TABLES)
    @Getter @Setter
    private String comments;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Lease changeComments(
            @ParameterLayout(multiLine = 5)
            final String comments
    ) {
        setComments(comments);
        return this;
    }

    public String default0ChangeComments() {
        return getComments();
    }

    @Column(allowsNull = "true", length = NotesType.Meta.MAX_LEN)
    @PropertyLayout(multiLine = 5, hidden = Where.ALL_TABLES)
    @Getter @Setter
    private String activityComments;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Lease changeActivityComments(
            @ParameterLayout(multiLine = 5)
            final String comments
    ) {
        setActivityComments(comments);
        return this;
    }

    public String default0ChangeActivityComments() {
        return getActivityComments();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    public LocalDate tenancyStartDate;

    @org.apache.isis.applib.annotation.Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    public LocalDate getTenancyStartDate() {
        return tenancyStartDate;
    }

    public void setTenancyStartDate(final LocalDate tenancyStartDate) {
        this.tenancyStartDate = tenancyStartDate;
    }

    @javax.jdo.annotations.Persistent
    public LocalDate tenancyEndDate;

    @org.apache.isis.applib.annotation.Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    public LocalDate getTenancyEndDate() {
        return tenancyEndDate;
    }

    public void setTenancyEndDate(final LocalDate tenancyEndDate) {
        this.tenancyEndDate = tenancyEndDate;
    }

    @org.apache.isis.applib.annotation.Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    public String getTenancyDuration() {
        LocalDateInterval ldi;
        if (getTenancyStartDate() != null && getTenancyEndDate() != null) {
            ldi = getEffectiveInterval();
        } else if (getTenancyStartDate() == null && getTenancyEndDate() != null && getStartDate() != null) {
            ldi = new LocalDateInterval(getStartDate(), getTenancyEndDate());
        } else if (getTenancyStartDate() != null && getTenancyEndDate() == null && getEndDate() != null) {
            ldi = new LocalDateInterval(getTenancyStartDate(), getEndDate());
        } else {
            return null;
        }

        if (ldi.isValid()) {
            return JodaPeriodUtils.asSimpleString(new Period(ldi.asInterval(), PeriodType.yearMonthDay()));
        }

        return null;
    }

    // //////////////////////////////////////

    @Action(
            domainEvent = ChangeDatesEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    public Lease changeTenancyDates(
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
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

    public boolean hideChangeTenancyDates() {
        return getStatus()==LeaseStatus.PREVIEW;
    }

    // //////////////////////////////////////

    @Override
    public boolean hideChangeDates() {
        return getStatus()==LeaseStatus.PREVIEW;
    }

    @Override
    public String disableChangeDates() {
        return !EstatioRole.SUPERUSER.isApplicableFor(getUser()) ? "You need super user rights to change the dates" : null;
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    public Boolean noRentWhenPropertyClosed;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Lease changeNoRentWhenPropertyClosed(
            @Parameter(optionality = Optionality.OPTIONAL) final Boolean noRentWhenPropertyClosed){
        setNoRentWhenPropertyClosed(noRentWhenPropertyClosed);
        return this;
    }


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

    @CollectionLayout(defaultView = "table")
    @javax.jdo.annotations.Persistent(mappedBy = "lease")
    @Getter @Setter
    private SortedSet<Occupancy> occupancies = new TreeSet<>();

    @Programmatic
    public boolean hasOverlappingOccupancies(){
        for (Occupancy O : this.getOccupancies()){
            for (Occupancy O2 : this.getOccupancies() ){
                if (O!=O2 && O.getInterval().overlaps(O2.getInterval())) return true;
            }
        }
        return false;
    }

    /**
     * The action to relate a lease to a unit. A lease can occupy unlimited
     * units.
     *
     * @param unit
     * @param startDate
     * @return
     */
    public Occupancy newOccupancy(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final Unit unit) {
        Occupancy occupancy = occupancyRepository.newOccupancy(this, unit, startDate);
        occupancies.add(occupancy);
        return occupancy;
    }

    public boolean hideNewOccupancy(){
        return getStatus()== LeaseStatus.PREVIEW;
    }

    public LocalDate default0NewOccupancy() {
        return getTenancyStartDate();
    }

    public List<Unit> choices1NewOccupancy() {
        return getProperty()!=null ? unitRepository.findByProperty(getProperty()) : unitRepository.allUnits();
    }

    public String validateNewOccupancy(final LocalDate startDate, final Unit unit){
        if (!unit.isActiveOn(startDate)){
            return "At the start date of the occupancy this unit is not available.";
        }
        return null;
    }

    @Programmatic
    public Occupancy newOccupancy(
            final LocalDate startDate,
            final Unit unit,
            final Occupancy.OccupancyReportingType turnoverReportingType) {
        Occupancy occupancy = occupancyRepository.newOccupancy(this, unit, startDate, turnoverReportingType);
        occupancies.add(occupancy);
        return occupancy;
    }

    @Programmatic
    public Optional<Occupancy> primaryOccupancy() {
        Comparator<Occupancy> byStartDateDescendingNullsFirst = (e1, e2) -> ObjectUtils.compare(e2.getStartDate(), e1.getStartDate(), true);
        Comparator<Occupancy> byUnitAreaDescendingNullsLast = (e1, e2) -> ObjectUtils.compare(e2.getUnit().getArea(), e1.getUnit().getArea(), false);

        //TODO: Dunno why but in order to make the sorted() work we have to convert getOccupancies from SortedSet to List. Argh.
        final List<Occupancy> occupancies = new ArrayList<>(getOccupancies());
        final Optional<Occupancy> first = occupancies.stream().sorted(
                byStartDateDescendingNullsFirst.thenComparing(byUnitAreaDescendingNullsLast)
        ).findFirst();
        return first;

    }

    /**
     * Added to the default fetch group in an attempt to resolve pre-prod error,
     * EST-233.
     */
    @javax.jdo.annotations.Persistent(mappedBy = "lease", defaultFetchGroup = "true")
    @CollectionLayout(defaultView = "table", paged = 999)
    @Getter @Setter
    private SortedSet<LeaseItem> items = new TreeSet<>();

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(defaultView = "table", paged = 999)
    public SortedSet<LeaseItem> getRecentAndFutureItems(){
        SortedSet<LeaseItem> result = new TreeSet<>();
        final List<LeaseItem> activeAndFutureItems = Lists.newArrayList(getItems())
                .stream()
                .filter(li -> li.getInterval().overlaps(LocalDateInterval.including(clockService.now().minusYears(1), null)))
                .collect(Collectors.toList());
        result.addAll(activeAndFutureItems);
        return result;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Lease getOriginalLease(){
        final LeaseAmendment amendment = leaseAmendmentRepository.findByLeasePreview(this);
        if (amendment==null) return null;
        return amendment.getLease();
    }

    public boolean hideOriginalLease(){
        return this.status!=LeaseStatus.PREVIEW;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public LeaseAmendment getAmendment(){
        final LeaseAmendment amendment = leaseAmendmentRepository.findByLeasePreview(this);
        if (amendment==null) return null;
        return amendment;
    }

    public boolean hideAmendment(){
        return this.status!=LeaseStatus.PREVIEW;
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public LeaseItem newItem(
            final LeaseItemType type,
            final LeaseAgreementRoleTypeEnum invoicedBy,
            final Charge charge,
            final InvoicingFrequency invoicingFrequency,
            final PaymentMethod paymentMethod,
            final LocalDate startDate) {
        LeaseItem leaseItem = leaseItemRepository.newLeaseItem(this, type, invoicedBy, charge, invoicingFrequency, paymentMethod, startDate);
        return leaseItem;
    }

    public boolean hideNewItem(){
        return getStatus()==LeaseStatus.PREVIEW;
    }

    public List<Charge> choices2NewItem() {
        return chargeRepository.outgoingChargesForCountry(this.getApplicationTenancy());
    }

    public LeaseAgreementRoleTypeEnum default1NewItem(){
        return LeaseAgreementRoleTypeEnum.LANDLORD;
    }

    public PaymentMethod default4NewItem() {
        return defaultPaymentMethod();
    }

    public LocalDate default5NewItem() {
        return this.getStartDate();
    }

    public String validateNewItem(
            final LeaseItemType type,
            final LeaseAgreementRoleTypeEnum invoicedBy,
            final Charge charge,
            final InvoicingFrequency invoicingFrequency,
            final PaymentMethod paymentMethod,
            final LocalDate startDate) {
        final List<Charge> validCharges = choices2NewItem();
        if (!validCharges.contains(charge)) {
            return String.format(
                    "Charge (with app tenancy level '%s') is not valid for this lease",
                    charge.getApplicationTenancyPath());
        }
        // ECP-769 extra validation for Sweden
        if (getAtPath()!=null && getAtPath().startsWith("/SWE/")){

            switch (type) {
            case SERVICE_CHARGE:
                if (!charge.getReference().equals("SERVICE_CHARGE")) messageService.warnUser("Are you sure? Charge normally is 'service charge' for type service charge");
                break;
            case MARKETING:
                if (!charge.getReference().equals("MARKETING_CONTRIBUTION")) messageService.warnUser("Are you sure? Charge normally is 'marketing contribution' for type marketing");
                break;
            case PROPERTY_TAX:
                if (!charge.getReference().equals("PROPERTY_TAX")) messageService.warnUser("Are you sure? Charge normally is 'property tax' for type property tax");
                break;
            case TURNOVER_RENT:
                if (!charge.getReference().equals("TURNOVER_RENT")) messageService.warnUser("Are you sure? Charge normally is 'turnover rent' for type turnover rent");
                break;
            default:
                messageService.warnUser(String.format("Are you sure? Items of type %s are normally are not entered manually.", type));
            }

        }
        return null;
    }

    public String disableNewItem() {
        return getProperty() == null ? "Please set occupancy first" : null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Lease changePaymentMethodForAll(final PaymentMethod paymentMethod){
        for (LeaseItem item : getItems()){
            item.setPaymentMethod(paymentMethod);
        }
        return this;
    }

    public boolean hideChangePaymentMethodForAll(){
        return getStatus()==LeaseStatus.PREVIEW;
    }

    public Agreement changePrevious(
            @Parameter(optionality = Optionality.OPTIONAL)
            final Agreement previousAgreement) {
        if (getPrevious() != null) {
            getPrevious().setNext(null);
        }
        if (previousAgreement != null) {
            previousAgreement.setNext(this);
        }
        return this;
    }

    public String validateChangePrevious(final Agreement previousAgreement) {
        if (previousAgreement == null) {
            // OK to set to null
            return null;
        }
        if (previousAgreement.getNext() != null) {
            return "Not allowed: the agreement chosen already is already linked to a next.";
        }
        if (this.getEffectiveInterval().overlaps(previousAgreement.getEffectiveInterval())) {
            return "Not allowed: overlapping date intervals";
        }
        // case: interval previous not overlapping, but before this interval
        final LocalDate previousEffectiveEndDate = previousAgreement.getEffectiveInterval().endDate();
        if (previousEffectiveEndDate == null || this.getEffectiveInterval().startDate().isBefore(previousEffectiveEndDate)) {
            return "Not allowed: previous agreement interval should be before this agreements interval";
        }
        Lease previousLease = (Lease) previousAgreement;
        if (!this.getApplicationTenancyPath().equals(previousLease.getApplicationTenancyPath())) {
            return "Not allowed: application tenancy should be equal";
        }
        return null;
    }

    public List<Agreement> autoComplete0ChangePrevious(final String searchPhrase) {
        return agreementRepository.findByTypeAndReferenceOrName(getType(), StringUtils.wildcardToCaseInsensitiveRegex("*".concat(searchPhrase).concat("*")))
                .stream()
                .filter(l->l.getClass().isAssignableFrom(Lease.class))
                .map(Lease.class::cast)
                .filter(l->l.getStatus()!=LeaseStatus.PREVIEW)
                .collect(Collectors.toList());
    }

    public boolean hideChangePrevious(){
        return getStatus()==LeaseStatus.PREVIEW;
    }

    @Programmatic
    public PaymentMethod defaultPaymentMethod(){
        return getItems().size() > 0 ? getItems().last().getPaymentMethod() : null;
    }

    @Programmatic
    public LeaseItem findItem(
            final LeaseItemType itemType,
            final LocalDate itemStartDate,
            final BigInteger sequence) {
        return leaseItemRepository.findLeaseItem(this, itemType, itemStartDate, sequence);
    }

    @Programmatic
    public LeaseItem findItem(
            final LeaseItemType itemType,
            final LocalDate itemStartDate,
            final LeaseAgreementRoleTypeEnum invoicedBy) {
        return leaseItemRepository.findByLeaseAndTypeAndStartDateAndInvoicedBy(this, itemType, itemStartDate, invoicedBy);
    }

    @Programmatic
    public LeaseItem findItem(
            final LeaseItemType itemType,
            final Charge charge,
            final LocalDate itemStartDate,
            final LeaseAgreementRoleTypeEnum invoicedBy) {
        return leaseItemRepository.findByLeaseAndTypeAndChargeAndStartDateAndInvoicedBy(this, itemType, charge, itemStartDate, invoicedBy);
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
        List<LeaseItem> items = new ArrayList<>();
        for (LeaseItem item : getItems()) {
            if (item.getType().equals(type)) {
                items.add(item);
            }
        }
        return items;
    }

    @Programmatic
    public LeaseItem findFirstItemOfTypeAndCharge(final LeaseItemType type, final Charge charge) {
        for (LeaseItem item : getItems()) {
            if (item.getType().equals(type) && item.getCharge().equals(charge)) {
                return item;
            }
        }
        return null;
    }

    @Programmatic
    public LeaseItem findFirstActiveItemOfTypeAndChargeInInterval(final LeaseItemType leaseItemType, final Charge charge, final LocalDateInterval interval){
        List<LeaseItem> itemsOfType = findItemsOfType(leaseItemType);
        for (LeaseItem item : itemsOfType){
            if (item.getCharge().equals(charge) && item.getInterval().overlaps(interval)){
                return item;
            }
        }
        return null;
    }

    // //////////////////////////////////////

    @CollectionLayout(defaultView = "table")
    @javax.jdo.annotations.Persistent(mappedBy = "lease")
    @Getter @Setter
    private SortedSet<BreakOption> breakOptions = new TreeSet<>();

    // //////////////////////////////////////

    @org.apache.isis.applib.annotation.Property(hidden = Where.ALL_TABLES, editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Column(name = "paidByBankMandateId")
    @Getter @Setter
    private BankMandate paidBy;

    // //////////////////////////////////////

    public Lease paidBy(final BankMandate bankMandate) {
        setPaidBy(bankMandate);
        return this;
    }

    public String disablePaidBy() {
        final List<BankMandate> validMandates = existingBankMandatesForTenant();
        if (validMandates.isEmpty()) {
            return "There are no valid mandates; set one up using 'New Mandate'";
        }
        return null;
    }

    public boolean hidePaidBy() {
        return getStatus()==LeaseStatus.PREVIEW;
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

        return (List) agreementRepository.findByAgreementTypeAndRoleTypeAndParty(
                bankMandateAgreementType, debtorRoleType, tenant);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Lease newMandate(
            final BankAccount bankAccount,
            final @Parameter(regexPattern = org.incode.module.base.dom.types.ReferenceType.Meta.REGEX, regexPatternReplacement = org.incode.module.base.dom.types.ReferenceType.Meta.REGEX_DESCRIPTION) String reference,
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate,
            final SequenceType sequenceType,
            final Scheme scheme,
            final LocalDate signatureDate) {

        final Party creditor = primaryPartyAsOfElseCurrent(startDate);
        final Party debtor = secondaryPartyAsOfElseCurrent(startDate);

        final BankMandate bankMandate =
                bankMandateRepository.newBankMandate(
                        reference,
                        reference,
                        startDate,
                        endDate,
                        debtor,
                        creditor,
                        bankAccount,
                        sequenceType,
                        scheme,
                        signatureDate);
        paidBy(bankMandate);
        return this;
    }

    public String disableNewMandate() {
        final AgreementRole tenantRole = getSecondaryAgreementRole();
        if (tenantRole == null || !tenantRole.isCurrent()) {
            return "Could not determine the tenant (secondary party) of this lease";
        }
        final List<? extends FinancialAccount> validBankAccounts = existingBankAccountsForTenant();
        if (validBankAccounts.isEmpty()) {
            return "There are no bank accounts available for this tenant";
        }
        return null;
    }

    public boolean hideNewMandate() {
        return getStatus()==LeaseStatus.PREVIEW;
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
            final LocalDate endDate,
            final SequenceType sequenceType,
            final Scheme scheme,
            final LocalDate signatureDate) {
        final List<? extends FinancialAccount> validBankAccounts = existingBankAccountsForTenant();
        if (!validBankAccounts.contains(bankAccount)) {
            return "Bank account is not owned by this lease's tenant";
        }
        if (bankMandateRepository.findByReference(reference) != null) {
            return "Reference already exists";
        }
        if (findItemWithPaymentMethod(PaymentMethod.DIRECT_DEBIT).isEmpty()){
            return "No items with payment method direct debit found";
        }
        return null;
    }

    private List<LeaseItem> findItemWithPaymentMethod(final PaymentMethod paymentMethod){
        return Lists.newArrayList(getItems()).stream().filter(x->x.getPaymentMethod()==paymentMethod).collect(Collectors.toList());
    }

    private List<BankAccount> existingBankAccountsForTenant() {
        final Party tenant = getSecondaryParty();
        if (tenant != null) {
            return bankAccountRepository.findBankAccountsByOwner(tenant);
        } else {
            return Collections.emptyList();
        }
    }

    private AgreementRoleType debtorRoleType() {
        return agreementRoleTypeRepository.find(BankMandateAgreementRoleTypeEnum.DEBTOR);
    }

    private AgreementType bankMandateAgreementType() {
        return agreementTypeRepository.find(BankMandateAgreementTypeEnum.MANDATE);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Lease verify() {
        verifyUntil(ObjectUtils.min(getEffectiveInterval().endDateExcluding(), getClockService().now()));
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Lease verifyUntil(final LocalDate date) {
        for (LeaseItem item : getItems()) {
            LocalDateInterval effectiveInterval = item.getEffectiveInterval();
            item.verifyUntil(ObjectUtils.min(effectiveInterval == null ? null : effectiveInterval.endDateExcluding(), date));
        }
        return this;
    }

    // //////////////////////////////////////

    @Action(domainEvent = Lease.TerminateEvent.class, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Lease terminate(
            final LocalDate terminationDate) {
        // TODO: remove occupancies after the termination date
        // TODO: break options
        setTenancyEndDate(terminationDate);
        return this;
    }

    public boolean hideTerminate(){
        return getStatus()==LeaseStatus.PREVIEW;
    }

    public LocalDate default0Terminate() {
        return getClockService().now();
    }

    public String validateTerminate(
            final LocalDate terminationDate) {
        if (terminationDate.isBefore(getStartDate())) {
            return "Termination date can't be before start date";
        }
        return null;
    }

    public String disableTerminate() {
        if (!(getStatus().equals(LeaseStatus.ACTIVE) || getStatus().equals(LeaseStatus.SUSPENDED_PARTIALLY))) {
            return "Status is not Active or Suspended Partially";
        }
        return null;
    }

    // //////////////////////////////////////

    @Action(domainEvent = Lease.SuspendAllEvent.class)
    public Lease suspendAll(final String reason) {
        for (LeaseItem item : getItems()) {
            item.suspend(reason);
        }
        return this;
    }

    public boolean hideSuspendAll() {
        return !getStatus().equals(LeaseStatus.ACTIVE) && !getStatus().equals(LeaseStatus.SUSPENDED_PARTIALLY);
    }

    // //////////////////////////////////////

    @Action(domainEvent = ResumeAllEvent.class)
    public Lease resumeAll() {
        for (LeaseItem item : getItems()) {
            item.doResume();
        }
        return this;
    }

    public boolean hideResumeAll() {
        return !getStatus().equals(LeaseStatus.SUSPENDED) && !getStatus().equals(LeaseStatus.SUSPENDED_PARTIALLY);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Lease assign(
            @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION) final String reference,
            final String name,
            final Party tenant,
            final LocalDate tenancyStartDate
    ) {
        // TODO: this should look up primary and secondary parties for startDate, so callers
        //  should check/disable if none defined for that period.

        Lease newLease = copyToNewLease(reference, name, tenant, getStartDate(), getEndDate(), tenancyStartDate, null, true);
        this.terminate(LocalDateInterval.endDateFromStartDate(tenancyStartDate));
        return newLease;
    }

    public boolean hideAssign(){
        return getStatus()==LeaseStatus.PREVIEW;
    }

    public LocalDate default3Assign() {
        return getClockService().now();
    }

    public List<Party> choices2Assign() {
        return partyRepository.allParties()
                .stream()
                .filter(party -> {
                    if (party.getAtPath().startsWith("/BEL")) {
                        return party.getReference().startsWith("BECL");
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    public String validateAssign(
            final String reference,
            final String name,
            final Party tenant,
            final LocalDate startDate
    ) {
        if (startDate.isBefore(getStartDate())){
            return "The start date cannot be before the start date of the lease";
        }
        return leaseRepository.findLeaseByReferenceElseNull(reference) == null ? null : "Lease reference already exists,";
    }

    // //////////////////////////////////////

    Lease copyToNewLease(
            final String reference,
            final String name,
            final Party tenant,
            final LocalDate startDate,
            final LocalDate endDate,
            final LocalDate tenancyStartDate,
            final LocalDate tenancyEndDate,
            boolean copyEpochDate) {

        Lease newLease = leaseRepository.newLease(
                this.getApplicationTenancy(),
                reference,
                name,
                this.getLeaseType(),
                startDate,
                endDate,
                tenancyStartDate,
                tenancyEndDate,
                this.primaryPartyAsOfElseCurrent(tenancyStartDate),
                tenant);

        copyOccupancies(newLease, tenancyStartDate);
        copyItemsAndTerms(newLease, tenancyStartDate, copyEpochDate);
        breakOptionRepository.copyBreakOptions(this, newLease, tenancyStartDate);
        copyAgreementRoleCommunicationChannels(newLease, tenancyStartDate);
        newLease.setComments(this.getComments());
        this.setNext(newLease);
        return newLease;
    }

    void copyItemsAndTerms(final Lease newLease, final LocalDate startDate, boolean copyEpochDate) {
        for (LeaseItem item : getItems()) {
            if (item.getEndDate()==null || item.getEndDate().isAfter(startDate)) {
                LeaseItem newItem = newLease.newItem(
                        item.getType(),
                        item.getInvoicedBy(),
                        item.getCharge(),
                        item.getInvoicingFrequency(),
                        item.getPaymentMethod(),
                        item.getStartDate()
                );
                newItem.setEndDate(item.getEndDate()); // ECP-1229: now also original item's end date is copied over
                if (copyEpochDate && item.getEpochDate() != null) {
                    newItem.setEpochDate(item.getEpochDate());
                }
                if (startDate == null) {
                    item.copyTerms(startDate, newItem);
                } else {
                    // ECP-1222 in some edge cases terms that were ending on the tenancy enddate and therefore were not followed by new terms by means of a verify action
                    // could result in a new item with no terms copied over when renewing and thus losing indexation information for example
                    // using copyAllTermsStartingFrom with startDate.minusDays(1) solves this issue
                    item.copyAllTermsStartingFrom(startDate.minusDays(1), newItem);
                }
            }
        }
    }

    private void copyOccupancies(final Lease newLease, final LocalDate startDate) {
        for (Occupancy occupancy : this.getOccupancies()) {
            if (occupancy.getInterval().contains(startDate) || occupancy.getInterval().endDateExcluding().equals(startDate)) {
                Occupancy newOccupancy = newLease.newOccupancy(startDate, occupancy.getUnit(), occupancy.getReportTurnover());
                newOccupancy.setActivity(occupancy.getActivity());
                newOccupancy.setBrand(occupancy.getBrand());
                newOccupancy.setSector(occupancy.getSector());
                newOccupancy.setUnitSize(occupancy.getUnitSize());
                newOccupancy.setReportOCR(occupancy.getReportOCR());
                newOccupancy.setReportRent(occupancy.getReportRent());
                // ECP-1246: we now copy using the LEASE#newOccupancy programmatic action in order to trigger the occupancy created event with the correct param
//                newOccupancy.setReportTurnover(occupancy.getReportTurnover());
            }
        }
    }

    private void copyAgreementRoleCommunicationChannels(final Lease newLease, final LocalDate startDate) {
        if (getSecondaryParty() == newLease.getSecondaryParty()) {
            // renew
            for (AgreementRole role : getRoles()) {
                AgreementRole newRole = agreementRoleRepository.findByAgreementAndPartyAndTypeAndContainsDate(newLease, role.getParty(), role.getType(), startDate);
                if (newRole != null) {
                    for (AgreementRoleCommunicationChannel agreementRoleCommunicationChannel : role.getCommunicationChannels()) {
                        newRole.addCommunicationChannel(
                                agreementRoleCommunicationChannel.getType(),
                                agreementRoleCommunicationChannel.getCommunicationChannel(),
                                startDate);
                    }
                }
            }
        }
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Lease renew(
            @Parameter(
                    regexPattern = ReferenceType.Meta.REGEX,
                    regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION)
            final String reference,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        Party tenant = secondaryPartyAsOfElseCurrent(startDate);
        LocalDate endDateTenancy = endDate;
        if (applicationTenancyPath.startsWith("/SWE")) {
            endDateTenancy = null;
        }
        final Lease newLease = copyToNewLease(reference, name, tenant, startDate, endDate, startDate, endDateTenancy, false);

        if (newLease != null){
            wrapperFactory.wrapSkipRules(this).terminate(startDate.minusDays(1));
        }
        return newLease;
    }

    public boolean hideRenew(){
        return getStatus()==LeaseStatus.PREVIEW;
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
            final LocalDate endDate
    ) {
        if (endDate.isBefore(startDate)) {
            return "End date can not be before start date.";
        }
        return leaseRepository.findLeaseByReferenceElseNull(reference) == null ? null : "Lease reference already exists.";
    }

    public String disableRenew() {
        if(getNext() != null){
            return "Cannot renew when there is a next lease";
        }
        return null;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, domainEvent = Lease.RemoveEvent.class)
    public void remove(final String reason) {
        for (BreakOption breakOption : getBreakOptions()){
            breakOption.remove(reason);
        }
        for (Occupancy occupancy : getOccupancies()){
            occupancy.remove();
        }
        if (getStatus()==LeaseStatus.PREVIEW) {
            for (PersistedCalculationResult result : factoryService.mixin(Lease_invoiceCalculations.class, this).$$()) {
                result.remove();
            }
        }
        for (LeaseItem item : getItems()) {
            item.remove();
        }
        final Lease_amendments mixin = factoryService.mixin(Lease_amendments.class, this);
        for (LeaseAmendment leaseAmendment : mixin.$$()){
            leaseAmendment.remove();
        }
        final LeaseAmendment amendmentIfAny = getAmendmentByLeasePreview();
        if (amendmentIfAny!=null){
            amendmentIfAny.setLeasePreview(null);
        }
        remove(this);
    }

    public boolean hideRemove() {
        if (getStatus()==LeaseStatus.PREVIEW) return false;
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(getUser());
    }

    public String disableRemove() {
        if (getNext() != null){
            return "Cannot remove lease that has successor";
        }
        return null;
    }

    @Override
    public boolean hideNewRole(){
        return getStatus()== LeaseStatus.PREVIEW;
    }

    @Programmatic
    public SortedSet<LocalDate> dueDatesInRange(LocalDate startDueDate, LocalDate nextDueDate) {
        final SortedSet<LocalDate> dates = Sets.newTreeSet();
        for (LeaseItem leaseItem : getItems()) {
            dates.addAll(leaseItem.dueDatesInRange(startDueDate, nextDueDate));
        }
        return dates;
    }

    public static class TerminateEvent extends ActionDomainEvent<Lease> {
        private static final long serialVersionUID = 1L;

        public LocalDate getTerminationDate() {
            return (LocalDate) (this.getArguments().isEmpty() ? null : getArguments().get(0));
        }
    }

    public static class SuspendAllEvent extends ActionDomainEvent<Lease> {
        private static final long serialVersionUID = 1L;

    }

    public static class ResumeAllEvent extends ActionDomainEvent<Lease> {
        private static final long serialVersionUID = 1L;

    }

    public static class ChangeDatesEvent extends ActionDomainEvent<Lease> {
        private static final long serialVersionUID = 1L;

        public LocalDate getNewTenancyStartDate() {
            return (LocalDate) (this.getArguments().isEmpty() ? null : getArguments().get(0));
        }

        public LocalDate getNewTenancyEndDate() {
            return (LocalDate) (this.getArguments().isEmpty() ? null : getArguments().get(1));
        }
    }

    // //////////////////////////////////////

    @Inject
    public LeaseItemRepository leaseItemRepository;

    @Inject
    OccupancyRepository occupancyRepository;

    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject
    BankMandateRepository bankMandateRepository;

    @Inject LeaseRepository leaseRepository;

    @Inject
    UnitRepository unitRepository;

    @Inject
    BreakOptionRepository breakOptionRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    private WrapperFactory wrapperFactory;

    @Inject MessageService messageService;

    @Inject FactoryService factoryService;

    @Inject LeaseAmendmentRepository leaseAmendmentRepository;



    // //////////////////////////////////////

    public static class ReferenceType {

        private ReferenceType() {}

        public static class Meta {

            //(?=(?:.{11,15}|.{17}))([X,Z]{1}-)?([A-Z]{3}-([A-Z,0-9]{3,8})-[A-Z,0-9,\&+=_/-]{1,7})
            //public static final String REGEX = "(?=.{11,17})([A-Z]{1}-)?([A-Z]{3}-([A-Z,0-9]{3,8})-[A-Z,0-9,\\&+=_/-]{1,7})";
            //public static final String REGEX = "^([X,Z]-)?(?=.{11,15}$)([A-Z]{3})-([A-Z,0-9]{3,8})-([A-Z,0-9,\\&+=_/-]{1,7})$";
            public static final String REGEX = "^([X,Z]-)?(?=.{3,15}$)([A-Z]{2,4})-([A-Z,0-9,\\&\\ \\+=_/-]{1,15})$";
            public static final String REGEX_DESCRIPTION = "Only letters and numbers devided by at least 2 and at most 4 dashes:\"-\" totalling between 8 and 15 characters. ";

            private Meta() {}

        }

    }

    public static class ExternalReferenceType {

        private ExternalReferenceType() {}

        public static class Meta {

            public static final int MAX_LEN = org.incode.module.base.dom.types.ReferenceType.Meta.MAX_LEN;

            private Meta() {}

        }
    }

}
