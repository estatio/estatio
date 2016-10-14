package org.estatio.dom.budgetassignment;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Occupancy;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "estatioBudgetassignment"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByOccupancy", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgetassignment.ServiceChargeItem " +
                        "WHERE occupancy == :occupancy "),
        @Query(
                name = "findByOccupancyAndCharge", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgetassignment.ServiceChargeItem " +
                        "WHERE occupancy == :occupancy && charge == :charge")
})
@Unique(name = "ServiceChargeItem_occupancy_charge_UNQ", members = { "occupancy", "charge" })
@DomainObject()
public class ServiceChargeItem extends UdoDomainObject2<ServiceChargeItem> implements WithApplicationTenancyProperty {

    public ServiceChargeItem()  {
        super("occupancy, charge");
    }

    public String title() {
        return TitleBuilder.start()
                .withName("Service Charge Item - ")
                .withName(getCharge().getReference())
                .toString();
    }

    @Override public ApplicationTenancy getApplicationTenancy() {
        return getOccupancy().getApplicationTenancy();
    }

    @Column(name = "occupancyId", allowsNull = "false")
    @Getter @Setter
    private Occupancy occupancy;

    @Column(name = "chargeId", allowsNull = "false")
    @Getter @Setter
    private Charge charge;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public ServiceChargeItem calculate() {
        return this;
    }

}
