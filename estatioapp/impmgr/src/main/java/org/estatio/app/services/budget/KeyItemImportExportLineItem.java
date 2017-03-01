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
package org.estatio.app.services.budget;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keyitem.KeyItemRepository;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.KeyItemImportExportLineItem"
)
public class KeyItemImportExportLineItem
        implements Comparable<KeyItemImportExportLineItem> {

    //region > constructors, title
    public KeyItemImportExportLineItem() {
    }

    public KeyItemImportExportLineItem(final KeyItem keyItem) {
        this.keyItem = keyItem;
        this.propertyReference = keyItem.getKeyTable().getBudget().getProperty().getReference();
        this.unitReference = keyItem.getUnit().getReference();
        this.sourceValue = keyItem.getSourceValue();
        this.keyValue = keyItem.getValue();
        this.keyTableName = keyItem.getKeyTable().getName();
        this.startDate = keyItem.getKeyTable().getBudget().getStartDate();
    }

    public KeyItemImportExportLineItem(final KeyItemImportExportLineItem item) {
        this.keyItem = item.keyItem;
        this.propertyReference = item.propertyReference;
        this.unitReference = item.unitReference;
        this.status = item.status;
        this.sourceValue = item.sourceValue.setScale(6, BigDecimal.ROUND_HALF_UP);
        this.keyValue = item.keyValue.setScale(6, BigDecimal.ROUND_HALF_UP);
        this.keyTableName = item.keyTableName;
        this.startDate = item.startDate;
    }

    public String title() {
        return "key item import / export";
    }
    //endregion

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private String keyTableName;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private String unitReference;

    @Column(scale = 6)
    @Getter @Setter
    private BigDecimal sourceValue;

    @Column(scale = 6)
    @Getter @Setter
    private BigDecimal keyValue;

    @Getter @Setter
    private Status status;


    //region > apply (action)
    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            invokeOn = InvokeOn.OBJECT_AND_COLLECTION,
            publishing = Publishing.DISABLED
    )
    public KeyItem apply() {

        switch (getStatus()) {

            case ADDED:
                KeyItem keyItem = new KeyItem();
                keyItem.setKeyTable(getKeyTable());
                keyItem.setUnit(getUnit());
                keyItem.setValue(getKeyValue().setScale(keyTable.getPrecision(), BigDecimal.ROUND_HALF_UP));
                keyItem.setSourceValue(getSourceValue().setScale(2, BigDecimal.ROUND_HALF_UP));
                container.persistIfNotAlready(keyItem);
                break;

            case UPDATED:
                getKeyItem().changeValue(this.getKeyValue().setScale(keyTable.getPrecision(), BigDecimal.ROUND_HALF_UP));
                getKeyItem().setSourceValue(this.getSourceValue().setScale(2, BigDecimal.ROUND_HALF_UP));
                break;

            case DELETED:
                String message = "KeyItem for unit " + getKeyItem().getUnit().getReference() + " deleted";
                getKeyItem().deleteBudgetKeyItem();
                container.informUser(message);
                return null;

            case NOT_FOUND:
                container.informUser("KeyItem not found");
                return null;

            default:
                break;

        }

        return getKeyItem();
    }
    //endregion


    @Programmatic
    public void validate() {
        setStatus(calculateStatus());
//        setComments(getStatus().name());
    }

    private Status calculateStatus() {
        if (getProperty() == null || getUnit() == null || getKeyTable() == null) {
            return Status.NOT_FOUND;
        }
        if (getKeyItem() == null) {
            return Status.ADDED;
        }
        if (ObjectUtils.notEqual(getKeyItem().getValue().setScale(6, BigDecimal.ROUND_HALF_UP), getKeyValue().setScale(6, BigDecimal.ROUND_HALF_UP)) || ObjectUtils.notEqual(getKeyItem().getSourceValue().setScale(6, BigDecimal.ROUND_HALF_UP), getSourceValue().setScale(6, BigDecimal.ROUND_HALF_UP))) {
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

    private KeyTable keyTable;

    @Programmatic
    public KeyTable getKeyTable() {
        if (keyTable == null) {
            Budget budget = budgetRepository.findByPropertyAndStartDate(getProperty(),getStartDate());
            keyTable = keyTableRepository.findByBudgetAndName(budget, getKeyTableName());
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

    //region > compareTo
    @Override
    public int compareTo(final KeyItemImportExportLineItem other) {
        return this.keyItem.compareTo(other.keyItem);
    }
    //endregion


    //region > injected services
    @Inject
    KeyItemRepository keyItemRepository;

    @Inject
    UnitRepository unitRepository;

    @Inject
    DomainObjectContainer container;

    @Inject
    KeyTableRepository keyTableRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BudgetRepository budgetRepository;
    //endregion

}
