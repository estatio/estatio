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

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.core.integtestsupport.IntegrationTestAbstract;
import org.apache.isis.core.integtestsupport.scenarios.ScenarioExecutionForIntegration;

/**
 * Base class for integration tests.
 */
public abstract class EstatioIntegrationTest extends IntegrationTestAbstract {

    private static final Logger LOG = LoggerFactory.getLogger(EstatioIntegrationTest.class);

    @BeforeClass
    public static void initClass() {
        PropertyConfigurator.configure("logging.properties");

        LOG.info("Starting tests");

        EstatioSystemInitializer.initIsft();
        
        // instantiating will install onto ThreadLocal
        new ScenarioExecutionForIntegration();
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

    // //////////////////////////////////////

    protected static void runScript(FixtureScript... fixtureScripts) {
        scenarioExecution().install(fixtureScripts);
    }

}

