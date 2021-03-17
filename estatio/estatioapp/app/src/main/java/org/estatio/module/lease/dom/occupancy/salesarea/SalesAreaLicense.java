package org.estatio.module.lease.dom.occupancy.salesarea;

import java.math.BigDecimal;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.occupancy.Occupancy;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
// identityType=IdentityType.DATASTORE inherited from superclass
@Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
// no @DatastoreIdentity nor @Version, since inherited from supertype
@Discriminator("org.estatio.module.lease.dom.occupancy.salesarea.SalesAreaLicense")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByOccupancy", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.occupancy.salesarea.SalesAreaLicense "
                        + "WHERE occupancy == :occupancy")
})
@DomainObject(editing = Editing.DISABLED)
public class SalesAreaLicense extends Agreement {

    public SalesAreaLicense() {
        super(LeaseAgreementRoleTypeEnum.LANDLORD, LeaseAgreementRoleTypeEnum.TENANT);
    }

    @Column(allowsNull = "false", name = "occupancyId")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private Occupancy occupancy;

    @Column(scale = 2, allowsNull = "true")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private BigDecimal salesAreaNonFood;

    @Column(scale = 2, allowsNull = "true")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private BigDecimal salesAreaFood;

    @Column(scale = 2, allowsNull = "true")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private BigDecimal foodAndBeveragesArea;

    @Action(semantics = SemanticsOf.SAFE)
    public LocalDate getEffectiveEndDate(){
        return getEndDate()!=null ? getEndDate() : getOccupancy().getEffectiveEndDate();
    }

    @Action
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public SalesAreaLicense createNext(
            final LocalDate startDate,
            @Nullable
            final BigDecimal salesAreaNonFood,
            @Nullable
            final BigDecimal salesAreaFood,
            @Nullable
            final BigDecimal foodAndBeveragesArea){
        final SalesAreaLicense license = salesAreaLicenseRepository
                .newSalesAreaLicense(getOccupancy(), getOccupancy().getLease().getReference().concat("-" + version()), getOccupancy().getLease().getReference().concat("-SAL").concat(version()), startDate,
                        getSecondaryParty(), getPrimaryParty(), salesAreaNonFood, salesAreaFood,
                        foodAndBeveragesArea);
        this.setNext(license);
        this.setEndDate(startDate.minusDays(1).isBefore(this.getStartDate()) ? startDate : startDate.minusDays(1));
        return license;
    }

    public String validateCreateNext(
            final LocalDate startDate,
            @Nullable
            final BigDecimal salesAreaFood,
            @Nullable
            final BigDecimal salesAreaNonFood,
            @Nullable
            final BigDecimal foodAndBeveragesArea){
        return validate(getOccupancy(), this, startDate, salesAreaFood, salesAreaNonFood, foodAndBeveragesArea);
    }

    public static String validate(final Occupancy o, final SalesAreaLicense l, final LocalDate s, final BigDecimal saf, final BigDecimal sanf, final BigDecimal fba){
        if (l!=null && l.getStartDate()!=null && s.isBefore(l.getStartDate())){
            return "The start date cannot be before the current start date";
        }
        if (saf==null && sanf==null && fba==null){
            return "Please fill in at least 1 area";
        }
        if (o.getEndDate()!=null && s.isAfter(o.getEndDate())){
            return "The license start date cannot be after the occupancy end date";
        }
        if (o.getEffectiveEndDate()!=null && s.isAfter(o.getEffectiveEndDate())){
            return "The license start date cannot be after the occupancy effective end date";
        }
        if (o.getStartDate()!=null && s.isBefore(o.getStartDate())){
            return "The license start date cannot be before the occupancy start date";
        }
        if (o.getEffectiveStartDate()!=null && s.isBefore(o.getEffectiveStartDate())){
            return "The license start date cannot be before the occupancy effective start date";
        }
        return null;
    }

    public String disableCreateNext(){
        if (getNext()!=null){
            return "There is a next already";
        }
        return null;
    }

    public LocalDate default0CreateNext(){
        return clockService.now();
    }

    private String version(){
        SalesAreaLicense license = this;
        Integer i = 1;
        while (license.getPrevious()!=null){
            i++;
            license = (SalesAreaLicense) license.getPrevious();
        }
        return i.toString();
    }

    @Override
    @ActionLayout(hidden = Where.EVERYWHERE)
    public Agreement changePrevious(final Agreement previousAgreement) {
        // TODO: implement .. ? I don't see a usecase at the moment
        return null;
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }

    @Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Getter @Setter
    @Property(hidden = Where.EVERYWHERE)
    private String applicationTenancyPath;

    @Programmatic
    public void remove(){
        if (getPrevious()==null){
            repositoryService.removeAndFlush(this);
        } else {
            // we do not want occupancies with a history of sales area licences to be removed
        }
    }

    @Inject
    SalesAreaLicenseRepository salesAreaLicenseRepository;
}
