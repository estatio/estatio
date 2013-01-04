package org.estatio.dom.utils;

import java.math.BigDecimal;
import java.math.MathContext;

public class MathUtils {
	
	private MathUtils() {}
	
    public static BigDecimal round(BigDecimal input, int precision) {
        MathContext mc = new MathContext(precision+1);
        return input.round(mc);
    }

}
