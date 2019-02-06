package org.estatio.module.capex.spiimpl.docs.rml;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {
    static String handleSrlSuffix(String str) {
        return str.endsWith("Srl") ? str.substring(0, str.length() - 3) + "S.r.l." : str;
    }

    static String stripSrlSuffix(String str) {
        return str.endsWith(" Srl") ? str.substring(0, str.length() - 4) : str;
    }

    static String capitalizeSentence(String name) {
        final String[] split = name.split("\\s");
        return Arrays.stream(split)
                .map(String::toLowerCase)
                .map(Util::capitalizeWord)
                .collect(Collectors.joining(" "));
    }

    private static String capitalizeWord(final String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    static Locale deriveLocale(final String atPath) {
        if(atPath != null) {
            if(atPath.startsWith("/ITA")) {
                return Locale.ITALIAN;
            }
            if(atPath.startsWith("/FRA")) {
                return Locale.FRENCH;
            }
            if(atPath.startsWith("/BEL")) {
                return Locale.FRENCH;
            }
            if(atPath.startsWith("/SWE")) {
                return Locale.forLanguageTag("SWE");
            }
        }
        return Locale.ENGLISH;
    }

    static String formattedAmount(
            final BigDecimal currencyAmount,
            final String atPath) {
        final Locale locale = deriveLocale(atPath);
        NumberFormat format = NumberFormat.getNumberInstance(locale);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        //format.setCurrency(Currency.getInstance(locale));
        return format.format(currencyAmount);
    }
}
