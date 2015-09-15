/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.budgeting.budgetitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.app.budget.BudgetCalculationServices;
import org.estatio.app.budget.DistributionService;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.budgeting.budgetkeyitem.BudgetKeyItem;
import org.estatio.dom.budgeting.budgetkeytable.BudgetKeyTable;
import org.estatio.dom.budgeting.Distributable;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetline.BudgetLine;
import org.estatio.dom.budgeting.budgetline.BudgetLines;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.OccupancyContributions;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findBudgetItemByBudget", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.budgetitem.BudgetItem " +
                        "WHERE budget == :budget "),
        @Query(
                name = "findBudgetItemByBudgetCharge", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.budgetitem.BudgetItem " +
                        "WHERE budget == :budget && charge == :charge")
})
@DomainObject(editing = Editing.DISABLED, autoCompleteRepository = BudgetItems.class)
public class BudgetItem extends EstatioDomainObject<BudgetItem> implements WithApplicationTenancyProperty {

    public BudgetItem() {
        super("budget, charge, budgetKeyTable, value");
    }

    //region > identificatiom
    public TranslatableString title() {
        return TranslatableString.tr(
                "{name}", "name",
                "Budget item - "
                .concat(getCharge().getReference())
                .concat(" - ")
                .concat(getValue().toString())
                .concat(" - ")
                .concat(getBudget().getProperty().getName())
        );
    }
    //endregion

    private Budget budget;

    @javax.jdo.annotations.Column(name="budgetId", allowsNull = "false")
    @MemberOrder(sequence = "1")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    // //////////////////////////////////////

    private BudgetKeyTable budgetKeyTable;

    @javax.jdo.annotations.Column(name="budgetKeyTableId", allowsNull = "false")
    @MemberOrder(sequence = "2")
    public BudgetKeyTable getBudgetKeyTable() {
        return budgetKeyTable;
    }

    public void setBudgetKeyTable(BudgetKeyTable budgetKeyTable) {
        this.budgetKeyTable = budgetKeyTable;
    }

    public BudgetItem changeBudgetKeyTable(final @ParameterLayout(named = "BudgetKeyTable") BudgetKeyTable budgetKeyTable) {
        setBudgetKeyTable(budgetKeyTable);
        return this;
    }

    public BudgetKeyTable default0ChangeBudgetKeyTable(final BudgetKeyTable budgetKeyTable) {
        return getBudgetKeyTable();
    }

    public String validateChangeBudgetKeyTable(final BudgetKeyTable budgetKeyTable) {
        if (budgetKeyTable.equals(null)) {
            return "BudgetKeyTable can't be empty";
        }
        return null;
    }

    // //////////////////////////////////////

    private BigDecimal value;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @MemberOrder(sequence = "3")
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BudgetItem changeValue(final @ParameterLayout(named = "Value") BigDecimal value) {
        setValue(value);
        return this;
    }

    public BigDecimal default0ChangeValue(final BigDecimal value) {
        return getValue();
    }

    public String validateChangeValue(final BigDecimal value) {
        if (value.equals(new BigDecimal(0))) {
            return "Value can't be zero";
        }
        return null;
    }

    // //////////////////////////////////////

    private Charge charge;

    @javax.jdo.annotations.Column(name="chargeId", allowsNull = "false")
    @MemberOrder(sequence = "5")
    public Charge getCharge() {
        return charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }

    public BudgetItem changeCharge(final @ParameterLayout(named = "Charge") Charge charge) {
        setCharge(charge);
        return this;
    }

    public Charge default0ChangeCharge(final Charge charge) {
        return getCharge();
    }

    public String validateChangeCharge(final Charge charge) {
        if (charge.equals(null)) {
            return "Charge can't be empty";
        }
        return null;
    }

    // //////////////////////////////////////


    public BudgetItem generateBudgetLines(@ParameterLayout(named = "Are you sure? All existing lines will be deleted.") final boolean confirmDelete) {

        for (BudgetLine budgetLine : budgetLines.findByBudgetItem(this)) {
            budgetLine.deleteBudgetLine();
        }

        List<Distributable> budgetLineList = new ArrayList<>();

        BudgetKeyItem budgetKeyItem = new BudgetKeyItem();
        for (Iterator<BudgetKeyItem> it = this.getBudgetKeyTable().getBudgetKeyItems().iterator(); it.hasNext();){

            budgetKeyItem = it.next();

            BigDecimal calculatedValue = BigDecimal.ZERO;
            calculatedValue = calculatedValue.add(budgetCalculationServices.calculatedValuePerBudgetKeyItem(this,budgetKeyItem));
            BudgetLine newBudgetLine = new BudgetLine(calculatedValue, this,budgetKeyItem);
            budgetLineList.add(newBudgetLine);

        }

        //distribute budget lines
        DistributionService distributionService = new DistributionService();
        budgetLineList = distributionService.distribute(budgetLineList, this.getValue(), 2);

        for (Distributable budgetLine : budgetLineList) {

            persistIfNotAlready((BudgetLine) budgetLine);

        }

        return this;
    }

    public String validateGenerateBudgetLines(final boolean confirmDelete) {

        return confirmDelete? null:"Please confirm";

    }

    @Override
    @MemberOrder(sequence = "7")
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudget().getApplicationTenancy();
    }

    @Inject
    private Leases leases;

    @Inject
    private BudgetCalculationServices budgetCalculationServices;

    @Inject
    BudgetLines budgetLines;

    @Inject
    OccupancyContributions occupancies;

}