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
package org.estatio.module.budget.dom.budgetcalculation;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.timestamp.Timestampable;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.charge.dom.Charge;

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
@javax.jdo.annotations.Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation " +
                        "WHERE partitionItem == :partitionItem " +
                        "&& keyItem == :keyItem " +
                        "&& calculationType == :calculationType"),
        @Query(
                name = "findByPartitionItemAndCalculationType", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation " +
                        "WHERE partitionItem == :partitionItem " +
                        "&& calculationType == :calculationType"),
        @Query(
                name = "findByBudgetAndUnitAndInvoiceChargeAndType", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation " +
                        "WHERE budget == :budget && "
                        + "unit == :unit && "
                        + "invoiceCharge == :invoiceCharge && "
                        + "calculationType == :type"),
        @Query(
                name = "findByBudgetAndUnitAndInvoiceChargeAndIncomingChargeAndType", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation " +
                        "WHERE budget == :budget && "
                        + "unit == :unit && "
                        + "invoiceCharge == :invoiceCharge && "
                        + "incomingCharge == :incomingCharge && "
                        + "calculationType == :type")
})
@Indices({
        @Index(name = "BudgetCalculation_budget_unit_invoiceCharge_type_IDX",
                members = { "budget", "unit", "invoiceCharge", "calculationType" }),
        @Index(name = "BudgetCalculation_budget_unit_invoiceCharge_incomingCharge_type_IDX",
                members = { "budget", "unit", "invoiceCharge", "incomingCharge", "calculationType" })
})
@Unique(name = "BudgetCalculation_partitionItem_keyItem_calculationType_UNQ", members = {"partitionItem", "keyItem", "calculationType"})
@DomainObject(
        auditing = Auditing.DISABLED,
        publishing = Publishing.DISABLED,
        objectType = "org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation"
)
public class BudgetCalculation extends UdoDomainObject2<BudgetCalculation>
        implements WithApplicationTenancyProperty, Timestampable {

    public BudgetCalculation() {
        super("partitionItem, keyItem");
    }

    public String title(){
        return TitleBuilder
                .start()
                .withName("Calculation - ")
                .withName(getValue())
                .toString();
    }

    @Getter @Setter
    @Column(allowsNull = "false", scale = 6)
    private BigDecimal value;

    @Getter @Setter
    @Column(allowsNull = "false", name="partitionItemId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private PartitionItem partitionItem;

    @Getter @Setter
    @Column(allowsNull = "false", name="keyItemId")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private KeyItem keyItem;

    @Getter @Setter
    @Column(allowsNull = "false")
    private BudgetCalculationType calculationType;

    @Getter @Setter
    @Column(name = "budgetId", allowsNull = "false")
    private Budget budget;

    @Getter @Setter
    @Column(name = "unitId", allowsNull = "false")
    private Unit unit;

    @Getter @Setter
    @Column(name = "invoiceChargeId", allowsNull = "false")
    private Charge invoiceCharge;

    @Getter @Setter
    @Column(name = "incomingChargeId", allowsNull = "false")
    private Charge incomingCharge;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Status status;

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return this.getPartitionItem().getApplicationTenancy();
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(hidden = Where.ALL_TABLES)
    private Timestamp updatedAt;

    @Getter @Setter
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Column(allowsNull = "true")
    private String updatedBy;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION, hidden = Where.ALL_TABLES)
    public BudgetItem getBudgetItem(){
        return this.getPartitionItem().getBudgetItem();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION, hidden = Where.ALL_TABLES)
    // TODO: revisit when working on multiple partitions for auditing
    public BigDecimal getEffectiveValue() {
        return getValue().multiply(getPartitionItem().getPartitioning().getFractionOfYear());
    }

    @Programmatic
    public void removeWithStatusNew() {
        if (getStatus() == Status.NEW) {
            repositoryService.removeAndFlush(this);
        }
    }

    @Programmatic
    public void finalizeCalculation() {
        setStatus(Status.ASSIGNED);
    }

    @Inject
    RepositoryService repositoryService;

}
