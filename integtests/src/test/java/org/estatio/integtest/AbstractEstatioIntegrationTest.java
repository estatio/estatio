/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.integtest;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.integtest.specs.EstatioApp;

public abstract class AbstractEstatioIntegrationTest {

    protected static EstatioApp app;
    
    @BeforeClass
    public static void initClass() {
        PropertyConfigurator.configure("logging.properties");
        EstatioSystemOnThread.getIsft();
    }

    @BeforeClass
    public static void init() {
        app = new EstatioApp();
    }
    
    // //////////////////////////////////////

    /**
     * The order is important; this rule is outermost, and must - at a minimum - come before
     * the {@link #expectedExceptions} rule.
     */
    @Rule
    public IsisTransactionRule isisTransactionRule = new IsisTransactionRule();

    private static class IsisTransactionRule implements MethodRule  {

        @Override
        public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
            final IsisSystemForTest isft = EstatioSystemOnThread.getIsft(); 
            
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    isft.beginTran();
                    try {
                        base.evaluate();
                        isft.commitTran();
                    } catch(Throwable e) {
                        isft.bounceSystem();
                        throw e;
                    }
                }
            };
        }
    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Rule
    public ExpectedException expectedExceptions = ExpectedException.none();


    // //////////////////////////////////////



    // //////////////////////////////////////

    protected <T> T wrap(T obj) {
        return app.wrapperFactory.wrap(obj);
    }

    protected <T> T unwrap(T obj) {
        return app.wrapperFactory.unwrap(obj);
    }

    // //////////////////////////////////////


}

