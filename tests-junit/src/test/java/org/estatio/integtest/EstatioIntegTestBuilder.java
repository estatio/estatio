package org.estatio.integtest;

import org.apache.log4j.Level;


import org.apache.isis.applib.fixtures.InstallableFixture;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusObjectStore;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusPersistenceMechanismInstaller;
import org.estatio.fixture.EstatioFixture;
import org.estatio.jdo.ChargesJdo;
import org.estatio.jdo.CommunicationChannelsJdo;
import org.estatio.jdo.CountriesJdo;
import org.estatio.jdo.IndicesJdo;
import org.estatio.jdo.InvoicesJdo;
import org.estatio.jdo.LeaseActorsJdo;
import org.estatio.jdo.LeaseItemsJdo;
import org.estatio.jdo.LeaseTermsJdo;
import org.estatio.jdo.LeaseUnitsJdo;
import org.estatio.jdo.LeasesJdo;
import org.estatio.jdo.PartiesJdo;
import org.estatio.jdo.PropertiesJdo;
import org.estatio.jdo.PropertyActorsJdo;
import org.estatio.jdo.StatesJdo;
import org.estatio.jdo.TaxesJdo;
import org.estatio.jdo.UnitsJdo;

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
                new PropertiesJdo(),
                new UnitsJdo(),
                new PropertyActorsJdo(),
                new PartiesJdo(),
                new LeasesJdo(),
                new LeaseItemsJdo(),
                new LeaseTermsJdo(),
                new LeaseActorsJdo(),
                new LeaseUnitsJdo(),
                new InvoicesJdo(),
                new CommunicationChannelsJdo(),
                new CountriesJdo(),
                new StatesJdo(),
                new IndicesJdo(),
                new TaxesJdo(),
                new ChargesJdo()
                );
    }

    private IsisConfiguration testConfiguration() {
        final IsisConfigurationDefault testConfiguration = new IsisConfigurationDefault();

        //testConfiguration.add("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionDriverName", "net.sf.log4jdbc.DriverSpy"); // use log4jdbc instead
        //testConfiguration.add("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL", "jdbc:hsqldb:mem:test"); //disable default sqlloq
        
        testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.defaultInheritanceStrategy", "TABLE_PER_CLASS");
        testConfiguration.add(DataNucleusObjectStore.INSTALL_FIXTURES_KEY , "true");
        
        return testConfiguration;
    }
}