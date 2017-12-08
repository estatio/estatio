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
package org.estatio.module.lease.fixtures.leaseitems.builders;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseTermForTax;
import org.estatio.module.lease.dom.LeaseTermFrequency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bd;

@EqualsAndHashCode(of={"leaseItem","startDate"}, callSuper = false)
@ToString(of={"leaseItem","startDate"})
@Accessors(chain = true)
public class LeaseTermForTaxBuilder
        extends BuilderScriptAbstract<LeaseTermForTax, LeaseTermForTaxBuilder> {

    @Getter @Setter LeaseItem leaseItem;
    @Getter @Setter LocalDate startDate;
    @Getter @Setter LocalDate endDate;
    @Getter @Setter LeaseTermFrequency leaseTermFrequency;
    @Getter @Setter BigDecimal taxPercentage;
    @Getter @Setter BigDecimal recoverablePercentage;
    @Getter @Setter Boolean taxable;


    @Getter LeaseTermForTax object;

    @AllArgsConstructor
    @Data
    public static class TermSpec {
        LocalDate startDate;
        LocalDate endDate;
        LeaseTermFrequency leaseTermFrequency;
        BigDecimal taxPercentage;
        BigDecimal recoverablePercentage;
        Boolean taxable;
    }

    @Override
    protected void execute(final ExecutionContext ec) {

        checkParam("leaseItem", ec, LeaseItem.class);
        defaultParam("startDate", ec, leaseItem.getStartDate());

        defaultParam("taxPercentage", ec, bd(fakeDataService.ints().between(1, 3)));
        defaultParam("recoverablePercentage", ec, bd(fakeDataService.ints().between(40, 60)));
        defaultParam("taxable", ec, fakeDataService.booleans().coinFlip());

        defaultParam("leaseTermFrequency", ec, LeaseTermFrequency.YEARLY);

        final Class<?> expectedClass = LeaseTermForTax.class;
        if(!leaseItem.getType().isCreate(expectedClass)) {
            throw new IllegalArgumentException(
                    String.format("LeaseItem type must instantiate %s (is %s)", expectedClass, leaseItem.getType()));
        }

        final LeaseTermForTax leaseTerm = (LeaseTermForTax) leaseItem.newTerm(startDate, endDate);

        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);

        leaseTerm.setTaxPercentage(taxPercentage);
        leaseTerm.setRecoverablePercentage(recoverablePercentage);
        leaseTerm.setInvoicingDisabled(taxable);

        ec.addResult(this, leaseTerm);

        object = leaseTerm;
    }

    @Inject
    FakeDataService fakeDataService;
}
