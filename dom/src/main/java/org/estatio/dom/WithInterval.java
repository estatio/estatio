package org.estatio.dom;

import com.google.common.base.Objects;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ChangedEvent;
import org.apache.isis.applib.util.ObjectContracts;

import org.estatio.dom.WithInterval.StartDateChangedEvent;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.valuetypes.LocalDateInterval;

public interface WithInterval<T extends WithInterval<T>> extends WithStartDate {

    
    @Optional
    @Disabled
    public LocalDate getEndDate();
    public void setEndDate(LocalDate endDate);
    
    public void modifyEndDate(LocalDate endDate);
    public void clearEndDate();

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

    public class StartDateChangedEvent extends ChangedEvent<WithInterval<?>, LocalDate> {
        public StartDateChangedEvent(WithInterval<?> source, LocalDate oldValue, LocalDate newValue) {
            super(source, oldValue, newValue);
        }

        /**
         * Syncs the {@link WithInterval#getStartDate() start date} of the provided <tt>syncee</tt> with
         * the new start date of the event's source, checking that the source is the
         * object to which this is synced to.
         */
        public void sync(final WithInterval<?> syncedTo, final WithInterval<?> syncee) {
            final WithInterval<?> source = getSource();
            if(source != syncedTo) {
                return;
            }
            if(syncee.getStartDate() == null || Objects.equal(getOldValue(), syncee.getStartDate())) {
                syncee.setStartDate(getNewValue());
            }
        }
    }
    public class EndDateChangedEvent extends ChangedEvent<WithInterval<?>, LocalDate> {
        public EndDateChangedEvent(WithInterval<?> source, LocalDate oldValue, LocalDate newValue) {
            super(source, oldValue, newValue);
        }
        /**
         * Syncs the {@link WithInterval#getEndDate() end date} of the provided <tt>syncee</tt> with
         * the new start date of the event's source, checking that the source is the
         * object to which this is synced to.
         */
        public void sync(final WithInterval<?> syncedTo, final WithInterval<?> syncee) {
            final WithInterval<?> source = getSource();
            if(source != syncedTo) {
                return;
            }
            if(syncee.getEndDate() == null || Objects.equal(getOldValue(), syncee.getEndDate())) {
                syncee.setEndDate(getNewValue());
            }
        }
    }
}
