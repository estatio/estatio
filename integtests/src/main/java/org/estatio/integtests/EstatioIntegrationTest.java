/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.integtests;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.apache.log4j.PropertyConfigurator;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.apache.isis.core.integtestsupport.IntegrationTestAbstract;
import org.apache.isis.core.integtestsupport.scenarios.ScenarioExecutionForIntegration;

/**
 * Base class for integration tests.
 */
public abstract class EstatioIntegrationTest extends IntegrationTestAbstract {

    @BeforeClass
    public static void initClass() {
        PropertyConfigurator.configure("logging.properties");
        EstatioSystemInitializer.initIsft();
        
        // instantiating will install onto ThreadLocal
        new ScenarioExecutionForIntegration();
    }

    // //////////////////////////////////////

    public static LocalDateInterval ival(String intervalStr) {
        return intervalStr != null? LocalDateInterval.parseString(intervalStr) : null;
    }

    // //////////////////////////////////////

    /**
     *
     * @param dateStr - in form "yyyy-MM-dd"
     */
    public static LocalDate dt(String dateStr) {
        return dateStr != null? LocalDate.parse(dateStr) : null;
    }

    public static LocalDate dt(int yyyy, int mm, int dd) {
        return new LocalDate(yyyy,mm,dd);
    }

    // //////////////////////////////////////

    public static BigInteger bi(int val) {
        return BigInteger.valueOf(val);
    }
    public static BigInteger bi(Integer val) {
        return val != null ? bi(val): null;
    }

    // //////////////////////////////////////

    public static BigDecimal bd(String str) {
        return new BigDecimal(str);
    }

    public static BigDecimal bd(double val) {
        return BigDecimal.valueOf(val);
    }
    public static BigDecimal bd(double v, int scale) {
        return bd(v).setScale(scale);
    }
    public static BigDecimal bd(double v, int scale, RoundingMode roundingMode) {
        return bd(v).setScale(scale, roundingMode);
    }
    public static BigDecimal bd2(double v) {
        return bd(v, 2);
    }
    public static BigDecimal bd2hup(double v) {
        return bd(v,2, RoundingMode.HALF_UP);
    }
    public static BigDecimal bd4(double v) {
        return bd(v,4);
    }


    public static BigDecimal bd(Double val) {
        return val != null? bd(val.doubleValue()): null;
    }
    public static BigDecimal bd(Double val, int scale) {
        return val != null? bd(val.doubleValue(), scale): null;
    }
    public static BigDecimal bd(Double val, int scale, RoundingMode roundingMode) {
        return val != null? bd(val.doubleValue(), scale, roundingMode): null;
    }
    public static BigDecimal bd2(Double val) {
        return val != null? bd2(val.doubleValue()): null;
    }
    public static BigDecimal bd2hup(Double val) {
        return val != null? bd2hup(val.doubleValue()): null;
    }
    public static BigDecimal bd4(Double val) {
        return val != null? bd4(val.doubleValue()): null;
    }


    public static BigDecimal bd(int val) {
        return BigDecimal.valueOf(val);
    }
    public static BigDecimal bd(int val, int newScale) {
        return bd(val).setScale(newScale);
    }
    public static BigDecimal bd1(int val) {
        return bd(val, 1);
    }
    public static BigDecimal bd2(int val) {
        return bd(val, 2);
    }
    public static BigDecimal bd4(int val) {
        return bd(val, 4);
    }


    public static BigDecimal bd(Integer val) {
        return val != null? bd(val.intValue()): null;
    }
    public static BigDecimal bd(Integer val, int newScale) {
        return val != null? bd(val.intValue(), newScale): null;
    }
    public static BigDecimal bd1(Integer val) {
        return val != null? bd1(val.intValue()): null;
    }
    public static BigDecimal bd2(Integer val) {
        return val != null? bd2(val.intValue()): null;
    }
    public static BigDecimal bd4(Integer val) {
        return val != null? bd4(val.intValue()): null;
    }


    // //////////////////////////////////////

    protected static <T> T assertType(Object o, Class<T> type) {
        if(o == null) {
            throw new AssertionError("Object is null");
        }
        if(!type.isAssignableFrom(o.getClass())) {
            throw new AssertionError(
                    String.format("Object %s (%s) is not an instance of %s", o.getClass().getName(), o.toString(), type));
        }
        return type.cast(o);
    }

}

