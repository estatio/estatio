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
import org.apache.log4j.PropertyConfigurator;
import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.apache.isis.core.integtestsupport.IntegrationTestAbstract;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.core.integtestsupport.scenarios.ScenarioExecutionForIntegration;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EstatioIntegrationTestForMigration extends IntegrationTestAbstract {

    @BeforeClass
    public static void initClass() {
        PropertyConfigurator.configure("logging.properties");
        initIsft();

        // instantiating will install onto ThreadLocal
        new ScenarioExecutionForIntegration();
    }

    /**
     * Holds an instance of an {@link org.apache.isis.core.integtestsupport.IsisSystemForTest} as a {@link ThreadLocal} on
     * the current thread, initialized with Estatio's domain services and with
     * {@link org.estatio.fixture.EstatioBaseLineFixture reference data fixture}.
     */
    private static IsisSystemForTest initIsft() {
        IsisSystemForTest isft = IsisSystemForTest.getElseNull();
        if (isft == null) {
            isft = new EstatioIntegTestBuilderForMigration().build().setUpSystem();
            IsisSystemForTest.set(isft);
        }
        return isft;
    }


    public static BigInteger bi(int i) {
        return BigInteger.valueOf(i);
    }

    public static LocalDate dt(int yyyy, int mm, int dd) {
        return new LocalDate(yyyy,mm,dd);
    }

    public static BigDecimal bd(int val, int newScale) {
        return BigDecimal.valueOf(val).setScale(newScale);
    }

    public static BigDecimal bd(int val) {
        return BigDecimal.valueOf(val);
    }

    public static BigDecimal bd(String str) {
        return new BigDecimal(str);
    }

    public static BigDecimal bd(double d) {
        return new BigDecimal(d);
    }

    public static BigDecimal bd2(int val) {
        return bd(val, 2);
    }

    public static BigDecimal bd4(int val) {
        return bd(val, 4);
    }

}
