/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.integtests.lease;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForPercentage;
import org.estatio.module.lease.dom.LeaseTermForTurnoverRent;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;

public class LeaseTermsForPercentage_IntegTest extends EstatioIntegrationTest {

    @Inject
    LeaseRepository leaseRepository;

    public static class LeaseTermForPercentageOver2011Test extends LeaseTermsForPercentage_IntegTest {

        LeaseTermForIndexable indexTerm1;
        LeaseTermForIndexable indexTerm2;
        LeaseTermForTurnoverRent torTerm;
        LeaseTermForPercentage percentageTerm;
        Lease topmodelLease;

        @Before
        public void setUp() throws Exception {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
                }
            });
        }

        @Test
        public void test() throws Exception {
            // given
            topmodelLease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            topmodelLease.verifyUntil(new LocalDate(2012, 01, 01));

            indexTerm1 = (LeaseTermForIndexable) topmodelLease.findFirstItemOfType(LeaseItemType.RENT).getTerms().first();
            indexTerm2 = (LeaseTermForIndexable) indexTerm1.getNext();

            torTerm = (LeaseTermForTurnoverRent) topmodelLease.findFirstItemOfType(LeaseItemType.TURNOVER_RENT).getTerms().first();
            torTerm.setAuditedTurnover(BigDecimal.valueOf(1111111.00));
            topmodelLease.verifyUntil(new LocalDate(2012, 01, 01));

            // when
            Assertions.assertThat(indexTerm1.valueForDate(new LocalDate(2011, 07, 14))).isEqualTo(new BigDecimal("20200.00"));
            Assertions.assertThat(indexTerm1.getStartDate()).isEqualTo(new LocalDate(2010, 07, 15));
            Assertions.assertThat(indexTerm2.valueForDate(new LocalDate(2012, 07, 14))).isEqualTo(new BigDecimal("20846.40"));
            Assertions.assertThat(indexTerm2.getStartDate()).isEqualTo(new LocalDate(2011, 07, 15));
            Assertions.assertThat(torTerm.valueForDate(new LocalDate(2012, 01, 01))).isEqualTo(new BigDecimal("57279.16"));
            percentageTerm = (LeaseTermForPercentage) topmodelLease.findFirstItemOfType(LeaseItemType.RENTAL_FEE).getTerms().first();

            // then
            Assertions.assertThat(percentageTerm.valueForDate(new LocalDate(2012, 01, 01))).isEqualTo(new BigDecimal("1166.67"));
        }

    }

    public static class LeaseTermForPercentageNoTurnOverRent extends LeaseTermsForPercentage_IntegTest {

        LeaseTermForIndexable indexTermLast;
        LeaseTermForIndexable indexTermPrevious;
        LeaseTermForPercentage percentageTermLast;
        Lease topmodelLease;

        @Before
        public void setUp() throws Exception {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
                }
            });
        }

        @Test
        public void test() throws Exception {
            // given
            topmodelLease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            topmodelLease.verifyUntil(new LocalDate(2016, 01, 01));

            indexTermLast = (LeaseTermForIndexable) topmodelLease.findFirstItemOfType(LeaseItemType.RENT).getTerms().last();
            indexTermPrevious = (LeaseTermForIndexable) indexTermLast.getPrevious();

            // when
            Assertions.assertThat(indexTermLast.valueForDate(new LocalDate(2015, 07, 15))).isEqualTo(new BigDecimal("21305.02"));
            Assertions.assertThat(indexTermLast.getStartDate()).isEqualTo(new LocalDate(2015, 07, 15));
            Assertions.assertThat(indexTermPrevious.valueForDate(new LocalDate(2014, 07, 14))).isEqualTo(new BigDecimal("21305.02"));
            Assertions.assertThat(indexTermPrevious.getStartDate()).isEqualTo(new LocalDate(2014, 07, 15));
            percentageTermLast = (LeaseTermForPercentage) topmodelLease.findFirstItemOfType(LeaseItemType.RENTAL_FEE).getTerms().last();

            // then
            Assertions.assertThat(percentageTermLast.valueForDate(new LocalDate(2016, 01, 01))).isEqualTo(new BigDecimal("319.58"));
            Assertions.assertThat(percentageTermLast.getStartDate()).isEqualTo(new LocalDate(2015, 01, 01));
            Assertions.assertThat(percentageTermLast.getEndDate()).isEqualTo(new LocalDate(2015, 12, 31));
            Assertions.assertThat(percentageTermLast.getOriginalValue()).isEqualTo(new BigDecimal("21305.04"));
        }

    }

}