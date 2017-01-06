package org.estatio.dom.budgetassignment.override;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.timestamp.Timestampable;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.budgetcalculation.Status;

import lombok.Getter;
import lombok.Setter;


@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Unique(name = "BudgetOverrideValue_budgetOverride_type_UNQ", members = { "budgetOverride", "type" })
@javax.jdo.annotations.Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgetassignment.override.BudgetOverrideValue " +
                        "WHERE budgetOverride == :budgetOverride && "
                        + "type == :type")
})

@DomainObject(
        objectType = "org.estatio.dom.budgetassignment.override.BudgetOverrideValue",
        auditing = Auditing.DISABLED,
        publishing = Publishing.DISABLED
)
public class BudgetOverrideValue extends UdoDomainObject2<BudgetOverrideValue>
        implements WithApplicationTenancyProperty, Timestampable {

    public BudgetOverrideValue() {
        super("budgetOverride, type, value");
    }

    public String title(){
        return TitleBuilder.start()
                .withParent(getBudgetOverride())
                .withName(" ")
                .withName(getValue())
                .toString();
    }

    @Getter @Setter
    @Column(name = "budgetOverrideId", allowsNull = "false")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private BudgetOverride budgetOverride;

    @Getter @Setter
    @Column(scale = 2, allowsNull = "false")
    private BigDecimal value;

    @Getter @Setter
    @Column(allowsNull = "false")
    private BudgetCalculationType type;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Status status;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(hidden = Where.ALL_TABLES)
    private Timestamp updatedAt;

    @Getter @Setter
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Column(allowsNull = "true")
    private String updatedBy;

    @Programmatic
    public void finalizeOverrideValue() {
        setStatus(Status.ASSIGNED);
    }

    @Programmatic
    public void removeWithStatusNew(){
        if (getStatus()==Status.NEW) {
            repositoryService.removeAndFlush(this);
        }
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return budgetOverride.getApplicationTenancy();
    }

    @Inject
    RepositoryService repositoryService;

}
