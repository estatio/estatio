package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {

    static final Pattern PATTERN = Pattern.compile("^\\s*(?<year>\\d{4})[/].+\\s*$");

    static String asFinancialYear(final String codaPeriod) {
        final Matcher matcher = PATTERN.matcher(codaPeriod);
        return matcher.matches() ? "F" + matcher.group("year") : null;
    }

    static BigDecimal subtract(final BigDecimal m, final BigDecimal n) {
        return m == null ? null : m.subtract(n != null ? n : BigDecimal.ZERO);
    }

}
