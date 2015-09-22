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

import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitMenu;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keyitem.KeyItems;
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
        this.budgetKeyTableName = keyItem.getKeyTable().getName();
    }

    public BudgetKeyItemImportExportLineItem(final BudgetKeyItemImportExportLineItem item) {
        this.keyItem = item.keyItem;
        this.unitReference = item.unitReference;
        this.status = item.status;
        this.sourceValue = item.sourceValue.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.keyValue = item.keyValue;
        this.budgetKeyTableName = item.budgetKeyTableName;
    }

    public String title() {
        return "";
    }

    private String budgetKeyTableName;

    @MemberOrder(sequence = "1")
    public String getBudgetKeyTableName() { return budgetKeyTableName; }

    public void setBudgetKeyTableName(final String budgetKeyTableName) {
        this.budgetKeyTableName = budgetKeyTableName;
    }

    private KeyItem keyItem;

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
        if (keyItem == null) {
            KeyItem keyItem = new KeyItem();
            keyItem.setKeyTable(keyTables.findByName(getBudgetKeyTableName()));
            keyItem.setUnit(unitRepository.findUnitByReference(unitReference));
            keyItem.setValue(BigDecimal.ZERO);
            keyItem.setSourceValue(BigDecimal.ZERO);
            container.persistIfNotAlready(keyItem);
        }
        keyItems.findByKeyTableAndUnit(keyTables.findByName(getBudgetKeyTableName()), unitRepository
                .findUnitByReference(unitReference)).changeValue(this.getKeyValue().setScale(keyTables.findByName(getBudgetKeyTableName()).getNumberOfDigits(), BigDecimal.ROUND_HALF_UP));
        keyItems.findByKeyTableAndUnit(keyTables.findByName(getBudgetKeyTableName()), unitRepository
                .findUnitByReference(unitReference)).setSourceValue(this.getSourceValue().setScale(2, BigDecimal.ROUND_HALF_UP));
        return keyItem;
    }

    @Programmatic
    public void validate() {
        Unit unit = unitRepository.findUnitByReference(unitReference);
        Status newStatus = Status.UNCHANGED;
        if (unit != null) {
            KeyItem keyItem = keyItems.findByKeyTableAndUnit(keyTables.findByName(getBudgetKeyTableName()), unit);
            if (keyItem == null) {
                newStatus = Status.ADDED;
            } else {
                if (org.apache.commons.lang3.ObjectUtils.compare(keyValue, keyItem.getValue()) != 0) {
                    newStatus = Status.UPDATED;
                }
            }
        } else {
            newStatus = Status.NOT_FOUND;
        }
        setStatus(newStatus);
        setComments(newStatus.name());
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
}
