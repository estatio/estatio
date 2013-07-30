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
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.party.Party;

public interface WithIntervalContiguous<T extends WithIntervalContiguous<T>> extends WithIntervalMutable<T> {

    
    /**
     * The interval that immediately precedes this one, if any.
     * 
     * <p>
     * The predecessor's {@link #getEndDate() end date} is the day before this interval's
     * {@link #getStartDate() start date}.
     * 
     * <p>
     * Implementations where successive intervals are NOT contiguous should instead implement {@link WithIntervalChained}.
     */
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    public T getPredecessor();

    /**
     * The interval that immediately succeeds this one, if any.
     * 
     * <p>
     * The successor's {@link #getStartDate() start date} is the day after this interval's
     * {@link #getEndDate() end date}.
     * 
     * <p>
     * Implementations where successive intervals are NOT contiguous should instead implement {@link WithIntervalChained}.
     */
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    public T getSuccessor();
    
    
    // //////////////////////////////////////

    /**
     * Helper class to delegate to implementations of {@link WithIntervalContiguous#changeDates(LocalDate, LocalDate)}
     */
    public static class ChangeDates<T extends WithIntervalContiguous<T>> {
        
        private final T withInterval;
        public ChangeDates(T withInterval) {
            this.withInterval = withInterval;
        }
        
        public T changeDates(
                final LocalDate startDate, 
                final LocalDate endDate) {
            
            final T predecessor = withInterval.getPredecessor();
            if(predecessor != null) {
                predecessor.setEndDate(startDate);
            }
            final T successor = withInterval.getSuccessor();
            if(successor != null) {
                successor.setStartDate(endDate);
            }
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
            final T predecessor = withInterval.getPredecessor();
            if (predecessor != null) {
                if(startDate == null) {
                    return "Start date cannot be set to null if there is a predecessor";
                }
                if(predecessor.getStartDate() != null && !predecessor.getStartDate().isBefore(startDate)) {
                    return "Start date cannot be on/before start of current predecessor";
                }
            }
            final T successor = withInterval.getSuccessor();
            if (successor != null) {
                if(endDate == null) {
                    return "End date cannot be set to null if there is a successor";
                }
                if(successor.getEndDate() != null && !successor.getEndDate().isAfter(endDate)) {
                    return "End date cannot be on/after end of current successor";
                }
            }
            return null;
        }

    }
    

    // //////////////////////////////////////

    
    public interface Factory<T extends WithIntervalContiguous<T>> {
        T newRole(LocalDate startDate, LocalDate endDate);
    }
    
    /**
     * Helper class for implementations that provide a <tt>succeededBy</tt> or
     * <tt>precededBy</tt> action.
     * 
     * <p>
     * Note that these methods are <i>not</i> part of the {@link WithIntervalContiguous} interface
     * because parameters vary across implementations.
     */
    public static class SucceedPrecede<T extends WithIntervalContiguous<T>> {
        
        private final T withInterval;
        public SucceedPrecede(T withInterval) {
            this.withInterval = withInterval;
        }

        public T succeededBy(
                final LocalDate startDate, 
                final LocalDate endDate,
                final WithIntervalContiguous.Factory<T> factory) {
            final WithInterval<?> successor = withInterval.getSuccessor();
            if(successor != null) {
                successor.setStartDate(endDate);
            }
            withInterval.setEndDate(startDate);
            return factory.newRole(startDate, endDate);
        }
                
        public String validateSucceededBy(
                final LocalDate startDate, 
                final LocalDate endDate) {
            if(withInterval.getStartDate() != null && !withInterval.getStartDate().isBefore(startDate)) {
                return "Successor must start after existing";
            }
            final WithInterval<?> successor = withInterval.getSuccessor();
            if(successor != null) {
                if (endDate == null) {
                    return "An end date is required because a successor already exists";
                }
                if(successor.getEndDate() != null && !endDate.isBefore(successor.getEndDate())) {
                    return "Successor must end prior to existing successor";
                }
            }
            return null;
        }

        public T precededBy(
                final LocalDate startDate, 
                final LocalDate endDate,
                final WithIntervalContiguous.Factory<T> factory) {
            
            final WithInterval<?> predecessor = withInterval.getPredecessor();
            if(predecessor != null) {
                predecessor.setEndDate(startDate);
            }
            withInterval.setStartDate(endDate);
            return factory.newRole(startDate, endDate);
        }
                
        public String validatePrecededBy(
                final LocalDate startDate, 
                final LocalDate endDate) {
            if(withInterval.getEndDate() != null && !withInterval.getEndDate().isAfter(endDate)) {
                return "Predecessor must end before existing";
            }
            final WithInterval<?> predecessor = withInterval.getPredecessor();
            if(predecessor != null) {
                if (startDate == null) {
                    return "A start date is required because a predecessor already exists";
                }
                if(predecessor.getStartDate() != null && !startDate.isAfter(predecessor.getStartDate())) {
                    return "Predecessor must start after existing predecessor";
                }
            }
            return null;
        }

    }

    
}
