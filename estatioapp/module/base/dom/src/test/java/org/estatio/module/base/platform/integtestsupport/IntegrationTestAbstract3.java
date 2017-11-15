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
package org.estatio.module.base.platform.integtestsupport;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.AppManifestAbstract;
import org.apache.isis.applib.NonRecoverableException;
import org.apache.isis.applib.RecoverableException;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.sessmgmt.SessionManagementService;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.objectstore.jdo.datanucleus.IsisConfigurationForJdoIntegTests;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.estatio.module.base.platform.applib.Module;

public abstract class IntegrationTestAbstract3<M extends Module>  {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestAbstract3.class);


    protected final M module;
    private final Class[] additionalModuleClasses;

    public IntegrationTestAbstract3(final M module, final Class... additionalModuleClasses) {
        this.module = module;
        this.additionalModuleClasses = additionalModuleClasses;
    }

    @Before
    public void bootstrapIfRequired() {
        final List<Module> transitiveDependencies = module.getTransitiveDependencies();
        final Class[] moduleTransitiveDependencies = asClasses(transitiveDependencies);

        final List<Class<?>> furtherDependencies = Lists.newArrayList();
        for (Module transitiveDependency : transitiveDependencies) {
            furtherDependencies.addAll(transitiveDependency.getDependenciesAsClass());
        }

        final AppManifestAbstract.Builder builder =
                AppManifestAbstract.Builder
                    .forModules(moduleTransitiveDependencies)
                    .withAdditionalModules(furtherDependencies)
                    .withAdditionalModules(additionalModuleClasses)                ;
        final AppManifest appManifest = builder.build();

        bootstrapUsing(appManifest);

        beginTransaction();

        setup();


    }

    /**
     * The {@link AppManifest} used to bootstrap the {@link IsisSystemForTest} (on the thread-local)
     */
    private static ThreadLocal<AppManifest> isftAppManifest = new ThreadLocal<>();

    protected static void bootstrapUsing(AppManifest appManifest) {
        PropertyConfigurator.configure("logging-integtest.properties");

        if (needToBootstrap(appManifest)) {
            final IsisSystemForTest.Builder isftBuilder =
                    new IsisSystemForTest.Builder()
                            .withLoggingAt(Level.INFO)
                            .with(appManifest)
                            .with(new IsisConfigurationForJdoIntegTests());

            IsisSystemForTest isft = isftBuilder.build();
            isft.setUpSystem();

            // save both the system and the manifest
            // used to bootstrap the system onto thread-loca
            IsisSystemForTest.set(isft);
            isftAppManifest.set(appManifest);
        }
    }

    private static boolean needToBootstrap(final AppManifest appManifest) {
        IsisSystemForTest isft = IsisSystemForTest.getElseNull();
        if (isft == null)
            return true;

        final AppManifest appManifestFromPreviously = isftAppManifest.get();
        return ! haveSameModules(appManifest, appManifestFromPreviously);
    }

    static boolean haveSameModules(
            final AppManifest m1,
            final AppManifest m2) {
        final List<Class<?>> m1Modules = m1.getModules();
        final List<Class<?>> m2Modules = m2.getModules();
        return m1Modules.containsAll(m2Modules) && m2Modules.containsAll(m1Modules);
    }

    /**
     * this is asymmetric - handles only the teardown of the transaction afterwards, not the initial set up
     * (which is done instead by the @Before, so that can also bootstrap system the very first time)
     */
    @Rule
    public IntegrationTestAbstract3.IsisTransactionRule isisTransactionRule = new IntegrationTestAbstract3.IsisTransactionRule();

    private static class IsisTransactionRule implements MethodRule {

        @Override
        public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {

            return new Statement() {
                @Override
                public void evaluate() throws Throwable {

                    // we don't set up the ISFT, because the very first time it won't be there.
                    // Instead we expect it to be bootstrapped via @Before
                    try {
                        base.evaluate();
                        final IsisSystemForTest isft = IsisSystemForTest.get();
                        isft.endTran();
                    } catch(final Throwable e) {
                        final IsisSystemForTest isft = IsisSystemForTest.getElseNull();
                        if(isft != null) {
                            isft.closeSession();
                            isft.openSession();
                        }
                        final List<Throwable> causalChain = Throwables.getCausalChain(e);
                        // if underlying cause is an applib-defined exception,
                        // throw that rather than Isis' wrapper exception
                        for (final Throwable cause : causalChain) {
                            if(cause instanceof RecoverableException ||
                                    cause instanceof NonRecoverableException) {
                                throw cause;
                            }
                        }
                        throw e;
                    }
                }
            };
        }
    }

    private void beginTransaction() {
        final IsisSystemForTest isft = IsisSystemForTest.get();

        isft.getContainer().injectServicesInto(this);
        isft.beginTran();
    }

    private void setup() {
        final List<Module> dependencies = module.getTransitiveDependencies();
        for (Module dependency : dependencies) {
            final FixtureScript setupFixture = dependency.getRefDataSetupFixture();
            if(setupFixture != null) {
                runFixtureScript(setupFixture);
            }
        }
        final FixtureScript fixtureScript = module.getRefDataSetupFixture();
        if(fixtureScript == null) {
            return;
        }
        runFixtureScript(fixtureScript);
    }

    @After
    public void tearDown() {
        final FixtureScript fixtureScript = module.getTeardownFixture();
        if(fixtureScript == null) {
            return;
        }
        runFixtureScript(fixtureScript);
    }


    protected void runFixtureScript(final FixtureScript... fixtureScriptList) {
        if (fixtureScriptList.length == 1) {
            this.fixtureScripts.runFixtureScript(fixtureScriptList[0], null);
        } else {
            this.fixtureScripts.runFixtureScript(new FixtureScript() {
                protected void execute(ExecutionContext executionContext) {
                    FixtureScript[] fixtureScripts = fixtureScriptList;
                    for (FixtureScript fixtureScript : fixtureScripts) {
                        executionContext.executeChild(this, fixtureScript);
                    }
                }
            }, null);
        }

        transactionService.nextTransaction();
    }


    private static Class[] asClasses(final List<Module> dependencies) {
        final List<? extends Class<? extends Module>> dependenciesAsClasses =
                dependencies.stream().map(Module::getClass)
                .collect(Collectors.toList());
        return dependenciesAsClasses.toArray(new Class[] {});
    }

    @Inject
    protected FakeDataService fakeDataService;
    @Inject
    protected FixtureScripts fixtureScripts;
    @Inject
    protected FactoryService factoryService;
    @Inject
    protected ServiceRegistry2 serviceRegistry;
    @Inject
    RepositoryService repositoryService;
    @Inject
    protected UserService userService;
    @Inject
    protected WrapperFactory wrapperFactory;
    @Inject
    protected TransactionService transactionService;
    @Inject
    protected SessionManagementService sessionManagementService;
}