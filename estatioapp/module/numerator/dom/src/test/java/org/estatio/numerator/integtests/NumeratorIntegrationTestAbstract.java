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
package org.estatio.numerator.integtests;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.core.integtestsupport.scenarios.ScenarioExecutionForIntegration;
import org.apache.isis.core.security.authentication.AuthenticationRequestNameOnly;

import org.isisaddons.module.security.SecurityModule;

import org.incode.module.integtestsupport.dom.IncodeIntegrationTestAbstract;

import org.estatio.numerator.dom.NumeratorDomModule;
import org.estatio.numerator.dom.impl.NumeratorRepository;
import org.estatio.numerator.fixture.NumeratorFixtureModule;

/**
 * Base class for integration tests.
 */
public abstract class NumeratorIntegrationTestAbstract extends IncodeIntegrationTestAbstract {


    private static final Logger LOG = LoggerFactory.getLogger(NumeratorIntegrationTestAbstract.class);

    @BeforeClass
    public static void initClass() {
        PropertyConfigurator.configure("logging-integtest.properties");

        LOG.info("Starting tests");

        IsisSystemForTest isft = IsisSystemForTest.getElseNull();
        if (isft == null) {
            isft = new IsisSystemForTest.Builder()
                    .withLoggingAt(Level.INFO)
                    .with(new AuthenticationRequestNameOnly("estatio-admin"))
                    .with(new AppManifest() {

                        @Override public List<Class<?>> getModules() {
                            final List<Class<?>> modules = Lists.newArrayList();
                            modules.add(NumeratorDomModule.class);
                            modules.add(NumeratorFixtureModule.class);
                            modules.add(SecurityModule.class);
                            return modules;
                        }

                        @Override
                        public Map<String, String> getConfigurationProperties() {
                            final Map<String, String> props = Maps.newHashMap();
                            AppManifest.Util.withIsisIntegTestProperties(props);
                            AppManifest.Util.withJavaxJdoRunInMemoryProperties(props);
                            AppManifest.Util.withDataNucleusProperties(props);
                            return props;
                        }
                        @Override
                        public List<Class<?>> getAdditionalServices() {
                            return Lists.newArrayList(NumeratorRepository.class);
                        }

                        @Override
                        public String getAuthenticationMechanism() {
                            return null;
                        }

                        @Override public String getAuthorizationMechanism() {
                            return null;
                        }

                        @Override public List<Class<? extends FixtureScript>> getFixtures() {
                            return null;
                        }
                    })
                    .build()
                    .setUpSystem();
            IsisSystemForTest.set(isft);
        }

        // instantiating will install onto ThreadLocal
        new ScenarioExecutionForIntegration();
    }

    @Inject
    protected TransactionService transactionService;

    @Inject
    protected FixtureScripts fixtureScripts;

}

