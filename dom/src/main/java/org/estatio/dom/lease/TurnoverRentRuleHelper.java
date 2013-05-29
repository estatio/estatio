package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TurnoverRentRuleHelper {

    private String rules[];

    public TurnoverRentRuleHelper(String rule) {
        if (rule != null && rule.trim().length() != 0)
            rules = rule.split(";");
    }

    public boolean isValid() {
        return (rules != null && isValidRule());
    }

    private boolean isValidRule() {
        // check for uneven rules
        if (rules == null || rules.length % 2 == 0)
            return false;
        // check for numeric values
        for (String rule : rules) {
            if (!isNumeric(rule))
                return false;
        }
        return true;
    }

    public BigDecimal calculateRent(BigDecimal turnover) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal prevCap = BigDecimal.ZERO;
        BigDecimal cap = BigDecimal.ZERO;
        BigDecimal percentage;
        BigDecimal base;
        if (isValid() && turnover != null) {
            for (int i = 0; i < rules.length; i = i + 2) {
                base = BigDecimal.ZERO;
                if (i == rules.length - 1) {
                    // the last or single item
                    percentage = new BigDecimal(rules[i]).divide(BigDecimal.valueOf(100));
                    if (turnover.compareTo(prevCap) > 0)
                        base = turnover.subtract(prevCap);
                } else {
                    percentage = new BigDecimal(rules[i + 1]).divide(BigDecimal.valueOf(100));
                    cap = new BigDecimal(rules[i]);
                    if (turnover.compareTo(cap) > 0) {
                        base = cap.subtract(prevCap);
                    } else {
                        if (turnover.compareTo(prevCap) > 0)
                            base = turnover.subtract(prevCap);
                    }
                }
                total = total.add(base.multiply(percentage).setScale(2, RoundingMode.HALF_UP));
                prevCap = cap;
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional
                                                // '-' and decimal.
    }
}
