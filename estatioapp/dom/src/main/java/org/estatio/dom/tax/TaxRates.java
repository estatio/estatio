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
package org.estatio.dom.tax;

import java.math.BigDecimal;
import java.util.List;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.EstatioDomainService;

@DomainService(repositoryFor = TaxRate.class)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.8"
)
public class TaxRates extends EstatioDomainService<TaxRate> {

    public TaxRates() {
        super(TaxRates.class, TaxRate.class);
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "1")
    @ActionSemantics(Of.SAFE)
    public List<TaxRate> allTaxRates() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public TaxRate newRate(
            final Tax tax,
            final LocalDate startDate,
            final BigDecimal percentage) {
        TaxRate currentRate = tax.taxRateFor(startDate);
        TaxRate rate;
        if (currentRate == null || !startDate.equals(currentRate.getStartDate())) {
            rate = newTransientInstance(TaxRate.class);
            rate.setTax(tax);
            rate.setStartDate(startDate);
            persist(rate);
        } else {
            rate = currentRate;
        }
        rate.setPercentage(percentage);
        if (currentRate != null) {
            TaxRate currentNextRate = currentRate.getNext();
            currentRate.modifyNext(rate);
            rate.modifyNext(currentNextRate);
        }
        return rate;
    }

    @Programmatic
    public TaxRate findTaxRateByTaxAndDate(final Tax tax, final LocalDate date) {
        return firstMatch("findByTaxAndDate", "tax", tax, "date", date);
    }

}
