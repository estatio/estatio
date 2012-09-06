package com.eurocommercialproperties.estatio.fixture;

import org.apache.isis.applib.fixtures.AbstractFixture;

import com.eurocommercialproperties.estatio.fixture.asset.PropertiesAndUnitsFixture;
import com.eurocommercialproperties.estatio.fixture.geography.GeographyFixture;
import com.eurocommercialproperties.estatio.fixture.lease.LeasesFixture;
import com.eurocommercialproperties.estatio.fixture.party.PartiesFixture;

public class EstatioRefDataFixture extends AbstractFixture {

    public EstatioRefDataFixture() {
        addFixture(new GeographyFixture());
        addFixture(new PartiesFixture());
        addFixture(new PropertiesAndUnitsFixture());
        addFixture(new LeasesFixture());
    }

}
