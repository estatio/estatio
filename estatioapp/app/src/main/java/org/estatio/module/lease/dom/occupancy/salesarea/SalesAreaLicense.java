package org.estatio.module.lease.dom.occupancy.salesarea;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
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

    @Override
    public Agreement changePrevious(final Agreement previousAgreement) {
        // TODO: implement ..
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
}
