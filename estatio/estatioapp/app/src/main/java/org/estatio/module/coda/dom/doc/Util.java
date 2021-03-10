package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {

    static final Pattern PATTERN = Pattern.compile("^\\s*(?<year>\\d{4})[/](?<month>\\d{1,2})+s*$");

    static String asFinancialYear(final String codaPeriod) {
        final Matcher matcher = PATTERN.matcher(codaPeriod);
        if (matcher.matches()) {
            final String yearStr = matcher.group("year");
            final int year = Integer.parseInt(yearStr);
            final String monthStr = matcher.group("month");
            final int month = Integer.parseInt(monthStr);
            if (year == 2020 && month > 12) return "F2021";
            if (year>=2021) return matcher.group("year");
        }
        return matcher.matches() ? "F" + matcher.group("year") : null;
    }

    static BigDecimal subtract(final BigDecimal m, final BigDecimal n) {
        return m == null ? null : m.subtract(n != null ? n : BigDecimal.ZERO);
    }
    static BigDecimal add(final BigDecimal m, final BigDecimal n) {
        return m == null ? null : m.add(n != null ? n : BigDecimal.ZERO);
    }

}
