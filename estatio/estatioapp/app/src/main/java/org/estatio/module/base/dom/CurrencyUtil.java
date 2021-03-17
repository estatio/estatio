package org.estatio.module.base.dom;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CurrencyUtil {

    public static String formattedAmount(
            final BigDecimal currencyAmount,
            final String atPath) {
        final Locale locale = LocaleUtil.deriveLocale(atPath);
        NumberFormat format = NumberFormat.getNumberInstance(locale);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        //format.setCurrency(Currency.getInstance(locale));
        return format.format(currencyAmount);
    }

}
