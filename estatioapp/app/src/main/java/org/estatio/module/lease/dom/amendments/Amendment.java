package org.estatio.module.lease.dom.amendments;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
// identityType=IdentityType.DATASTORE inherited from superclass
@Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
// no @DatastoreIdentity nor @Version, since inherited from supertype
@Discriminator("org.estatio.module.lease.dom.amendments.Amendment")
@Queries({
        @Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amendments.Amendment "
                        + "WHERE lease == :lease "),
        @Query(
                name = "findByState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amendments.Amendment "
                        + "WHERE state == :state ")
})
@DomainObject(editing = Editing.DISABLED)
public class Amendment extends Agreement {

    public Amendment() {
        super(LeaseAgreementRoleTypeEnum.LANDLORD, LeaseAgreementRoleTypeEnum.TENANT);
    }

    @Column(name = "leaseId", allowsNull = "false")
    @Getter @Setter
    private Lease lease;

    @Column(allowsNull = "false")
    @Getter @Setter
    private AmendmentState state;

    @Getter @Setter
    @Persistent(mappedBy = "amendment", dependentElement = "true")
    private SortedSet<AmendmentItem> items = new TreeSet<>();

    @Override
    @ActionLayout(hidden = Where.EVERYWHERE)
    public Agreement changePrevious(final Agreement previousAgreement) {
        return null;
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return lease.getApplicationTenancy();
    }

    @Programmatic
    public void remove(){
        repositoryService.removeAndFlush(this);
    }

    @Inject RepositoryService repositoryService;

}
