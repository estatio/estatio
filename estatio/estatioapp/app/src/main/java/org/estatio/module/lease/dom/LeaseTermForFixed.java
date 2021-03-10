/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.module.lease.dom;

import java.math.BigDecimal;

import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;

import org.incode.module.base.dom.utils.MathUtils;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.lease.LeaseTermForFixed")
public class LeaseTermForFixed extends LeaseTerm {

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED)
    @Getter @Setter
    private BigDecimal value;

    public LeaseTerm changeValue(
            final BigDecimal value) {
        setValue(value);
        return this;
    }

    public boolean hideChangeValue(){
        if (getLeaseItem().getLease().getStatus()==LeaseStatus.PREVIEW) return true;
        return false;
    }

    public String validateChangeValue(final BigDecimal value) {
        if (LeaseItemType.RENT_DISCOUNT_FIXED.equals(getLeaseItem().getType()) && isPositive(value)) {
            return "Discount should be negative or zero";
        }
        return null;
    }

    private static boolean isPositive(final BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    @Programmatic
    public void negateAmountsAndApplyPercentage(final BigDecimal discountPercentage){
        // TODO: check if this makes sense as there is no override on copyValuesTo
        if (getValue()!=null){
            setValue(applyPercentage(getValue(), discountPercentage).negate());
        }
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getEffectiveValue() {
        return MathUtils.firstNonZero(getValue());
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public BigDecimal valueForDate(final LocalDate dueDate) {
        return getValue();
    }

    // //////////////////////////////////////

    @Override
    public LeaseTermValueType valueType() {
        return LeaseTermValueType.FIXED;
    }

    @Override
    public void copyValuesTo(final LeaseTerm target) {
        LeaseTermForFixed t = (LeaseTermForFixed) target;
        super.copyValuesTo(t);
        t.setValue(getValue());
    }

}
