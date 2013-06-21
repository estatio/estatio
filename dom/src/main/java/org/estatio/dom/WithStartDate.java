package org.estatio.dom;

import org.joda.time.LocalDate;

public interface WithStartDate {

    public LocalDate getStartDate();
    public void setStartDate(LocalDate localDate);
    
//    static Ordering<WithStartDate> ORDERING_BY_START_DATE_DESC = new Ordering<WithStartDate>() {
//        public int compare(WithStartDate left, WithStartDate right) {
//            return Ordering.natural().nullsLast().reverse().compare(left.getStartDate(), right.getStartDate());
//        };
//    };

}
