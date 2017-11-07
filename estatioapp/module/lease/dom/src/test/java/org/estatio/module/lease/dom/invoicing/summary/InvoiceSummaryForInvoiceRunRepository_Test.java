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
package org.estatio.module.lease.dom.invoicing.summary;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceSummaryForInvoiceRunRepository_Test {

    FinderInteraction finderInteraction;

    InvoiceSummaryForInvoiceRunRepository invoiceSummaryForInvoiceRunRepository;


    @Before
    public void setup() {

        invoiceSummaryForInvoiceRunRepository = new InvoiceSummaryForInvoiceRunRepository() {
            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<InvoiceSummaryForInvoiceRun> allInstances() {
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

    public static class FindByRunId extends InvoiceSummaryForInvoiceRunRepository_Test {

        @Test
        public void happyCase() {

            String runId = new String();

            invoiceSummaryForInvoiceRunRepository.findByRunId(runId);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction.getResultType()).isEqualTo(InvoiceSummaryForInvoiceRun.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByRunId");
            assertThat(finderInteraction.getArgumentsByParameterName().get("runId")).isEqualTo((Object) runId);
            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(1);

        }

    }
}
