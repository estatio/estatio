/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

import org.estatio.fixture.EstatioFixture;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;

class IntegrationSystemForTestRule implements MethodRule {

    private static ThreadLocal<IsisSystemForTest> ISFT = new ThreadLocal<IsisSystemForTest>() {
        @Override
        protected IsisSystemForTest initialValue() {
            final IsisSystemForTest isft = EstatioIntegTestBuilder.builderWith(new EstatioFixture()).build().setUpSystem();
            return isft;
        };
    };

    public IsisSystemForTest getIsisSystemForTest() {
        return ISFT.get();
    }

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        final IsisSystemForTest isft = getIsisSystemForTest(); // creates and starts running if required
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                isft.beginTran();
                base.evaluate();
                // if an exception is thrown by any test, then we don't attempt to cleanup (eg by calling bounceSystem)#
                // because - in any case - we only ever install the fixtures once for ALL of the tests.
                // therefore, just fix the first test that fails and don't worry about any other test failures beyond that
                // (fix them up one by one)
                isft.commitTran();
            }
        };
    }

}
