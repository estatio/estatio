package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.MathContext;

public enum Fraction {
    M1 {
        @Override
        BigDecimal calculation(BigDecimal actualMGR) {
            return actualMGR.divide(new BigDecimal("12"), MathContext.DECIMAL64).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    },
    M2 {
        @Override
        BigDecimal calculation(BigDecimal actualMGR) {
            return actualMGR.divide(new BigDecimal("6"), MathContext.DECIMAL64).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    },
    M3 {
        @Override
        BigDecimal calculation(BigDecimal actualMGR) {
            return actualMGR.divide(new BigDecimal("4"), MathContext.DECIMAL64).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    },
    M6 {
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
