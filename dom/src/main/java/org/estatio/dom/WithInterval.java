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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;

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
}
