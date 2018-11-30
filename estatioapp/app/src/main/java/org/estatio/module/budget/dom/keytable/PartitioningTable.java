package org.estatio.module.budget.dom.keytable;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationViewmodel;
import org.estatio.module.budget.dom.partioning.PartitionItem;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.dom.budgeting.keytable.PartitioningTable"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByBudgetAndName", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.keytable.PartitioningTable " +
                        "WHERE name == :name "
                        + "&& budget == :budget"),
        @Query(
                name = "findByBudget", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.keytable.PartitioningTable " +
                        "WHERE budget == :budget "),
        @Query(
                name = "findKeyTableByNameMatches", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.keytable.PartitioningTable " +
                        "WHERE name.toLowerCase().indexOf(:name) >= 0 ")
})
@Unique(name = "PartitioningTable_budget_name", members = { "budget", "name" })
@DomainObject(
        objectType = "org.estatio.dom.budgeting.keytable.PartitioningTable"
)
public abstract class PartitioningTable extends UdoDomainObject2<Budget> implements WithApplicationTenancyProperty {

    public PartitioningTable() {
        super("name, budget");
    }

    public String title() {
        return TitleBuilder
                .start()
                .withParent(getBudget())
                .withName(getName())
                .toString();
    }

    public String toString() {
        return this.getName();
    }

    @Column(name = "budgetId", allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private Budget budget;

    @Column(allowsNull = "false")
    @Getter @Setter
    private String name;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public PartitioningTable changeName(final String name) {
        setName(name);
        return this;
    }

    public String validateChangeName(final String name) {
        if (name.equals(null)) {
            return "Name can't be empty";
        }
        if (partitioningTableRepository.findByBudgetAndName(getBudget(), name) != null) {
            return "There is already a table with this name for this budget";
        }
        return null;
    }

    public String default0ChangeName(final String name) {
        return getName();
    }

    @Programmatic
    public abstract List<BudgetCalculationViewmodel> calculateFor(final PartitionItem partitionItem, final BigDecimal partitionItemValue, final BudgetCalculationType budgetCalculationType);

    @Inject
    PartitioningTableRepository partitioningTableRepository;


}
