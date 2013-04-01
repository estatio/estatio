package org.estatio.fixture.charge;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.currency.Currencies;
import org.estatio.dom.currency.Currency;

public class CurrencyFixture extends AbstractFixture {

    @Override
    public void install() {
        createCurrency("EUR", "Euro");
    }

    private void createCurrency(String reference, String despription) {
        Currency currency = currencies.newCurrency(reference);
        currency.setDescription(despription);
    }

    private Currencies currencies;

    public void setCurrencies(Currencies currencies) {
        this.currencies = currencies;
    }

}
