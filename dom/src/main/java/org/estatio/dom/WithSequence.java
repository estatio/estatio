package org.estatio.dom;

import java.math.BigInteger;

import com.google.common.collect.Ordering;

public interface WithSequence {

    public BigInteger getSequence();
    public void setSequence(BigInteger sequence);
    
//    static Ordering<WithSequence> ORDERING_BY_SEQUENCE_DESC = new Ordering<WithSequence>() {
//        public int compare(WithSequence left, WithSequence right) {
//            return Ordering.natural().nullsLast().reverse().compare(left.getSequence(), right.getSequence());
//        };
//    };
//
//    static Ordering<WithSequence> ORDERING_BY_SEQUENCE_ASC = new Ordering<WithSequence>() {
//        public int compare(WithSequence left, WithSequence right) {
//            return Ordering.natural().nullsFirst().compare(left.getSequence(), right.getSequence());
//        };
//    };

}
