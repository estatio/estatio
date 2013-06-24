package org.estatio.dom.tax;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetForTesting;

public class TaxRatesTest_finders {

    private FinderInteraction finderInteraction;

    private TaxRates taxRates;

    private Tax tax;
    private LocalDate date;

    @Before
    public void setup() {

        tax = new Tax();
        date = new LocalDate(2013,4,1);
        
        taxRates = new TaxRates() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<TaxRate> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }
            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    @Test
    public void findLeaseByReference() {

        taxRates.findTaxRateByTaxAndDate(tax, date);
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(TaxRate.class));
        assertThat(finderInteraction.getQueryName(), is("findByTaxAndDate"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("tax"), is((Object)tax));
        assertThat(finderInteraction.getArgumentsByParameterName().get("date"), is((Object)date));

        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
    }
    

    @Test
    public void allTaxRates() {
        
        taxRates.allTaxRates();
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
    }
    
}
