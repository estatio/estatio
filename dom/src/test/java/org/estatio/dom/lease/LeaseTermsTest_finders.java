package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class LeaseTermsTest_finders {

    private FinderInteraction finderInteraction;

    private LeaseTerms leaseTerms;

    private LeaseItem leaseItem;
    private BigInteger sequence;

    private LocalDate date;


    @Before
    public void setup() {

        leaseItem = new LeaseItem();
        sequence = BigInteger.TEN;
        
        date = new LocalDate(2013,4,1);
        
        leaseTerms = new LeaseTerms() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<LeaseTerm> allInstances() {
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
    public void findLeaseTermByLeaseItemAndSequence() {
        
        leaseTerms.findLeaseTermByLeaseItemAndSequence(leaseItem, sequence);
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(LeaseTerm.class));
        assertThat(finderInteraction.getQueryName(), is("findByLeaseItemAndSequence"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("leaseItem"), is((Object)leaseItem));
        assertThat(finderInteraction.getArgumentsByParameterName().get("sequence"), is((Object)sequence));
        
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
    }

    @Test
    public void leaseTermsToBeApproved() {
        
        leaseTerms.leaseTermsToBeApproved(date);
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(LeaseTerm.class));
        assertThat(finderInteraction.getQueryName(), is("findByStatusAndActiveDate"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("status"), is((Object)LeaseTermStatus.NEW));
        assertThat(finderInteraction.getArgumentsByParameterName().get("date"), is((Object)date));
        
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
    }
    

    @Test
    public void allInvoices() {
        
        leaseTerms.allLeaseTerms();
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
    }
    
}
