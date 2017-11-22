package org.estatio.module.lease.dom;

import java.math.BigDecimal;
import java.math.MathContext;

public enum Fraction {
    M1(1,12),
    M2(2,12),
    M3(3,12),
    M6(6,12),
    MANUAL(1,1);

    Fraction(int nom, int denom) {
        this.nom = nom;
        this.denom = denom;
    }

    private int nom;
    private int denom;

    public BigDecimal fractionOf(BigDecimal value){
        return value.multiply(new BigDecimal(nom)).divide(new BigDecimal(denom), MathContext.DECIMAL64).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
