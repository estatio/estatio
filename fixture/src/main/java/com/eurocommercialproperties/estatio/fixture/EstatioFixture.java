package com.eurocommercialproperties.estatio.fixture;

import org.apache.isis.applib.fixtures.AbstractFixture;

import com.eurocommercialproperties.estatio.fixture.asset.PropertiesAndUnitsFixture;
import com.eurocommercialproperties.estatio.fixture.geography.GeographyFixture;
import com.eurocommercialproperties.estatio.fixture.index.IndexFixture;
import com.eurocommercialproperties.estatio.fixture.lease.LeasesFixture;
import com.eurocommercialproperties.estatio.fixture.party.PartiesFixture;
import com.eurocommercialproperties.estatio.fixture.tax.TaxFixture;

public class EstatioFixture extends AbstractFixture {

    public EstatioFixture() {
        addFixture(new GeographyFixture());
        addFixture(new IndexFixture());
        addFixture(new PartiesFixture());
        addFixture(new PropertiesAndUnitsFixture());
        addFixture(new LeasesFixture());
        addFixture(new TaxFixture());
    }

}
