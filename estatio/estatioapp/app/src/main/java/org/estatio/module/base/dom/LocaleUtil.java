package org.estatio.module.base.dom;

import java.util.Locale;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LocaleUtil {

    public static Locale deriveLocale(final String atPath) {
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
}
