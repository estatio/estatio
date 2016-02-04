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

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.charge.Charge;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import javax.inject.Inject;
import javax.jdo.annotations.*;
import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

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
@DomainObject(autoCompleteRepository = BudgetItemRepository.class)
public class BudgetItem extends EstatioDomainObject<BudgetItem> implements WithApplicationTenancyProperty {

    public BudgetItem() {
        super("budget, charge, budgetedValue");
    }

    public TranslatableString title() {
        return TranslatableString.tr(
                "{name}", "name",
                "Budget item ");
    }

    @Column(name="budgetId", allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private Budget budget;

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal budgetedValue;

    @Action(hidden = Where.EVERYWHERE)
    public BudgetItem changeBudgetedValue(final BigDecimal value) {
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

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal auditedValue;

    public BudgetItem changeAuditedValue(final BigDecimal value) {
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

    @Column(name="chargeId", allowsNull = "false")
    @Getter @Setter
    private Charge charge;

    @Action(hidden = Where.EVERYWHERE)
    public BudgetItem changeCharge(final Charge charge) {
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

    @CollectionLayout(render= RenderType.EAGERLY)
    @Persistent(mappedBy = "budgetItem", dependentElement = "true")
    @Getter @Setter
    private SortedSet<BudgetItemAllocation> budgetItemAllocations = new TreeSet<>();


    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudget().getApplicationTenancy();
    }

    @Inject
    private BudgetItemRepository budgetItemRepository;

}