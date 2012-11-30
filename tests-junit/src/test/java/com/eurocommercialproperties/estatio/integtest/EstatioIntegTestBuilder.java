package com.eurocommercialproperties.estatio.integtest;

import org.apache.log4j.Level;

import com.eurocommercialproperties.estatio.fixture.EstatioFixture;
import com.eurocommercialproperties.estatio.jdo.ChargesJdo;
import com.eurocommercialproperties.estatio.jdo.CommunicationChannelsJdo;
import com.eurocommercialproperties.estatio.jdo.CountriesJdo;
import com.eurocommercialproperties.estatio.jdo.IndicesJdo;
import com.eurocommercialproperties.estatio.jdo.InvoicesJdo;
import com.eurocommercialproperties.estatio.jdo.LeaseActorsJdo;
import com.eurocommercialproperties.estatio.jdo.LeaseItemsJdo;
import com.eurocommercialproperties.estatio.jdo.LeaseTermsJdo;
import com.eurocommercialproperties.estatio.jdo.LeaseUnitsJdo;
import com.eurocommercialproperties.estatio.jdo.LeasesJdo;
import com.eurocommercialproperties.estatio.jdo.PartiesJdo;
import com.eurocommercialproperties.estatio.jdo.PropertiesJdo;
import com.eurocommercialproperties.estatio.jdo.PropertyActorsJdo;
import com.eurocommercialproperties.estatio.jdo.StatesJdo;
import com.eurocommercialproperties.estatio.jdo.TaxesJdo;
import com.eurocommercialproperties.estatio.jdo.UnitsJdo;

import org.apache.isis.applib.fixtures.InstallableFixture;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.runtimes.dflt.objectstores.jdo.datanucleus.DataNucleusObjectStore;
import org.apache.isis.runtimes.dflt.objectstores.jdo.datanucleus.DataNucleusPersistenceMechanismInstaller;
import org.apache.isis.runtimes.dflt.testsupport.IsisSystemForTest;

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
        testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.defaultInheritanceStrategy", "TABLE_PER_CLASS");
        testConfiguration.add(DataNucleusObjectStore.INSTALL_FIXTURES_KEY , "true");
        return testConfiguration;
    }
}