package org.estatio.module.capex.dom.util;

import org.estatio.module.base.dom.apptenancy.WithApplicationTenancy;

public class CountryUtil {

    public static boolean isItalian(WithApplicationTenancy object) {
        return object.getAtPath() != null && object.getAtPath().startsWith("/ITA");
    }

}
