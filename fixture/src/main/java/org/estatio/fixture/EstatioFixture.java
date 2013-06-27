package org.estatio.fixture;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.fixtures.AbstractFixture;

import org.estatio.fixture.agreement.AgreementTypesAndRoleTypesFixture;
import org.estatio.fixture.asset.PropertiesAndUnitsFixture;
import org.estatio.fixture.charge.ChargeFixture;
import org.estatio.fixture.charge.CurrencyFixture;
import org.estatio.fixture.geography.GeographyFixture;
import org.estatio.fixture.index.IndexFixture;
import org.estatio.fixture.invoice.InvoiceFixture;
import org.estatio.fixture.lease.LeasesFixture;
import org.estatio.fixture.party.PartiesFixture;
import org.estatio.fixture.tax.TaxFixture;


public class EstatioFixture extends AbstractFixture {

    public EstatioFixture() {
    }
    
    @Override
    public void install() {
        
        List<AbstractFixture> fixtures = Arrays.asList(
            newFixture(GeographyFixture.class),
            newFixture(AgreementTypesAndRoleTypesFixture.class),
            newFixture(TaxFixture.class),
            newFixture(CurrencyFixture.class),
            newFixture(ChargeFixture.class),
            newFixture(IndexFixture.class),
            newFixture(PartiesFixture.class),
            newFixture(PropertiesAndUnitsFixture.class),
            newFixture(LeasesFixture.class),
            newFixture(InvoiceFixture.class)
        );

        for (AbstractFixture fixture : fixtures) {
            fixture.install(); 
            getContainer().flush();
        }

    }

    private AbstractFixture newFixture(Class<? extends AbstractFixture> fixtureClass) {
        return getContainer().newTransientInstance(fixtureClass);
    }

}
