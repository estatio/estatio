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

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.budgeting.BudgetCalculationContributionServices;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.OccupancyContributions;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.VersionStrategy;
import java.math.BigDecimal;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        //      ,schema = "budget"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByBudget", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.budgetitem.BudgetItem " +
                        "WHERE budget == :budget "),
        @Query(
                name = "findByBudgetAndCharge", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.budgetitem.BudgetItem " +
                        "WHERE budget == :budget && charge == :charge"),
        @Query(
            name = "findByPropertyAndChargeAndStartDate", language = "JDOQL",
            value = "SELECT " +
                    "FROM org.estatio.dom.budgeting.budgetitem.BudgetItem " +
                    "WHERE budget.property == :property "
                    + "&& charge == :charge "
                    + "&& budget.startDate == :startDate")
})
@DomainObject(editing = Editing.DISABLED, autoCompleteRepository = BudgetItems.class)
public class BudgetItem extends EstatioDomainObject<BudgetItem> implements WithApplicationTenancyProperty {


    public BudgetItem() {
        super("budget, charge, budgetedValue");
    }

    //region > identificatiom
    public TranslatableString title() {
        return TranslatableString.tr(
                "{name}", "name",
                "Budget item - "
                .concat(getCharge().getReference())
                .concat(" - ")
                .concat(getBudgetedValue().toString())
                .concat(" - ")
                .concat(getBudget().getProperty().getName())
        );
    }
    //endregion

    private Budget budget;

    @javax.jdo.annotations.Column(name="budgetId", allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    // //////////////////////////////////////

    private BigDecimal budgetedValue;

    @javax.jdo.annotations.Column(allowsNull = "false", scale = 2)
    public BigDecimal getBudgetedValue() {
        return budgetedValue;
    }

    public void setBudgetedValue(BigDecimal budgetedValue) {
        this.budgetedValue = budgetedValue;
    }

    public BudgetItem changeBudgetedValue(final @ParameterLayout(named = "Value") BigDecimal value) {
        setBudgetedValue(value);
        return this;
    }

    public BigDecimal default0ChangeBudgetedValue(final BigDecimal value) {
        return getBudgetedValue();
    }

    public String validateChangeBudgetedValue(final BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            return "Value should be a positive non zero value";
        }
        return null;
    }

    // //////////////////////////////////////

    //region > auditedValue (property)
    private BigDecimal auditedValue;

    @javax.jdo.annotations.Column(allowsNull = "true", scale = 2)
    public BigDecimal getAuditedValue() {
        return auditedValue;
    }

    public void setAuditedValue(final BigDecimal auditedValue) {
        this.auditedValue = auditedValue;
    }

    public BudgetItem changeAuditedValue(final @ParameterLayout(named = "Value") BigDecimal value) {
        setAuditedValue(value);
        return this;
    }

    public BigDecimal default0ChangeAuditedValue(final BigDecimal value) {
        return getAuditedValue();
    }

    public String validateChangeAuditedValue(final BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return "Value can't be negative";
        }
        return null;
    }
    //endregion

    private Charge charge;

    @javax.jdo.annotations.Column(name="chargeId", allowsNull = "false")
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
        if (budgetItemRepository.findByBudgetAndCharge(budget, charge)!=null) {
            return "There is already an item with this charge.";
        }
        return null;
    }

    // //////////////////////////////////////

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudget().getApplicationTenancy();
    }

    @Inject
    private Leases leases;

    @Inject
    private BudgetCalculationContributionServices budgetCalculationContributionServices;

    @Inject
    OccupancyContributions occupancies;

    @Inject
    private BudgetItems budgetItemRepository;

}