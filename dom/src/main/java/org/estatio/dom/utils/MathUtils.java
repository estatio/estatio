package org.estatio.dom.utils;

import java.math.BigDecimal;
import java.math.MathContext;

public class MathUtils {
	
	private MathUtils() {}
	
    public static BigDecimal round(BigDecimal input, int precision) {
        MathContext mc = new MathContext(precision+1);
        return input.round(mc);
    }

    public static boolean isZeroOrNull(BigDecimal input) {
        if (input == null) return true;
        if (input.compareTo(BigDecimal.ZERO) == 0) return true;
        return false;
    }

    public static boolean isNotZeroOrNull(BigDecimal input) {
        return !isZeroOrNull(input);
    }
}
