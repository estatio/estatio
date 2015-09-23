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
package org.estatio.app.budget;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.services.actinvoc.ActionInvocationContext;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitMenu;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keyitem.KeyItems;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTables;

enum Status {
    NOT_FOUND,
    UPDATED,
    DELETED,
    UNCHANGED,
    ADDED;
}

@ViewModel
@DomainObjectLayout(
        named = "Bulk import/export budget key line item",
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class BudgetKeyItemImportExportLineItem
        implements Comparable<BudgetKeyItemImportExportLineItem> {

    public BudgetKeyItemImportExportLineItem() {
    }

    public BudgetKeyItemImportExportLineItem(final KeyItem keyItem) {
        this.keyItem = keyItem;
        this.unitReference = keyItem.getUnit().getReference();
        this.sourceValue = keyItem.getSourceValue();
        this.keyValue = keyItem.getValue();
        this.keyTableName = keyItem.getKeyTable().getName();
    }

    public BudgetKeyItemImportExportLineItem(final BudgetKeyItemImportExportLineItem item) {
        this.keyItem = item.keyItem;
        this.unitReference = item.unitReference;
        this.status = item.status;
        this.sourceValue = item.sourceValue.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.keyValue = item.keyValue;
        this.keyTableName = item.keyTableName;
    }

    public String title() {
        return "";
    }

    private String propertyReference;

    public String getPropertyReference() {
        return propertyReference;
    }

    public void setPropertyReference(final String propertyReference) {
        this.propertyReference = propertyReference;
    }

    private Property property;

    @Programmatic
    public Property getProperty() {
        if (property == null) {
            property = propertyRepository.findPropertyByReference(getPropertyReference());
        }
        return property;
    }

    private LocalDate startDate;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    private String keyTableName;

    @MemberOrder(sequence = "1")
    public String getKeyTableName() {
        return keyTableName;
    }

    public void setKeyTableName(final String keyTableName) {
        this.keyTableName = keyTableName;
    }

    private KeyTable keyTable;

    public KeyTable getKeyTable() {
        if (keyTable == null) {
            keyTable = keyTables.findByPropertyAndNameAndStartDate(getProperty(), getKeyTableName(), getStartDate());
        }
        return keyTable;
    }

    private KeyItem keyItem;

    @Programmatic
    public KeyItem getKeyItem() {
        if (keyItem == null) {
            keyItem = keyItems.findByKeyTableAndUnit(getKeyTable(), getUnit());
        }
        return keyItem;
    }

    private String unitReference;

    @MemberOrder(sequence = "2")
    public String getUnitReference() {
        return unitReference;
    }

    public void setUnitReference(final String unitReference) {
        this.unitReference = unitReference;
    }

    private BigDecimal sourceValue;

    @Column(scale = 2)
    @MemberOrder(sequence = "3")
    public BigDecimal getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(final BigDecimal sourceValue) {
        this.sourceValue = sourceValue;
    }

    private BigDecimal keyValue;

    @Column(scale = 6)
    @MemberOrder(sequence = "4")
    public BigDecimal getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(BigDecimal keyValue) {
        this.keyValue = keyValue;
    }

    private String comments;

    @MemberOrder(sequence = "7")
    public String getComments() {
        return comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }

    private Status status;

    @MemberOrder(sequence = "6")
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    // //////////////////////////////////////
    // apply
    // //////////////////////////////////////

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            invokeOn = InvokeOn.OBJECT_AND_COLLECTION
    )
    public KeyItem apply() {
        if (getKeyItem() == null) {
            KeyItem keyItem = new KeyItem();
            keyItem.setKeyTable(getKeyTable());
            keyItem.setUnit(getUnit());
            keyItem.setValue(BigDecimal.ZERO);
            keyItem.setSourceValue(BigDecimal.ZERO);
            container.persistIfNotAlready(keyItem);
        }
        getKeyItem().changeValue(this.getKeyValue().setScale(keyTable.getNumberOfDigits(), BigDecimal.ROUND_HALF_UP));
        getKeyItem().setSourceValue(this.getSourceValue().setScale(2, BigDecimal.ROUND_HALF_UP));
        return getKeyItem();
    }

    @Programmatic
    public void validate() {
        setStatus(calculateStatus());
        setComments(getStatus().name());
    }

    private Status calculateStatus() {
        if (getProperty() == null || getUnit() == null || getKeyTable() == null) {
            return Status.NOT_FOUND;
        }
        if (getKeyItem() == null) {
            return Status.ADDED;
        }

        if (ObjectUtils.notEqual(getKeyItem().getValue(), getKeyValue()) || ObjectUtils.notEqual(getKeyItem().getSourceValue(), getSourceValue())) {
            return Status.UPDATED;
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

    // //////////////////////////////////////
    // compareTo
    // //////////////////////////////////////

    @Override
    public int compareTo(final BudgetKeyItemImportExportLineItem other) {
        return this.keyItem.compareTo(other.keyItem);
    }

    // //////////////////////////////////////
    // injected services
    // //////////////////////////////////////

    @javax.inject.Inject
    private BudgetKeyItemImportExportService budgetKeyItemImportExportService;

    @javax.inject.Inject
    private KeyItems keyItems;

    @javax.inject.Inject
    private ActionInvocationContext actionInvocationContext;

    @Inject
    private UnitMenu unitMenu;
    @Inject
    private UnitRepository unitRepository;

    @Inject
    private DomainObjectContainer container;

    @Inject
    private KeyTables keyTables;

    @Inject
    PropertyRepository propertyRepository;

}
