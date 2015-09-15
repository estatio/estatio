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
package org.estatio.dom.budgeting.budgetkeyitem;

import java.math.BigDecimal;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.Distributable;
import org.estatio.dom.budgeting.budgetkeytable.BudgetKeyTable;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@DomainObject(editing = Editing.DISABLED)
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByBudgetKeyTableAndUnit", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.budgetkeyitem.BudgetKeyItem " +
                        "WHERE budgetKeyTable == :budgetKeyTable && unit == :unit")
})
public class BudgetKeyItem extends EstatioDomainObject<BudgetKeyItem>
        implements WithApplicationTenancyProperty,
        Distributable {

    public BudgetKeyItem() {
        super("budgetKeyTable, unit, value, sourceValue");
    }

    //region > identificatiom
    public TranslatableString title() {
        return TranslatableString.tr("{name}", "name", "Budget key item for ".concat(getUnit().getName()));
    }
    //endregion

    private BudgetKeyTable budgetKeyTable;

    @javax.jdo.annotations.Column(name="budgetKeyTableId", allowsNull = "false")
    @MemberOrder(sequence = "1")
    @PropertyLayout(hidden = Where.PARENTED_TABLES )
    public BudgetKeyTable getBudgetKeyTable() {
        return budgetKeyTable;
    }

    public void setBudgetKeyTable(BudgetKeyTable budgetKeyTable) {
        this.budgetKeyTable = budgetKeyTable;
    }

    // //////////////////////////////////////

    private Unit unit;

    @javax.jdo.annotations.Column(name="unitId", allowsNull = "false")
    @MemberOrder(sequence = "1")
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    // //////////////////////////////////////

    private BigDecimal sourceValue;

    @javax.jdo.annotations.Column(allowsNull = "false", scale = 2)
    @MemberOrder(sequence = "3")
    public BigDecimal getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(BigDecimal sourceValue) {
            this.sourceValue = sourceValue;
    }

    public BudgetKeyItem changeSourceValue(final @ParameterLayout(named = "Source value") BigDecimal sourceValue) {
        setSourceValue(sourceValue.setScale(2, BigDecimal.ROUND_HALF_UP));
        return this;
    }

    public BigDecimal default0ChangeSourceValue(final BigDecimal sourceValue) {
        return getSourceValue();
    }

    public String validateChangeSourceValue(final BigDecimal sourceValue) {
        if (sourceValue.compareTo(BigDecimal.ZERO) <= 0) {
            return "Source Value must be positive";
        }
        return null;
    }

    // //////////////////////////////////////

    private BigDecimal value;

    @javax.jdo.annotations.Column(allowsNull = "false", scale = 6)
    @MemberOrder(sequence = "2")
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {

            this.value = value;

    }

    public BudgetKeyItem changeValue(final @ParameterLayout(named = "Key value") BigDecimal keyValue) {
        setValue(keyValue.setScale(getBudgetKeyTable().getNumberOfDigits(), BigDecimal.ROUND_HALF_UP));
        return this;
    }

    public BigDecimal default0ChangeValue(final BigDecimal targetValue) {
        return getValue().setScale(getBudgetKeyTable().getNumberOfDigits(), BigDecimal.ROUND_HALF_UP);
    }

    public String validateChangeValue(final BigDecimal keyValue) {
        if (keyValue.compareTo(BigDecimal.ZERO) < 0) {
            return "Value cannot be less than zero";
        }
        return null;
    }

    //region > deleteBudgetKeyItem
    public BudgetKeyTable deleteBudgetKeyItem(@ParameterLayout(named = "Are you sure?") final boolean confirmDelete) {
        removeIfNotAlready(this);
        return this.getBudgetKeyTable();
    }

    public String validateDeleteBudgetKeyItem(boolean confirmDelete){
        return confirmDelete? null:"Please confirm";
    }
    //endregion

    @Programmatic
    public void deleteBudgetKeyItem() {
        removeIfNotAlready(this);
    }

    @Override
    @MemberOrder(sequence = "4")
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudgetKeyTable().getApplicationTenancy();
    }
}
