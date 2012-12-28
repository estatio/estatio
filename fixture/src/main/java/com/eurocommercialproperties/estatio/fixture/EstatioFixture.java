package com.eurocommercialproperties.estatio.fixture;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.fixtures.AbstractFixture;

import com.eurocommercialproperties.estatio.fixture.asset.PropertiesAndUnitsFixture;
import com.eurocommercialproperties.estatio.fixture.geography.GeographyFixture;
import com.eurocommercialproperties.estatio.fixture.index.IndexFixture;
import com.eurocommercialproperties.estatio.fixture.lease.LeasesFixture;
import com.eurocommercialproperties.estatio.fixture.party.PartiesFixture;
import com.eurocommercialproperties.estatio.fixture.tax.TaxFixture;

public class EstatioFixture extends AbstractFixture {

    public EstatioFixture() {
    }
    
    @Override
    public void install() {
        
        List<AbstractFixture> fixtures = Arrays.asList(
        getContainer().newTransientInstance(GeographyFixture.class),
        getContainer().newTransientInstance(IndexFixture.class),
        getContainer().newTransientInstance(PartiesFixture.class),
        getContainer().newTransientInstance(PropertiesAndUnitsFixture.class),
        getContainer().newTransientInstance(LeasesFixture.class),
        getContainer().newTransientInstance(TaxFixture.class)
        );
        
        for (AbstractFixture fixture : fixtures) {
            fixture.install(); 
            getContainer().flush();
        }
        
    }

}
