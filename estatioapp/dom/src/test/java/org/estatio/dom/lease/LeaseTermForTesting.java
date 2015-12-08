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

import org.joda.time.LocalDate;

import org.estatio.dom.valuetypes.LocalDateInterval;

public class LeaseTermForTesting extends LeaseTerm {

    public LeaseTermForTesting() {
    }

    public LeaseTermForTesting(LeaseItem leaseItem, LocalDateInterval interval, BigDecimal value) {
        super();
        this.value = value;
        setStartDate(interval.startDate());
        setEndDate(interval.endDate());
        setLeaseItem(leaseItem);
    }

    public LeaseTermForTesting(LeaseItem leaseItem, LocalDate startDate, LocalDate endDate, BigDecimal value) {
        super();
        this.value = value;
        setStartDate(startDate);
        setEndDate(endDate);
        setLeaseItem(leaseItem);
    }

    @Override
    public BigDecimal getEffectiveValue() {
        return value;
    }
    
    @Override
    public BigDecimal valueForDate(LocalDate date){
        // after the end date the adjusted value is returned
        if (getEndDate() != null && date.compareTo(getEndDate()) >= 0) {
            return adjustedValue != null ? adjustedValue : value;
        }
        return value;
    }

    // //////////////////////////////////////

    private BigDecimal value;

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    private BigDecimal adjustedValue;
    
    public void setAdjustedValue(final BigDecimal adjustedValue) {
        this.adjustedValue = adjustedValue;
    }
}
