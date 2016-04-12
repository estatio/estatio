package org.estatio.dom.lease.indexation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public enum IndexationCalculationMethod {

    DEFAULT {
        @Override
        IndexationResult calc(final Indexable input) {
            return ITALY.calc(input);
        }
    },
    ITALY {
        @Override
        IndexationResult calc(final Indexable input) {
            BigDecimal indexedValue = null;
            BigDecimal indexationPercentage = null;
            final BigDecimal baseIndexValue = input.getBaseIndexValue();
            final BigDecimal nextIndexValue = input.getNextIndexValue();
            final BigDecimal rebaseFactor = input.getRebaseFactor();
            final BigDecimal baseValue = input.getBaseValue();
            if (baseIndexValue != null && nextIndexValue != null) {
                final BigDecimal indexationFactor = nextIndexValue
                        .divide(baseIndexValue, MathContext.DECIMAL64)
                        .multiply(rebaseFactor, MathContext.DECIMAL64).setScale(3, RoundingMode.HALF_EVEN);
                indexationPercentage = (indexationFactor
                        .subtract(BigDecimal.ONE))
                        .multiply(ONE_HUNDRED).setScale(1, RoundingMode.HALF_EVEN);
                final BigDecimal levelledIndexationFactor = indexationPercentage
                        .multiply((input.getLevellingPercentage() == null ? ONE_HUNDRED : input.getLevellingPercentage()).divide(ONE_HUNDRED))
                        .divide(ONE_HUNDRED)
                        .add(BigDecimal.ONE);
                if (baseValue != null) {
                    indexedValue = baseValue
                            .multiply(levelledIndexationFactor)
                            .setScale(2, RoundingMode.HALF_EVEN);
                }
            }
            return new IndexationResult(indexedValue, indexationPercentage, baseIndexValue, nextIndexValue);
        }
    },
    FRANCE {
        @Override
        IndexationResult calc(final Indexable input) {
            BigDecimal indexedValue = null;
            BigDecimal indexationPercentage = null;
            final BigDecimal baseIndexValue = input.getBaseIndexValue();
            final BigDecimal nextIndexValue = input.getNextIndexValue();
            final BigDecimal rebaseFactor = input.getRebaseFactor();
            final BigDecimal baseValue = input.getBaseValue();
            if (baseIndexValue != null && nextIndexValue != null) {
                final BigDecimal indexationFactor = nextIndexValue
                        .divide(baseIndexValue, MathContext.DECIMAL64)
                        .multiply(rebaseFactor, MathContext.DECIMAL64);
                indexationPercentage = (indexationFactor
                        .subtract(BigDecimal.ONE))
                        .multiply(ONE_HUNDRED).setScale(3, RoundingMode.HALF_EVEN);
                if (baseValue != null) {
                    indexedValue = baseValue
                            .multiply(indexationFactor)
                            .setScale(2, RoundingMode.HALF_EVEN);
                }
            }
            return new IndexationResult(indexedValue, indexationPercentage, baseIndexValue, nextIndexValue);
        }
    };

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    abstract IndexationResult calc(final Indexable input);

    public static IndexationResult calculate(final Indexable input) {
        return input.getIndexationCalculation() == null ? DEFAULT.calc(input) : input.getIndexationCalculation().calc(input);
    }
}
