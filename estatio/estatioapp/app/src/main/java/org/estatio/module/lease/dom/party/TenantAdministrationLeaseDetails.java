package org.estatio.module.lease.dom.party;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Unique;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.types.MoneyType;
import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.lease.dom.Lease;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.party.TenantAdministrationLeaseDetails "
                        + "WHERE tenantAdministrationRecord == :tenantAdministrationRecord && "
                        + "lease == :lease "),
})
@Unique(name = "TenantAdministrationLeaseDetails_tenantAdministrationRecord_lease_UNQ", members = {"tenantAdministrationRecord", "lease"})
@DomainObject(objectType = "party.TenantAdministrationLeaseDetails")
public class TenantAdministrationLeaseDetails implements Comparable{

    public String title(){
        return TitleBuilder.start().withParent(getTenantAdministrationRecord()).withName(getLease()).toString();
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "tenantAdministrationRecordId")
    private TenantAdministrationRecord tenantAdministrationRecord;

    @Getter @Setter
    @Column(allowsNull = "false", name = "leaseId")
    private Lease lease;

    @Getter @Setter
    @Column(allowsNull = "false", scale = MoneyType.Meta.SCALE)
    private BigDecimal declaredAmountOfClaim;

    @Getter @Setter
    @Column(allowsNull = "true")
    private Boolean debtAdmitted;

    @Getter @Setter
    @Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    private BigDecimal admittedAmountOfClaim;

    @Getter @Setter
    @Column(allowsNull = "true")
    private Boolean leaseContinued;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public TenantAdministrationLeaseDetails changeLeaseDetails(
            final BigDecimal declaredAmountOfClaim,
            @Nullable
            final Boolean debtAdmitted,
            @Nullable
            final BigDecimal admittedAmountOfClaim,
            @Nullable
            final Boolean leaseContinued
            ){
        setDeclaredAmountOfClaim(declaredAmountOfClaim);
        setDebtAdmitted(debtAdmitted);
        setAdmittedAmountOfClaim(admittedAmountOfClaim);
        setLeaseContinued(leaseContinued);
        return this;
    }

    public BigDecimal default0ChangeLeaseDetails(){
        return getDeclaredAmountOfClaim();
    }

    public Boolean default1ChangeLeaseDetails(){
        return getDebtAdmitted();
    }

    public BigDecimal default2ChangeLeaseDetails(){
        return getAdmittedAmountOfClaim();
    }

    public Boolean default3ChangeLeaseDetails(){
        return getLeaseContinued();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<EntryValueForLease> getValuesOfContinuationPlan(){
        return entryValueForLeaseRepository.findByLeaseDetails(this);
    }

    @Override
    public int compareTo(final Object o) {
        TenantAdministrationLeaseDetails casted = (TenantAdministrationLeaseDetails) o;
        return getLease().compareTo(casted.getLease());
    }

    @Inject EntryValueForLeaseRepository entryValueForLeaseRepository;

}
