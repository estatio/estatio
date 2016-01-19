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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.actinvoc.ActionInvocationContext;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.*;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keyitem.KeyItems;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import java.math.BigDecimal;

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
public class KeyItemImportExportLineItem
        implements Comparable<KeyItemImportExportLineItem> {

    public KeyItemImportExportLineItem() {
    }

    public KeyItemImportExportLineItem(final KeyItem keyItem) {
        this.keyItem = keyItem;
        this.propertyReference = keyItem.getKeyTable().getProperty().getReference();
        this.unitReference = keyItem.getUnit().getReference();
        this.sourceValue = keyItem.getSourceValue();
        this.keyValue = keyItem.getValue();
        this.keyTableName = keyItem.getKeyTable().getName();
        this.startDate = keyItem.getKeyTable().getStartDate();
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
        this.comments = item.comments;
    }

    public String title() {
        return "";
    }

    private String keyTableName;

    @MemberOrder(sequence = "1")
    public String getKeyTableName() {
        return keyTableName;
    }

    public void setKeyTableName(final String keyTableName) {
        this.keyTableName = keyTableName;
    }

    private String unitReference;

    @MemberOrder(sequence = "2")
    public String getUnitReference() {
        return unitReference;
    }

    public void setUnitReference(final String unitReference) {
        this.unitReference = unitReference;
    }

    private String propertyReference;

    @MemberOrder(sequence = "3")
    public String getPropertyReference() {
        return propertyReference;
    }

    public void setPropertyReference(final String propertyReference) {
        this.propertyReference = propertyReference;
    }


    private LocalDate startDate;

    @MemberOrder(sequence = "4")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    private BigDecimal sourceValue;

    @Column(scale = 6)
    @MemberOrder(sequence = "5")
    public BigDecimal getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(final BigDecimal sourceValue) {
        this.sourceValue = sourceValue;
    }

    private BigDecimal keyValue;

    @Column(scale = 6)
    @MemberOrder(sequence = "6")
    public BigDecimal getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(BigDecimal keyValue) {
        this.keyValue = keyValue;
    }

    private Status status;

    @MemberOrder(sequence = "6")
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }


    private String comments;

    @MemberOrder(sequence = "7")
    public String getComments() {
        return comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }



    // //////////////////////////////////////
    // apply
    // //////////////////////////////////////

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            invokeOn = InvokeOn.OBJECT_AND_COLLECTION
    )
    public KeyItem apply() {

        switch (getStatus()) {

            case ADDED:
                KeyItem keyItem = new KeyItem();
                keyItem.setKeyTable(getKeyTable());
                keyItem.setUnit(getUnit());
                keyItem.setValue(BigDecimal.ZERO);
                keyItem.setSourceValue(BigDecimal.ZERO);
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
            keyTable = keyTableRepository.findByPropertyAndNameAndStartDate(getProperty(), getKeyTableName(), getStartDate());
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

    private Property property;

    @Programmatic
    public Property getProperty() {
        if (property == null) {
            property = propertyRepository.findPropertyByReference(getPropertyReference());
        }
        return property;
    }

    // //////////////////////////////////////
    // compareTo
    // //////////////////////////////////////

    @Override
    public int compareTo(final KeyItemImportExportLineItem other) {
        return this.keyItem.compareTo(other.keyItem);
    }

    // //////////////////////////////////////
    // injected services
    // //////////////////////////////////////

    @javax.inject.Inject
    private KeyItemImportExportService keyItemImportExportService;

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
    private KeyTableRepository keyTableRepository;

    @Inject
    PropertyRepository propertyRepository;

}
