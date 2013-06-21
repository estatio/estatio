package org.estatio.integtest.testing;

import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.fixtures.InstallableFixture;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import org.apache.isis.core.wrapper.WrapperFactoryDefault;
import org.apache.isis.objectstore.jdo.applib.service.settings.ApplicationSettingsServiceJdo;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusObjectStore;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusPersistenceMechanismInstaller;
import org.apache.isis.objectstore.jdo.datanucleus.service.support.IsisJdoSupportImpl;
import org.apache.isis.objectstore.jdo.service.RegisterEntities;

import org.estatio.api.Api;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.agreement.Agreements;
import org.estatio.dom.asset.FixedAssetRoles;
import org.estatio.dom.asset.FixedAssets;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Units;
import org.estatio.dom.charge.ChargeGroups;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.currency.Currencies;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.financial.contributed.FinancialAccountContributedActions;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.States;
import org.estatio.dom.index.IndexBases;
import org.estatio.dom.index.IndexValues;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.LeaseItems;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.LeaseUnits;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.UnitsForLease;
import org.estatio.dom.lease.contributed.LeaseTermContributedActions;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.dom.numerator.Numerators;
import org.estatio.dom.party.Parties;
import org.estatio.dom.tag.Tags;
import org.estatio.dom.tax.TaxRates;
import org.estatio.dom.tax.Taxes;
import org.estatio.fixture.EstatioFixture;
import org.estatio.services.appsettings.EstatioSettingsService;
import org.estatio.services.appsettings.EstatioSettingsServiceJdo;
import org.estatio.services.bookmarks.EstatioBookmarkService;
import org.estatio.services.clock.ClockService;

public abstract class AbstractEstatioIntegrationTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Rule
    public BootstrapIsisRule bootstrapIsis = new BootstrapIsisRule();

    @Rule
    public ExpectedException expectedExceptions = ExpectedException.none();

    /**
     * Same running system returned for all tests, set up with
     * {@link ToDoItemsFixture}.
     * 
     * <p>
     * The database is NOT reset between tests.
     */
    public IsisSystemForTest getIsft() {
        return bootstrapIsis.getIsisSystemForTest();
    }

    protected WrapperFactory wrapperFactory;
    protected DomainObjectContainer container;

    protected Api api;

    protected Countries countries;
    protected States states;

    protected Charges charges;

    protected Numerators numerators;

    protected AgreementRoleTypes agreementRoleTypes;

    protected FixedAssets fixedAssets;
    protected Properties properties;
    protected FixedAssetRoles actors;
    protected Units units;

    protected FinancialAccounts financialAccounts;

    protected Parties parties;

    protected Leases leases;
    protected LeaseTerms leaseTerms;
    protected Invoices invoices;
    protected InvoiceItemsForLease invoiceItemsForLease;

    protected Tags tags;

    protected EstatioSettingsService settings;

    @Before
    public void init() {
        wrapperFactory = getIsft().getService(WrapperFactoryDefault.class);
        container = getIsft().container;

        api = getIsft().getService(Api.class);

        charges = getIsft().getService(Charges.class);

        countries = getIsft().getService(Countries.class);
        states = getIsft().getService(States.class);

        numerators = getIsft().getService(Numerators.class);

        agreementRoleTypes = getIsft().getService(AgreementRoleTypes.class);

        fixedAssets = getIsft().getService(FixedAssets.class);
        properties = getIsft().getService(Properties.class);
        actors = getIsft().getService(FixedAssetRoles.class);
        units = getIsft().getService(Units.class);

        financialAccounts = getIsft().getService(FinancialAccounts.class);

        parties = getIsft().getService(Parties.class);

        leases = getIsft().getService(Leases.class);
        leaseTerms = getIsft().getService(LeaseTerms.class);
        invoices = getIsft().getService(Invoices.class);
        invoiceItemsForLease = getIsft().getService(InvoiceItemsForLease.class);

        tags = getIsft().getService(Tags.class);

        settings = getIsft().getService(EstatioSettingsService.class);
    }

    protected <T> T wrap(T obj) {
        return wrapperFactory.wrap(obj);
    }

    protected <T> T unwrap(T obj) {
        return wrapperFactory.unwrap(obj);
    }

    // //////////////////////////////////////////////////////////////
    // Boilerplate
    // //////////////////////////////////////////////////////////////

    static InstallableFixture newFixture() {
        return new EstatioFixture();
    };

    @BeforeClass
    public static void initClass() {
        PropertyConfigurator.configure("logging.properties");
    }

    private static class BootstrapIsisRule implements MethodRule {

        private static ThreadLocal<IsisSystemForTest> ISFT = new ThreadLocal<IsisSystemForTest>() {
            @Override
            protected IsisSystemForTest initialValue() {
                final IsisSystemForTest isft = EstatioIntegTestBuilder.builderWith(newFixture()).build().setUpSystem();
                return isft;
            }

        };

        public IsisSystemForTest getIsisSystemForTest() {
            return ISFT.get();
        }

        @Override
        public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
            final IsisSystemForTest isft = getIsisSystemForTest(); // creates
                                                                   // and starts
                                                                   // running if
                                                                   // required
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    isft.beginTran();
                    base.evaluate();
                    // if an exception is thrown by any test, then we don't
                    // attempt to cleanup (eg by calling bounceSystem)#
                    // because - in any case - we only ever install the fixtures
                    // once for ALL of the tests.
                    // therefore, just fix the first test that fails and don't
                    // worry about any other test failures beyond that
                    // (fix them up one by one)
                    isft.commitTran();
                }
            };
        }

    }

    private static class EstatioIntegTestBuilder extends IsisSystemForTest.Builder {

        @SuppressWarnings("unused")
        public static EstatioIntegTestBuilder builder() {
            return builderWith(new EstatioFixture());
        }

        public static EstatioIntegTestBuilder builderWith(InstallableFixture... fixtures) {
            EstatioIntegTestBuilder builder = new EstatioIntegTestBuilder();
            builder.withFixtures(fixtures);
            builder.withLoggingAt(Level.INFO);
            return builder;
        }

        private EstatioIntegTestBuilder() {
            with(testConfiguration());
            with(new DataNucleusPersistenceMechanismInstaller());
            withServices(
                    new RegisterEntities(), 
                    new WrapperFactoryDefault(), 
                    new Countries(), new States(), new Currencies(), new Indices(), new IndexBases(), new IndexValues(), new FixedAssets(), new Properties(), new FixedAssetRoles(), new UnitsForLease(), new Parties(), new Agreements(), new AgreementTypes(),
                    new AgreementRoleTypes(), new AgreementRoles(), new Leases(), new LeaseTerms(), new LeaseItems(), new LeaseUnits(), new Invoices(), new InvoiceItemsForLease(), new CommunicationChannels(), new Taxes(), new TaxRates(), new Tags(), new EstatioBookmarkService(), new Charges(),
                    new ChargeGroups(), new FinancialAccounts(), new Numerators(), new ClockService(), new Api(), new IsisJdoSupportImpl(), new InvoiceCalculationService(), new ApplicationSettingsServiceJdo(), new EstatioSettingsServiceJdo(), new FinancialAccountContributedActions(),
                    new LeaseTermContributedActions());
        }

        private IsisConfiguration testConfiguration() {
            final IsisConfigurationDefault testConfiguration = new IsisConfigurationDefault();

            testConfiguration.add("isis.persistor.datanucleus.RegisterEntities.packagePrefix", "org.estatio.dom");
            // testConfiguration.add("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionDriverName",
            // "net.sf.log4jdbc.DriverSpy"); // use log4jdbc instead
            testConfiguration.add("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL", "jdbc:hsqldb:mem:test;sqllog=3"); // disable
                                                                                                                                      // default
                                                                                                                                      // sqlloq

            testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.defaultInheritanceStrategy", "TABLE_PER_CLASS");
            testConfiguration.add(DataNucleusObjectStore.INSTALL_FIXTURES_KEY, "true");

            testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.cache.level2.type", "none");
            // TODO: this is a (temporary?) work-around for
            // NumeratorIntegrationTest failing if do a find prior to create and
            // then a find;
            // believe that the second find fails to work due to original find
            // caching an incorrect query compilation plan
            testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.query.compilation.cached", "false");

            // adding this is meant to be all that is required for
            // across-the-board multi-tenancy support
            // however, it causes DN to throw a NullPointerException...
            // testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.tenantId","DEV1");

            return testConfiguration;
        }
    }

}
