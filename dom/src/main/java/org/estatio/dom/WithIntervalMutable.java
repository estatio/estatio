/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Named;

public interface WithIntervalMutable<T extends WithIntervalMutable<T>> extends WithInterval<T> {

    @ActionSemantics(Of.IDEMPOTENT)
    public T changeDates(
            final @Named("Start Date") @Optional LocalDate startDate, 
            final @Named("End Date") @Optional LocalDate endDate);

    public LocalDate default0ChangeDates();
    public LocalDate default1ChangeDates();
    
    public String validateChangeDates(
            final LocalDate startDate, 
            final LocalDate endDate);


    
    /**
     * Helper class for implementations to delegate to.
     * 
     * <p>
     * If the class implements {@link WithIntervalContiguous} then use {@link WithIntervalContiguous.Helper} instead.
     */
    public static class Helper<T extends WithIntervalMutable<T>> {
        
        private T withInterval;

        public Helper(final T withInterval) {
            this.withInterval = withInterval;
        }
        
        public T changeDates(
                final LocalDate startDate,
                final LocalDate endDate) {
            withInterval.setStartDate(startDate);
            withInterval.setEndDate(endDate);
            return withInterval;
        }

        public LocalDate default0ChangeDates() {
            return withInterval.getEffectiveStartDate();
        }

        public LocalDate default1ChangeDates() {
            return withInterval.getEffectiveEndDate();
        }

        public String validateChangeDates(
                final LocalDate startDate,
                final LocalDate endDate) {
            if(startDate != null && endDate != null && !startDate.isBefore(endDate)) {
                return "End date must be after start date";
            }
            return null;
        }
    }

}
