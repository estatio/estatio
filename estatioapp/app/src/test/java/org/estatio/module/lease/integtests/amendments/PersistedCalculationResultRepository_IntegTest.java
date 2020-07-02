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
package org.estatio.module.lease.integtests.amendments;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.invoice.dom.InvoicingInterval;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.amendments.PersistedCalculationResult;
import org.estatio.module.lease.dom.amendments.PersistedCalculationResultRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationService;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistedCalculationResultRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
            }
        });
    }

    @Test
    public void create_and_finder_works() throws Exception {

        // given
        final LeaseItem rentItem = LeaseItemForRent_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseTerm term = rentItem.getTerms().first();
        assertThat(term).isNotNull();
        InvoiceCalculationService.CalculationResult calculationResult = setupCalculationResult(term.getStartDate(), new BigDecimal("123.45"));
        assertThat(persistedCalculationResultRepository.listAll()).isEmpty();

        // when
        persistedCalculationResultRepository.create(calculationResult, term);

        // then
        assertThat(persistedCalculationResultRepository.listAll()).hasSize(1);
        final List<PersistedCalculationResult> resultsForTerm = persistedCalculationResultRepository.findByLeaseTerm(term);
        assertThat(resultsForTerm).hasSize(1);
        assertThat(resultsForTerm.get(0).getLeaseTerm()).isEqualTo(term);
    }

    @Test
    public void deleteIfAnyAndRecreate_works() throws Exception {

        // given
        final LeaseItem rentItem = LeaseItemForRent_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseTerm term = rentItem.getTerms().first();
        assertThat(term).isNotNull();
        InvoiceCalculationService.CalculationResult calculationResult = setupCalculationResult(term.getStartDate(), new BigDecimal("123.45"));
        final PersistedCalculationResult persistedCalculationResult = persistedCalculationResultRepository
                .create(calculationResult, term);
        assertThat(persistedCalculationResultRepository.findByLeaseTerm(term)).hasSize(1);
        assertThat(persistedCalculationResultRepository.findByLeaseTerm(term)).contains(persistedCalculationResult);

        // when
        InvoiceCalculationService.CalculationResult newResult1 = setupCalculationResult(term.getStartDate(), new BigDecimal("234.56"));
        InvoiceCalculationService.CalculationResult newResult2 = setupCalculationResult(term.getStartDate(), new BigDecimal("3.45"));
        List<InvoiceCalculationService.CalculationResult> newResultList = Arrays.asList(newResult1, newResult2);
        persistedCalculationResultRepository.deleteIfAnyAndRecreate(newResultList, term);

        // then
        final List<PersistedCalculationResult> newPCalcsForTerm = persistedCalculationResultRepository.findByLeaseTerm(term);
        assertThat(newPCalcsForTerm).hasSize(2);
        assertThat(newPCalcsForTerm).doesNotContain(persistedCalculationResult);
        assertThat(newPCalcsForTerm.get(0).getValue()).isEqualTo(new BigDecimal("234.56"));
        assertThat(newPCalcsForTerm.get(1).getValue()).isEqualTo(new BigDecimal("3.45"));

    }

    private InvoiceCalculationService.CalculationResult setupCalculationResult(final LocalDate date, final BigDecimal value){
        final LocalDate invoicingStartDate = date;
        final LocalDate invoicingEndDate = date.plusMonths(3);
        final LocalDate invoicingDueDate = date;
        LocalDateInterval intervalForInvoice = LocalDateInterval.including(invoicingStartDate, invoicingEndDate);
        InvoicingInterval invoicingInterval = new InvoicingInterval(intervalForInvoice, invoicingDueDate);

        final LocalDate effectiveStartDate = date;
        final LocalDate effectiveEndate = date.plusMonths(3);
        LocalDateInterval effectiveInterval = LocalDateInterval.including(effectiveStartDate, effectiveEndate);

        return new InvoiceCalculationService.CalculationResult(invoicingInterval, effectiveInterval, value);
    }

    @Inject
    PersistedCalculationResultRepository persistedCalculationResultRepository;
}