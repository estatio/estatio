package org.estatio.dom.utils;

import java.math.BigDecimal;

public class FinancialAmountUtil {

    public static BigDecimal subtractHandlingNulls(final BigDecimal amount, final BigDecimal amountToSubtract){
        if (amountToSubtract==null) return amount;
        return amount == null ? amountToSubtract.negate() : amount.subtract(amountToSubtract);
    }

    public static BigDecimal addHandlingNulls(final BigDecimal amount, final BigDecimal amountToAdd){
        if (amountToAdd==null) return amount;
        return amount == null ? amountToAdd : amount.add(amountToAdd);
    }

}
