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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
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
        this.budgetedCost = item.budgetedCost!= null ? item.budgetedCost.setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.auditedCost = item.auditedCost!= null ? item.auditedCost.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
        this.directCostTableName = item.directCostTableName;
        this.startDate = item.startDate;
        this.tenantOnBudgetStartDate = item.tenantOnBudgetStartDate;
    }

    public String title() {
        return "direct cost line";
    }
    //endregion

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String propertyReference;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String directCostTableName;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private LocalDate startDate;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private String unitReference;

    @Column(scale = 2)
    @Getter @Setter
    @MemberOrder(sequence = "5")
    private BigDecimal budgetedCost;

    @Column(scale = 2)
    @Getter @Setter
    @MemberOrder(sequence = "6")
    private BigDecimal auditedCost;

    @Getter @Setter
    @MemberOrder(sequence = "7")
    private String tenantOnBudgetStartDate;

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
            directCost = directCostRepository.findUnique(getDirectCostTable(), getUnit());
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

    @Programmatic
    public String reasonInValid() {
        if (getPropertyReference()==null) return "Property reference is mandatory";
        if (getProperty()==null) {
            return String.format("Property not found for property reference %s", getPropertyReference());
        }
        if (getDirectCostTableName()==null) return "DirectCostTable name is mandatory";
        if (getStartDate()==null) return "Startdate is mandatory";
        final Budget budget = budgetRepository.findByPropertyAndStartDate(getProperty(), getStartDate());
        if (budget==null) return String.format("Budget could not be found for property %s and startDate %s", getPropertyReference(), getStartDate());
        if (getDirectCostTable()==null) return String.format("DirectCostTable could not be found for name %s and startDate %s", getDirectCostTable(), getStartDate());
        if (getUnitReference()==null) return "Unit reference is mandatory";
        if (getUnit()==null) return String.format("Unit with reference %s not found", getUnitReference());
        if (getBudgetedCost()==null) return "Budgeted cost is mandatory";
        return null;
    }

    @Programmatic
    public void importData() {
        directCostRepository.upsertValuesUsingBusinessLogicOrCreate(getDirectCostTable(), getUnit(), getBudgetedCost(), getAuditedCost());
    }

    @Override
    public int compareTo(final DirectCostLine other) {
        return this.directCost.compareTo(other.directCost);
    }

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
