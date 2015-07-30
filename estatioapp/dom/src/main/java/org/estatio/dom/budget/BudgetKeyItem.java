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
package org.estatio.dom.budget;

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
                        "FROM org.estatio.dom.budget.BudgetKeyItem " +
                        "WHERE budgetKeyTable == :budgetKeyTable && unit == :unit")
})
public class BudgetKeyItem extends EstatioDomainObject<BudgetKeyItem> implements WithApplicationTenancyProperty {

    public BudgetKeyItem() {
        super("budgetKeyTable,unit,keyValue, sourceValue");
    }

    //region > identificatiom
    public TranslatableString title() {
        return TranslatableString.tr("{name}", "name", "Budget key item for ".concat(getUnit().getName()));
    }
    //endregion

    private BudgetKeyTable budgetKeyTable;

    @javax.jdo.annotations.Column(allowsNull = "false")
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

    @javax.jdo.annotations.Column(allowsNull = "false")
    @MemberOrder(sequence = "1")
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    // //////////////////////////////////////

    private BigDecimal sourceValue;

    @javax.jdo.annotations.Column(allowsNull = "false", scale = 3)
    @MemberOrder(sequence = "3")
    public BigDecimal getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(BigDecimal sourceValue) {
            this.sourceValue = sourceValue;
    }

    // //////////////////////////////////////

    private BigDecimal keyValue;

    @javax.jdo.annotations.Column(allowsNull = "false", scale = 3)
    @MemberOrder(sequence = "2")
    public BigDecimal getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(BigDecimal keyValue) {

            this.keyValue = keyValue;

    }

    public BudgetKeyItem changeKeyValue(final @ParameterLayout(named = "Key value") BigDecimal keyValue) {
        setKeyValue(keyValue.setScale(3, BigDecimal.ROUND_HALF_DOWN));
        return this;
    }

    public BigDecimal default0ChangeKeyValue(final BigDecimal keyValue) {
        return getKeyValue();
    }

    public String validateChangeKeyValue(final BigDecimal keyValue) {
        if (keyValue.compareTo(BigDecimal.ZERO) < 0) {
            return "keyValue cannot be less than zero";
        }
        //TODO: Maybe changing an individual key value is no good idea at all?
        return null;
    }

    // //////////////////////////////////////

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
