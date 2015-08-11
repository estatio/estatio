package org.estatio.app.budget;

import java.math.BigDecimal;

/**
 * Created by jodo on 07/08/15.
 */
public enum Rounding {

    DECIMAL2 {
        public int digits() {
            return 2;
        }
        public BigDecimal baseFactor() {
            return new BigDecimal(100);
        }
        public BigDecimal correctionFactor() {
            return new BigDecimal(0.010000);
        }
    },
    DECIMAL3 {
        public int digits() {
            return 3;
        }
        public BigDecimal baseFactor() {
            return new BigDecimal(1000);
        }
        public BigDecimal correctionFactor() {
            return new BigDecimal(0.001000);
        }
    };

    public abstract int digits();

    public abstract BigDecimal baseFactor();

    public abstract BigDecimal correctionFactor();
}
