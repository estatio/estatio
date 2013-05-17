package org.estatio.integtest;

import org.apache.log4j.Level;
import org.estatio.api.Api;
import org.estatio.appsettings.EstatioSettingsService;
import org.estatio.dom.invoice.InvoiceCalculationService;
import org.estatio.fixture.EstatioFixture;
import org.estatio.jdo.AgreementRolesJdo;
import org.estatio.jdo.AgreementsJdo;
import org.estatio.jdo.ChargeGroupsJdo;
import org.estatio.jdo.ChargesJdo;
import org.estatio.jdo.CommunicationChannelsJdo;
import org.estatio.jdo.CountriesJdo;
import org.estatio.jdo.CurrenciesJdo;
import org.estatio.jdo.FinancialAccountsJdo;
import org.estatio.jdo.FixedAssetRolesJdo;
import org.estatio.jdo.IndicesJdo;
import org.estatio.jdo.InvoicesJdo;
import org.estatio.jdo.LeaseItemsJdo;
import org.estatio.jdo.LeaseTermsJdo;
import org.estatio.jdo.LeaseUnitReferencesJdo;
import org.estatio.jdo.LeaseUnitsJdo;
import org.estatio.jdo.LeasesJdo;
import org.estatio.jdo.NumeratorsJdo;
import org.estatio.jdo.PartiesJdo;
import org.estatio.jdo.PropertiesJdo;
import org.estatio.jdo.StatesJdo;
import org.estatio.jdo.TaxesJdo;
import org.estatio.jdo.UnitsJdo;

import org.apache.isis.applib.fixtures.InstallableFixture;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusObjectStore;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusPersistenceMechanismInstaller;
import org.apache.isis.objectstore.jdo.datanucleus.service.support.IsisJdoSupportImpl;
import org.apache.isis.objectstore.jdo.service.RegisterEntities;

public class EstatioIntegTestBuilder extends IsisSystemForTest.Builder {

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
                new CountriesJdo(),
                new StatesJdo(),
                new CurrenciesJdo(),
                new IndicesJdo(),
                new PropertiesJdo(),
                new FixedAssetRolesJdo(),
                new UnitsJdo(),
                new PartiesJdo(),
                new AgreementsJdo(),
                new AgreementRolesJdo(),
                new LeasesJdo(),
                new LeaseTermsJdo(),
                new LeaseItemsJdo(),
                new LeaseUnitsJdo(),
                new LeaseUnitReferencesJdo(),
                new InvoicesJdo(),
                new CommunicationChannelsJdo(),
                new TaxesJdo(),
                new ChargesJdo(),
                new ChargeGroupsJdo(),
                new FinancialAccountsJdo(),
                new NumeratorsJdo(),
                new Api(),
                new IsisJdoSupportImpl(),
                new InvoiceCalculationService(),
                new EstatioSettingsService()
                );
    }

    private IsisConfiguration testConfiguration() {
        final IsisConfigurationDefault testConfiguration = new IsisConfigurationDefault();

        testConfiguration.add("isis.persistor.datanucleus.RegisterEntities.packagePrefix", "org.estatio.dom");
        //testConfiguration.add("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionDriverName", "net.sf.log4jdbc.DriverSpy"); // use log4jdbc instead
        testConfiguration.add("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL", "jdbc:hsqldb:mem:test;sqllog=3"); //disable default sqlloq
        
        testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.defaultInheritanceStrategy", "TABLE_PER_CLASS");
        testConfiguration.add(DataNucleusObjectStore.INSTALL_FIXTURES_KEY , "true");
        
        testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.cache.level2.type","none");
        // TODO: this is a (temporary?) work-around for NumeratorIntegrationTest failing if do a find prior to create and then a find; 
        // believe that the second find fails to work due to original find caching an incorrect query compilation plan
        testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.query.compilation.cached","false");

        
        return testConfiguration;
    }
}