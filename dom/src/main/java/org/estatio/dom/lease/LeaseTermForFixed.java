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
package org.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.utils.MathUtils;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class LeaseTermForFixed extends LeaseTerm {

    private BigDecimal value;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
    @Disabled
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    public LeaseTerm changeValue(
            final @Named("Value") BigDecimal value) {
        setValue(value);
        return this;
    }

    public String validateChangeValue(final BigDecimal value) {
        if (LeaseItemType.DISCOUNT.equals(getLeaseItem().getType()) && isPositive(value)) {
            return "Discount should be negative or zero";
        }
        return null;
    }

    private static boolean isPositive(final BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
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

}
