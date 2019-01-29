/*
 * Copyright 2012-2015 Eurocommercial Properties NV
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.estatio.module.budgetassignment.imports;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.keyitem.DirectCost;
import org.estatio.module.budget.dom.keyitem.DirectCostRepository;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.PartitioningTableRepository;
import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.Setter;
import static org.estatio.module.budget.dom.budget.Status.NEW;
import static org.estatio.module.budget.dom.budget.Status.RECONCILED;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.budgetassignment.imports.DirectCostLine"
)
public class DirectCostLine
        implements Comparable<DirectCostLine> {

    //region > constructors, title
    public DirectCostLine() {
    }

    public DirectCostLine(final DirectCost directCost, final Party tenant) {
        this.directCost = directCost;
        this.propertyReference = directCost.getPartitioningTable().getBudget().getProperty().getReference();
        this.unitReference = directCost.getUnit().getReference();
        this.budgetedCost = directCost.getBudgetedCost();
        this.auditedCost = directCost.getAuditedCost();
        this.directCostTableName = directCost.getPartitioningTable().getName();
        this.startDate = directCost.getPartitioningTable().getBudget().getStartDate();
        this.tenantOnBudgetStartDate = tenant!=null ? tenant.getName() : null;
    }

    public DirectCostLine(final DirectCostLine item) {
        this.directCost = item.directCost;
        this.propertyReference = item.propertyReference;
        this.unitReference = item.unitReference;
        this.status = item.status;
        this.budgetedCost = item.budgetedCost!= null ? item.budgetedCost.setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.auditedCost = item.auditedCost!= null ? item.auditedCost.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
        this.directCostTableName = item.directCostTableName;
        this.startDate = item.startDate;
        this.tenantOnBudgetStartDate = item.tenantOnBudgetStartDate;
    }

    public String title() {
        return "direct cost import / export";
    }
    //endregion

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private String directCostTableName;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private String unitReference;

    @Column(scale = 2)
    @Getter @Setter
    private BigDecimal budgetedCost;

    @Column(scale = 2)
    @Getter @Setter
    private BigDecimal auditedCost;

    @Getter @Setter
    private Status status;

    @Getter @Setter
    private String tenantOnBudgetStartDate;

    //region > apply (action)
    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            invokeOn = InvokeOn.OBJECT_AND_COLLECTION,
            publishing = Publishing.DISABLED
    )
    public DirectCost apply() {

        switch (getStatus()) {

            case ADDED:
                DirectCost directCost = new DirectCost();
                directCost.setPartitioningTable(getDirectCostTable());
                directCost.setUnit(getUnit());
                directCost.setBudgetedCost(getBudgetedCost());
                directCost.setAuditedCost(getAuditedCost());
                repositoryService.persistAndFlush(directCost);
                break;

            case UPDATED:
                DirectCost dc = getDirectCost();
                org.estatio.module.budget.dom.budget.Status budgetStatus = dc.getPartitioningTable().getBudget().getStatus();
                if (budgetStatus==NEW) dc.changeBudgetedCost(this.getBudgetedCost());
                if (budgetStatus!=RECONCILED) dc.changeAuditedCost(this.getAuditedCost()); // redundant, but just to be sure
                break;

            case DELETED:
                dc = getDirectCost();
                budgetStatus = dc.getPartitioningTable().getBudget().getStatus();
                if (budgetStatus==NEW) {
                    String message = "DirectCost for unit " + dc.getUnit().getReference() + " deleted";
                    dc.delete();
                    messageService.informUser(message);
                }
                return null;

            case NOT_FOUND:
                messageService.informUser("DirectCost not found");
                return null;

            default:
                break;

        }

        return getDirectCost();
    }

    @Programmatic
    public void validate() {
        setStatus(calculateStatus());
    }

    private Status calculateStatus() {
        if (getProperty() == null || getUnit() == null || getDirectCostTable() == null) {
            return Status.NOT_FOUND;
        }
        if (getDirectCost() == null) {
            return Status.ADDED;
        }
        if (ObjectUtils.notEqual(getDirectCost().getBudgetedCost(), getBudgetedCost()) || ObjectUtils.notEqual(getDirectCost().getAuditedCost(), getAuditedCost())) {
            return Status.UPDATED;
        }
        // added for newly created lines for deleted items
        if (getStatus() == Status.DELETED) {
            return Status.DELETED;
        }
        return Status.UNCHANGED;
    }

    private Unit unit;

    @Programmatic
    private Unit getUnit() {
        if (unit == null) {
            unit = unitRepository.findUnitByReference(unitReference);
        }
        return unit;
    }

    private DirectCostTable directCostTable;

    @Programmatic
    public DirectCostTable getDirectCostTable() {
        if (directCostTable == null) {
            Budget budget = budgetRepository.findByPropertyAndStartDate(getProperty(),getStartDate());
            directCostTable = (DirectCostTable) partitioningTableRepository.findByBudgetAndName(budget, getDirectCostTableName());
        }
        return directCostTable;
    }

    private DirectCost directCost;

    @Programmatic
    public DirectCost getDirectCost() {
        if (directCost == null) {
            directCost = directCostRepository.findByDirectCostTableAndUnit(getDirectCostTable(), getUnit());
        }
        return directCost;
    }

    private Property property;

    @Programmatic
    public Property getProperty() {
        if (property == null) {
            property = propertyRepository.findPropertyByReference(getPropertyReference());
        }
        return property;
    }

    //region > compareTo
    @Override
    public int compareTo(final DirectCostLine other) {
        return this.directCost.compareTo(other.directCost);
    }
    //endregion


    @Inject
    DirectCostRepository directCostRepository;

    @Inject
    UnitRepository unitRepository;

    @Inject
    MessageService messageService;

    @Inject
    PartitioningTableRepository partitioningTableRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    RepositoryService repositoryService;

}
