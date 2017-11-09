package org.estatio.module.budget.dom.budgetitem;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByBudgetItemAndType", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.budgetitem.BudgetItemValue " +
                        "WHERE budgetItem == :budgetItem && type == :type "),
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.budgetitem.BudgetItemValue " +
                        "WHERE budgetItem == :budgetItem && date == :date && type == :type ")
})
@Unique(name = "BudgetItemValue_budgetItem_date_type", members = { "budgetItem", "date", "type" })
@DomainObject(
        objectType = "org.estatio.dom.budgeting.budgetitem.BudgetItemValue"
)
public class BudgetItemValue extends UdoDomainObject2<BudgetItemValue>{

    public BudgetItemValue(){
        super("budgetItem, date, type");
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getBudgetItem())
                .withName(getValue())
                .toString();
    }

    @Column(name = "budgetItemId", allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private BudgetItem budgetItem;

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal value;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate date;

    @Column(allowsNull = "false")
    @Getter @Setter
    private BudgetCalculationType type;

    @Override
    @Property(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudgetItem().getApplicationTenancy();
    }
}
