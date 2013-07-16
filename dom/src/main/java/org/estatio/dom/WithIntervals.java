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

import java.util.SortedSet;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;

import org.joda.time.LocalDate;

/**
 * Utility class for managing collections of {@link WithInterval}s.
 * 
 */
public class WithIntervals {
    
    private WithIntervals(){}

    /**
     * An extension to the concept of a {@link Predicate} that can be evaluated
     * with respect to some root object.
     * 
     * <p>
     * Used by {@link WithIntervals#addInterval(SortedSet, Object, LocalDate, LocalDate, RootedPredicate, WithIntervalMutator)}
     * in order to filter objects in the provided {@link SortedSet}.
     * 
     * <p>
     * This implementation is somewhat akin to the idea of a partially applied function.
     */
    public static abstract class RootedPredicate<P,Q> implements Predicate<Q> {
        private P root;
        public void setRoot(P root) {
            this.root = root;
        }
        @Override
        public final boolean apply(Q input) {
            if(root == null) {
                throw new IllegalStateException("must call setRoot(...) first");
            }
            return doApply(root, input);
        }
        protected abstract boolean doApply(P root, Q input);
    }

    /**
     * Abstracts out the act of creating new {@link WithInterval}s, or of
     * updating existing ones.
     * 
     * <p>
     * Used by {@link WithIntervals#addInterval(SortedSet, Object, LocalDate, LocalDate, RootedPredicate, WithIntervalMutator)}
     * in order to manipulate objects in the provided {@link SortedSet}.
     */
    public interface WithIntervalMutator<T extends WithInterval<T>> {
        public void newInterval(LocalDate startDate, LocalDate endDate);
        public void copyExisting(T existing, LocalDate startDate, LocalDate endDate);
        public void replaceExactMatch(T existing);
        public void adjustExistingStartDate(T existing, LocalDate date);
        public void adjustExistingEndDate(T existing, LocalDate date);
    }

    /**
     * Adds a new {@link WithInterval} in the provided {@link SortedSet}, adjusting any existing {@link WithInterval}s
     * accordingly.
     */
    public static <R extends WithInterval<R>, Y> void addInterval(final SortedSet<R> existingRoles, final Y type, final LocalDate startDate, final LocalDate endDate, final RootedPredicate<R, Y> predicate, final WithIntervalMutator<R> mutator) {
        for (R existingRole : existingRoles) {
            predicate.setRoot(existingRole);
            if(!predicate.apply(type)) {
                continue;
            }
            final LocalDate existingStartDate = existingRole.getStartDate();
            final LocalDate existingEndDate = existingRole.getEndDate();
            
            // replace existing if exact match
            if(Objects.equal(existingStartDate, startDate) &&
               Objects.equal(existingEndDate, endDate)) {
                mutator.replaceExactMatch(existingRole);
                return;
            }
            
            final boolean newStartsBeforeExisting = 
                    existingStartDate !=null && (startDate == null || startDate.isBefore(existingStartDate));
            final boolean newEndsAfterExisting = 
                    existingEndDate !=null && (endDate == null || endDate.isAfter(existingEndDate));
    
            // bisect new
            if(newStartsBeforeExisting && newEndsAfterExisting) {
                mutator.newInterval(startDate, existingStartDate);
                mutator.newInterval(existingEndDate, endDate);
                return;
            }
            
            final boolean existingStartsBeforeNew = 
                    startDate !=null && (existingStartDate == null || existingStartDate.isBefore(startDate));
            final boolean existingEndsAfterNew = 
                    endDate !=null && (existingEndDate == null || existingEndDate.isAfter(endDate));
    
            // bisect existing
            if(existingStartsBeforeNew && existingEndsAfterNew) {
                mutator.copyExisting(existingRole, existingStartDate, startDate);
                mutator.newInterval(startDate, endDate);
                mutator.adjustExistingStartDate(existingRole, endDate);
                return;
            }
    
            // adjust
            if(startDate != null){
                if(existingRole.getInterval().contains(startDate)) {
                    mutator.adjustExistingEndDate(existingRole, startDate);
                }
            }
            
            // adjust
            if(endDate != null){
                if(existingRole.getInterval().contains(endDate)) {
                    existingRole.setStartDate(endDate);
                }
            }
        }
        
        mutator.newInterval(startDate, endDate);
    }

}
