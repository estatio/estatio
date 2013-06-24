package org.estatio.dom.lease;

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

public class LeasesTest_finders {

    private FinderInteraction finderInteraction;

    private Leases leases;

    private FixedAsset asset;

    private LocalDate date;

    @Before
    public void setup() {

        asset = new FixedAssetForTesting();
        
        leases = new Leases() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<Lease> allInstances() {
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

        leases.findLeaseByReference("*REF?1*");
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Lease.class));
        assertThat(finderInteraction.getQueryName(), is("findByReference"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object)".*REF.1.*"));

        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }
    
    @Test
    public void findLeasesByReference() {
        
        leases.findLeasesByReference("*REF?1*");
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Lease.class));
        assertThat(finderInteraction.getQueryName(), is("findByReference"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object)".*REF.1.*"));
        
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }
    
    @Test
    public void findLeasesByAssetAndDate() {
        
        leases.findLeasesByAsset(asset, date);
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Lease.class));
        assertThat(finderInteraction.getQueryName(), is("findByAssetAndActiveOnDate"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("asset"), is((Object)asset));
        assertThat(finderInteraction.getArgumentsByParameterName().get("date"), is((Object)date));
        
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
    }
    

    @Test
    public void allInvoices() {
        
        leases.allLeases();
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
    }
    
}
