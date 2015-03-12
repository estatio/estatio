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

import javax.inject.Inject;
import com.google.common.base.Throwables;
import org.apache.log4j.PropertyConfigurator;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.isis.applib.fixtures.FixtureClock;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
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

    /**
     * Replacement for the deprecated {@link #runScript(org.apache.isis.applib.fixturescripts.FixtureScript...)}.
     */
    protected void runFixtureScript(final FixtureScript... fixtureScriptList) {
        if(fixtureScriptList.length == 1) {
            fixtureScripts.runFixtureScript(fixtureScriptList[0], null);
        } else {
            fixtureScripts.runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    for (FixtureScript fixtureScript : fixtureScriptList) {
                        executionContext.executeChild(this, fixtureScript);
                    }
                }
            }, null);
        }
        nextTransaction();
    }

    // //////////////////////////////////////

    protected FixtureClock getFixtureClock() {
        return ((FixtureClock)FixtureClock.getInstance());
    }

    // //////////////////////////////////////
    
    public Matcher<Throwable> causalChainHasMessageWith(final String messageFragment) {
        return new TypeSafeMatcher<Throwable>() {

            @Override
            public void describeTo(Description arg0) {
                arg0.appendText("causal chain has message with " + messageFragment);

            }

            @Override
            protected boolean matchesSafely(Throwable arg0) {
                for (Throwable ex : Throwables.getCausalChain(arg0)) {
                    if (ex.getMessage().contains(messageFragment)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Inject
    protected FixtureScripts fixtureScripts;

}

