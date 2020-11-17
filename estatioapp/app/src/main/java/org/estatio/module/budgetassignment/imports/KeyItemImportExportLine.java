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
import org.estatio.module.budget.dom.keyitem.KeyItem;
import org.estatio.module.budget.dom.keyitem.KeyItemRepository;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.PartitioningTableRepository;
import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.KeyItemImportExportLine"
)
public class KeyItemImportExportLine
        implements Comparable<KeyItemImportExportLine> {

    public KeyItemImportExportLine() {
    }

    public KeyItemImportExportLine(final KeyItem keyItem, final Party tenant) {
        this.keyItem = keyItem;
        this.propertyReference = keyItem.getPartitioningTable().getBudget().getProperty().getReference();
        this.unitReference = keyItem.getUnit().getReference();
        this.sourceValue = keyItem.getSourceValue();
        this.keyValue = keyItem.getValue();
        this.keyTableName = keyItem.getPartitioningTable().getName();
        this.startDate = keyItem.getPartitioningTable().getBudget().getStartDate();
        this.divSourceValue = keyItem.getDivCalculatedSourceValue();
        this.tenantOnBudgetStartDate = tenant!=null ? tenant.getName() : null;
    }

    public KeyItemImportExportLine(final KeyItemImportExportLine item) {
        this.keyItem = item.keyItem;
        this.propertyReference = item.propertyReference;
        this.unitReference = item.unitReference;
        this.sourceValue = item.sourceValue.setScale(6, BigDecimal.ROUND_HALF_UP);
        this.keyValue = item.keyValue.setScale(6, BigDecimal.ROUND_HALF_UP);
        this.keyTableName = item.keyTableName;
        this.startDate = item.startDate;
        this.divSourceValue = item.divSourceValue;
        this.tenantOnBudgetStartDate = item.tenantOnBudgetStartDate;
    }

    public String title() {
        return "key item line";
    }

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String propertyReference;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String keyTableName;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private LocalDate startDate;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private String unitReference;

    @Column(scale = 6)
    @Getter @Setter
    @MemberOrder(sequence = "5")
    private BigDecimal sourceValue;

    @Column(scale = 6)
    @Getter @Setter
    @MemberOrder(sequence = "6")
    private BigDecimal keyValue;

    @Column(scale = 6)
    @Getter @Setter
    @MemberOrder(sequence = "8")
    private BigDecimal divSourceValue;

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

    private KeyTable keyTable;

    @Programmatic
    public KeyTable getKeyTable() {
        if (keyTable == null) {
            Budget budget = budgetRepository.findByPropertyAndStartDate(getProperty(),getStartDate());
            keyTable = (KeyTable) partitioningTableRepository.findByBudgetAndName(budget, getKeyTableName());
        }
        return keyTable;
    }

    private KeyItem keyItem;

    @Programmatic
    public KeyItem getKeyItem() {
        if (keyItem == null) {
            keyItem = keyItemRepository.findByKeyTableAndUnit(getKeyTable(), getUnit());
        }
        return keyItem;
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
    public String reasonInValid(){
        if (getPropertyReference()==null) return "Property reference is mandatory";
        if (getProperty()==null) {
            return String.format("Property not found for property reference %s", getPropertyReference());
        }
        if (getKeyTableName()==null) return "Keytable name is mandatory";
        if (getStartDate()==null) return "Startdate is mandatory";
        final Budget budget = budgetRepository
                .findByPropertyAndStartDate(getProperty(), getStartDate());
        if (budget==null) return String.format("Budget could not be found for property %s and startDate %s", getPropertyReference(), getStartDate());
        if (getKeyTable()==null) return String.format("Keytable could not be found for name %s and startDate %s", getKeyTableName(), getStartDate());
        if (getUnitReference()==null) return "Unit reference is mandatory";
        if (getUnit()==null) return String.format("Unit with reference %s not found", getUnitReference());
        if (getSourceValue()==null) return "Source value is mandatory";
        if (getKeyValue()==null && getKeyTable().getFoundationValueType()== FoundationValueType.MANUAL) return String.format("Key value is mandatory for table with foundation value type %s", getKeyTable().getFoundationValueType());
        return null;
    }

    public void importData() {
        keyItemRepository.newItem(getKeyTable(), getUnit(), getSourceValue().setScale(6, BigDecimal.ROUND_HALF_UP), getKeyValue()!=null ? getKeyValue().setScale(keyTable.getPrecision(), BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
    }


    @Override
    public int compareTo(final KeyItemImportExportLine other) {
        return this.keyItem.compareTo(other.keyItem);
    }


    @Inject
    KeyItemRepository keyItemRepository;

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
