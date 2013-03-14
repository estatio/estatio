package org.estatio.dom.index;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.joda.time.LocalDate;


public class IndexationCalculator {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private Index index;
    private LocalDate baseIndexStartDate;
    private LocalDate nextIndexStartDate;
    private BigDecimal baseIndexValue;
    private BigDecimal nextIndexValue;
    private BigDecimal indexationFactor;
    private BigDecimal rebaseFactor;
    private BigDecimal baseValue;
    private BigDecimal indexedValue;
    private BigDecimal indexationPercentage;

    public IndexationCalculator(Index index, LocalDate baseIndexStartDate,LocalDate nextIndexStartDate, BigDecimal baseValue) {
        super();
        this.index = index;
        this.baseIndexStartDate = baseIndexStartDate;
        this.nextIndexStartDate = nextIndexStartDate;
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

    public void calculate(Indexable input) {
        this.calculate();
        input.setBaseIndexValue(baseIndexValue);
        input.setNextIndexValue(nextIndexValue);
        input.setIndexationPercentage(indexationPercentage);
        input.setIndexedValue(indexedValue);
    }

    public void calculate() {
        if (index != null){
            index.initialize(this, baseIndexStartDate, nextIndexStartDate);
            if (this.baseIndexValue != null && this.nextIndexValue !=null){
                indexationFactor = nextIndexValue.divide(baseIndexValue, 4, RoundingMode.HALF_UP).multiply(rebaseFactor).setScale(3, RoundingMode.HALF_UP);
                indexationPercentage = (indexationFactor.subtract(BigDecimal.ONE)).multiply(ONE_HUNDRED).setScale(1);
                indexedValue = baseValue.multiply(indexationFactor).setScale(2, RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP);
            }
        }
    }

    public void setBaseIndexValue(BigDecimal baseIndexValue) {
        this.baseIndexValue = baseIndexValue;
    }

    public void setNextIndexValue(BigDecimal nextIndexValue) {
        this.nextIndexValue = nextIndexValue;
    }

    public void setRebaseFactor(BigDecimal rebaseFactor) {
        this.rebaseFactor = rebaseFactor;
    }

}
