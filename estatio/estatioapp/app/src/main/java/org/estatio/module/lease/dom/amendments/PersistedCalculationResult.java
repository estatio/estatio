package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.AbstractInterval;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationService;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByLeaseTerm", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amendments.PersistedCalculationResult "
                        + "WHERE leaseTerm == :leaseTerm "),
})
@Indices({
        @Index(name = "PersistedCalculationResult_leaseTerm_IDX", members = { "leaseTerm" })
})
@DomainObject(objectType = "org.estatio.module.lease.dom.amendments.PersistedCalculationResult")
public class PersistedCalculationResult extends UdoDomainObject2<PersistedCalculationResult>  {

    public PersistedCalculationResult() {
        super("leaseTerm, value, invoicingStartDate, invoicingEndDate, invoicingDueDate, effectiveStartDate, effectiveEndDate");
    }

    public PersistedCalculationResult(final InvoiceCalculationService.CalculationResult calculationResult, final LeaseTerm leaseTerm){
        this();
        this.leaseTerm = leaseTerm;
        this.value = calculationResult.value();
        this.invoicingStartDate = calculationResult.invoicingInterval().startDate();
        this.invoicingEndDate = calculationResult.invoicingInterval().endDate(AbstractInterval.IntervalEnding.INCLUDING_END_DATE);
        this.invoicingDueDate = calculationResult.invoicingInterval().dueDate();
        this.effectiveStartDate = calculationResult.effectiveInterval().startDate();
        this.effectiveEndDate = calculationResult.effectiveInterval().endDate(AbstractInterval.IntervalEnding.INCLUDING_END_DATE);
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "leaseTermId")
    @MemberOrder(sequence = "5")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private LeaseTerm leaseTerm;

    @Getter @Setter
    @Column(allowsNull = "false", scale = 2)
    @MemberOrder(sequence = "1")
    private BigDecimal value;

    @Getter @Setter
    @Column(allowsNull = "false")
    @MemberOrder(sequence = "2")
    private LocalDate invoicingStartDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    @MemberOrder(sequence = "3")
    private LocalDate invoicingEndDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    @MemberOrder(sequence = "4")
    private LocalDate invoicingDueDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    @MemberOrder(sequence = "6")
    private LocalDate effectiveStartDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    @MemberOrder(sequence = "7")
    private LocalDate effectiveEndDate;

    @Programmatic
    public void remove(){
        repositoryService.removeAndFlush(this);
    }

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return leaseTerm.getApplicationTenancy();
    }

    @Inject
    RepositoryService repositoryService;
}
