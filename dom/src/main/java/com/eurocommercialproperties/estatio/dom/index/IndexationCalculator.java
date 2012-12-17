package com.eurocommercialproperties.estatio.dom.index;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.joda.time.LocalDate;


public class IndexationCalculator {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private Index index;
    private LocalDate baseIndexStartDate;
    private LocalDate baseIndexEndDate;
    private LocalDate nextIndexStartDate;
    private LocalDate nextIndexEndDate;
    private BigDecimal baseIndexValue;
    private BigDecimal nextIndexValue;
    private BigDecimal indexationFactor;
    private BigDecimal rebaseFactor;
    private BigDecimal baseValue;
    private BigDecimal indexedValue;
    private BigDecimal indexationPercentage;

    public IndexationCalculator(Index index, LocalDate baseIndexStartDate, LocalDate baseIndexEndDate, LocalDate nextIndexStartDate, LocalDate nextIndexEndDate, BigDecimal baseValue) {
        super();
        this.index = index;
        this.baseIndexStartDate = baseIndexStartDate;
        this.baseIndexEndDate = baseIndexEndDate;
        this.nextIndexStartDate = nextIndexStartDate;
        this.nextIndexEndDate = nextIndexEndDate;
        this.baseValue = baseValue;
    }

    public BigDecimal getBaseIndexValue() {
        return baseIndexValue;
    }

    public BigDecimal getNextIndexValue() {
        return nextIndexValue;
    }

    public BigDecimal getIndexationFactor() {
        return indexationFactor;
    }

    public BigDecimal getIndexedValue() {
        return indexedValue;
    }

    public BigDecimal getIndexationPercentage() {
        return indexationPercentage;
    }

    public void calculate() {
        BigDecimal[] indexValues = index.getIndexationValues(baseIndexStartDate, nextIndexStartDate);
        this.baseIndexValue = indexValues[0];
        this.nextIndexValue = indexValues[1];
        this.rebaseFactor = indexValues[2];
        if (this.baseIndexValue != null && this.nextIndexValue !=null){
            indexationFactor = nextIndexValue.divide(baseIndexValue, 5, RoundingMode.HALF_UP).multiply(rebaseFactor).setScale(2, RoundingMode.HALF_UP);
            indexationPercentage = indexationFactor.subtract(BigDecimal.ONE).multiply(ONE_HUNDRED).setScale(0);
            indexedValue = baseValue.multiply(indexationFactor);
        }
    }
}
