/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;

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
