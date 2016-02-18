package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.MathContext;

public enum DepositType {
    MONTH {
        @Override
        BigDecimal calculation(BigDecimal actualMGR) {
            return actualMGR.divide(new BigDecimal("12"), MathContext.DECIMAL64).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    },
    QUARTER {
        @Override
        BigDecimal calculation(BigDecimal actualMGR) {
            return actualMGR.divide(new BigDecimal("4"), MathContext.DECIMAL64).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    },
    HALF_YEAR{
        @Override
        BigDecimal calculation(BigDecimal actualMGR) {
            return actualMGR.divide(new BigDecimal("2"), MathContext.DECIMAL64).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    },
    MANUAL{
        @Override
        BigDecimal calculation(BigDecimal actualMGR) {
            return BigDecimal.ZERO.setScale(2);
        }
    };

    abstract BigDecimal calculation(final BigDecimal actualMGR);
}
