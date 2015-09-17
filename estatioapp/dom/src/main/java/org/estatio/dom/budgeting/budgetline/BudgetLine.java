package org.estatio.dom.budgeting.budgetline;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.budgetkeyitem.BudgetKeyItem;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
//       ,schema = "budget"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@DomainObject(editing = Editing.DISABLED)
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByBudgetItem", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.budgetline.BudgetLine " +
                        "WHERE budgetItem == :budgetItem")
})
public class BudgetLine extends EstatioDomainObject<BudgetKeyItem>
        implements WithApplicationTenancyProperty {

    public BudgetLine() {
        super("budgetItem, budgetKeyItem");
    }

    public BudgetLine(
            final BigDecimal value,
            final BudgetItem budgetItem,
            final BudgetKeyItem budgetKeyItem) {

        this();
        this.value = value;
        this.budgetItem = budgetItem;
        this.budgetKeyItem = budgetKeyItem;
    }

    //region > value (property)
    private BigDecimal value;

    @Column(allowsNull = "false", scale = 2)
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }
    //endregion


    @PropertyLayout(hidden = Where.EVERYWHERE)
    public BigDecimal getSourceValue() {
        return getBudgetKeyItem().getValue();
    }


    //region > budgetItem (property)
    private BudgetItem budgetItem;

    @Column(allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    public BudgetItem getBudgetItem() {
        return budgetItem;
    }

    public void setBudgetItem(final BudgetItem budgetItem) {
        this.budgetItem = budgetItem;
    }
    //endregion

    //region > budgetKeyItem (property)
    private BudgetKeyItem budgetKeyItem;

    @Column(allowsNull = "false")
    public BudgetKeyItem getBudgetKeyItem() {
        return budgetKeyItem;
    }

    public void setBudgetKeyItem(final BudgetKeyItem budgetKeyItem) {
        this.budgetKeyItem = budgetKeyItem;
    }
    //endregion

    @Programmatic
    public void deleteBudgetLine() {
        removeIfNotAlready(this);
    }

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudgetItem().getApplicationTenancy();
    }
}
