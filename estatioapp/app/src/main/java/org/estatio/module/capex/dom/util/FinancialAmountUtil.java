package org.estatio.module.capex.dom.util;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.estatio.module.tax.dom.Tax;

public class FinancialAmountUtil {

    public static BigDecimal subtractHandlingNulls(final BigDecimal amount, final BigDecimal amountToSubtract) {
        if (amountToSubtract == null)
            return amount;
        return amount == null ? amountToSubtract.negate() : amount.subtract(amountToSubtract);
    }

    public static BigDecimal addHandlingNulls(final BigDecimal amount, final BigDecimal amountToAdd) {
        if (amountToAdd == null)
            return amount;
        return amount == null ? amountToAdd : amount.add(amountToAdd);
    }

    public static BigDecimal determineVatAmount(
            final BigDecimal netAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate now) {
        if (tax != null && netAmount != null)
            return tax.grossFromNet(netAmount, now).subtract(netAmount);

        return netAmount != null && grossAmount != null ?
                grossAmount.subtract(netAmount) :
                null;
    }

    public static BigDecimal determineNetAmount(
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate now) {
        if (tax != null && grossAmount != null)
            return tax.netFromGross(grossAmount, now);

        return vatAmount != null && grossAmount != null ?
                grossAmount.subtract(vatAmount) :
                null;
    }

    public static BigDecimal determineGrossAmount(
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final Tax tax,
            final LocalDate now) {
        if (tax != null && netAmount != null)
            return tax.grossFromNet(netAmount, now);

        return netAmount != null && vatAmount != null ?
                netAmount.add(vatAmount) :
                null;
    }

}
