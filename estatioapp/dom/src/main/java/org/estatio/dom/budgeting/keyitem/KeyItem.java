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
package org.estatio.dom.budgeting.keyitem;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.Distributable;
import org.estatio.dom.budgeting.keytable.KeyTable;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "EstatioBudgeting" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByKeyTableAndUnit", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.keyitem.KeyItem " +
                        "WHERE keyTable == :keyTable && unit == :unit")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.budgeting.keyitem.KeyItem"
)
public class KeyItem extends UdoDomainObject2<KeyItem>
        implements WithApplicationTenancyProperty, Distributable {

    public KeyItem() {
        super("keyTable, unit, value, sourceValue");
    }

    public String title() {
        return TitleBuilder
                .start()
                .withParent(getKeyTable())
                .withName(getUnit())
                .toString();
    }

    @Column(name="keyTableId", allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES )
    @Getter @Setter
    private KeyTable keyTable;

    // //////////////////////////////////////

    @Column(name="unitId", allowsNull = "false")
    @Getter @Setter
    private Unit unit;

    // //////////////////////////////////////

    @Column(allowsNull = "false", scale = 6)
    @Getter @Setter
    private BigDecimal sourceValue;

    @ActionLayout(hidden = Where.EVERYWHERE)
    public KeyItem changeSourceValue(final BigDecimal sourceValue) {
        setSourceValue(sourceValue.setScale(2, BigDecimal.ROUND_HALF_UP));
        return this;
    }

    public BigDecimal default0ChangeSourceValue(final BigDecimal sourceValue) {
        return getSourceValue();
    }

    public String validateChangeSourceValue(final BigDecimal sourceValue) {
        if (sourceValue.compareTo(BigDecimal.ZERO) < 0) {
            return "Source Value must be positive";
        }
        return null;
    }

    // //////////////////////////////////////

    @Column(allowsNull = "false", scale = 6)
    @Getter @Setter
    private BigDecimal value;

    @ActionLayout(hidden = Where.EVERYWHERE)
    public KeyItem changeValue(final BigDecimal keyValue) {
        setValue(keyValue.setScale(getKeyTable().getPrecision(), BigDecimal.ROUND_HALF_UP));
        return this;
    }

    public BigDecimal default0ChangeValue(final BigDecimal targetValue) {
        return getValue().setScale(getKeyTable().getPrecision(), BigDecimal.ROUND_HALF_UP);
    }

    public String validateChangeValue(final BigDecimal keyValue) {
        if (keyValue.compareTo(BigDecimal.ZERO) < 0) {
            return "Value cannot be less than zero";
        }
        return null;
    }

    //region > auditedValue (property)
    @Column(allowsNull = "true", scale = 6)
    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private BigDecimal auditedValue;

    @ActionLayout(hidden = Where.EVERYWHERE)
    public KeyItem changeAuditedValue(final BigDecimal auditedKeyValue) {
        setAuditedValue(auditedKeyValue.setScale(getKeyTable().getPrecision(), BigDecimal.ROUND_HALF_UP));
        return this;
    }

    public BigDecimal default0ChangeAuditedValue(final BigDecimal auditedKeyValue) {
        if (getAuditedValue()!=null) {
        return getAuditedValue().setScale(getKeyTable().getPrecision(), BigDecimal.ROUND_HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public String validateChangeAuditedValue(final BigDecimal auditedKeyValue) {
        if (auditedKeyValue.compareTo(BigDecimal.ZERO) < 0) {
            return "Value cannot be less than zero";
        }
        return null;
    }
    //endregion

    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void deleteBudgetKeyItem() {
        removeIfNotAlready(this);
    }

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getKeyTable().getApplicationTenancy();
    }
}
