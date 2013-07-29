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

import java.util.Iterator;
import java.util.SortedSet;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.name.Named;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;

public interface WithInterval<T extends WithInterval<T>> extends WithStartDate {

    /**
     * The start date of the interval.
     * 
     * <p>
     * A value of <tt>null</tt> implies that the {@link #getParentWithInterval() parent}'s
     * start date should be used.  If that is <tt>null</tt>, then implies 'the beginning of time'.
     */
    @Disabled
    @Optional
    public LocalDate getStartDate();
    public void setStartDate(LocalDate startDate);

    /**
     * The end date of the interval.
     * 
     * <p>
     * A value of <tt>null</tt> implies that the {@link #getParentWithInterval() parent}'s
     * end date should be used.  If that is <tt>null</tt>, then implies 'the end of time'.
     */
    @Optional
    @Disabled
    public LocalDate getEndDate();
    public void setEndDate(LocalDate endDate);

    @Hidden
    public WithInterval<?> getParentWithInterval();

    /**
     * Either the {@link #getStartDate() start date}, or the {@link #getParentWithInterval() parent}'s
     * start date (if any). 
     */
    @Hidden
    public LocalDate getEffectiveStartDate();
    /**
     * Either the {@link #getEndDate() end date}, or the {@link #getParentWithInterval() parent}'s
     * end date (if any). 
     */
    @Hidden
    public LocalDate getEffectiveEndDate();

    @Programmatic
    public LocalDateInterval getInterval();


    

    /**
     * The interval that immediately precedes this one, if any.
     * 
     * <p>
     * The predecessor's {@link #getEndDate() end date} is the day before this interval's
     * {@link #getStartDate() start date}.
     */
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    public T getPrevious();

    /**
     * The interval that immediately succeeds this one, if any.
     * 
     * <p>
     * The successor's {@link #getStartDate() start date} is the day after this interval's
     * {@link #getEndDate() end date}.
     */
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    public T getNext();

    
    
    public static class Util {
        private Util() {}
        public static LocalDate effectiveStartDateOf(final WithInterval<?> wi) {
            if (wi.getStartDate() != null) {
                return wi.getStartDate();
            } 
            final WithInterval<?> parentWi = wi.getParentWithInterval();
            if (parentWi != null) {
                return parentWi.getEffectiveStartDate();
            } 
            return null;
        }
        public static LocalDate effectiveEndDateOf(final WithInterval<?> wi) {
            if (wi.getEndDate() != null) {
                return wi.getEndDate();
            } 
            final WithInterval<?> parentWi = wi.getParentWithInterval();
            if (parentWi != null) {
                return parentWi.getEffectiveEndDate();
            } 
            return null;
        }
        public static <T extends WithInterval<T>> T find(SortedSet<T> roles, Predicate<T> predicate) {
            final Iterable<T> filter = Iterables.filter(roles, predicate);
            final Iterator<T> iterator = filter.iterator();
            return iterator.hasNext()? iterator.next(): null;
        }
    }

    public interface Factory {
        void newRole(LocalDate startDate, LocalDate endDate);
    }
    
    public static class SucceededBy {
        
        private final WithInterval<?> withInterval;
        public SucceededBy(WithInterval<?> withInterval) {
            this.withInterval = withInterval;
        }

        public void succeededBy(
                final LocalDate startDate, 
                final LocalDate endDate,
                final WithInterval.Factory factory) {
            final WithInterval<?> successor = withInterval.getNext();
            if(successor != null) {
                successor.setStartDate(endDate);
            }
            withInterval.setEndDate(startDate);
            factory.newRole(startDate, endDate);

        }
                
        public String validateSucceededBy(
                final LocalDate startDate, 
                final LocalDate endDate) {
            if(withInterval.getStartDate() != null && !withInterval.getStartDate().isBefore(startDate)) {
                return "Successor must start after existing";
            }
            final WithInterval<?> successor = withInterval.getNext();
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
    }
    
    public static class PrecededBy {
        
        private final WithInterval<?> withInterval;
        public PrecededBy(WithInterval<?> withInterval) {
            this.withInterval = withInterval;
        }

        public void precededBy(
                final LocalDate startDate, 
                final LocalDate endDate,
                final WithInterval.Factory factory) {
            
            final WithInterval<?> predecessor = withInterval.getPrevious();
            if(predecessor != null) {
                predecessor.setEndDate(startDate);
            }
            withInterval.setStartDate(endDate);
            factory.newRole(startDate, endDate);
        }
                
        public String validatePrecededBy(
                final LocalDate startDate, 
                final LocalDate endDate) {
            if(withInterval.getEndDate() != null && !withInterval.getEndDate().isAfter(endDate)) {
                return "Predecessor must end before existing";
            }
            final WithInterval<?> predecessor = withInterval.getPrevious();
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
    public static class UpdateDates {
        
        private final WithInterval<?> withInterval;
        public UpdateDates(WithInterval<?> withInterval) {
            this.withInterval = withInterval;
        }
        
        public void updateDates(
                final LocalDate startDate, 
                final LocalDate endDate) {
            
            final WithInterval<?> predecessor = withInterval.getPrevious();
            if(predecessor != null) {
                predecessor.setEndDate(startDate);
            }
            final WithInterval<?> successor = withInterval.getNext();
            if(successor != null) {
                successor.setStartDate(endDate);
            }
            withInterval.setStartDate(startDate);
            withInterval.setEndDate(endDate);
        }

        public String validateUpdateDates(
                final LocalDate startDate, 
                final LocalDate endDate) {

            if(startDate != null && endDate != null && !startDate.isBefore(endDate)) {
                return "Start date cannot be on/after the end date";
            }
            final WithInterval<?> predecessor = withInterval.getPrevious();
            if (predecessor != null) {
                if(startDate == null) {
                    return "Start date cannot be set to null if there is a predecessor";
                }
                if(predecessor.getStartDate() != null && !predecessor.getStartDate().isBefore(startDate)) {
                    return "Start date cannot be on/before start of current predecessor";
                }
            }
            final WithInterval<?> successor = withInterval.getNext();
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

}
