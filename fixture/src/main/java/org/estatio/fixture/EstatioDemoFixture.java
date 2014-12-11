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
package org.estatio.fixture;

import org.apache.isis.applib.fixtures.FixtureClock;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.estatio.fixture.asset.PropertyForGra;
import org.estatio.fixture.asset.PropertyForHan;
import org.estatio.fixture.asset.PropertyForViv;
import org.estatio.fixture.financial.BankAccountAndMandateForPoison;
import org.estatio.fixture.financial.BankAccountAndMandateForTopModel;
import org.estatio.fixture.financial.BankAccountForAcme;
import org.estatio.fixture.financial.BankAccountForHelloWorld;
import org.estatio.fixture.financial.BankAccountForMediaX;
import org.estatio.fixture.financial.BankAccountForMiracle;
import org.estatio.fixture.financial.BankAccountForPret;
import org.estatio.fixture.guarantee.GuaranteeForOxfTopModel001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfMediax002;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfPoison003;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.fixture.lease.LeaseForOxfPret004;
import org.estatio.fixture.lease.LeaseItemAndLeaseTermForRentForKalPoison001;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMiracl005;
import org.estatio.fixture.party.PersonForGinoVannelli;
import org.estatio.fixture.party.PersonForLinusTorvalds;

public class EstatioDemoFixture extends DiscoverableFixtureScript {

    public EstatioDemoFixture() {
        super(null, "demo");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.executeChild(this, new EstatioBaseLineFixture());

        executionContext.executeChild(this, new PersonForLinusTorvalds());

        executionContext.executeChild(this, new BankAccountForAcme());

        executionContext.executeChild(this, new BankAccountForHelloWorld());

        executionContext.executeChild(this, new BankAccountAndMandateForTopModel());
        executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());

        executionContext.executeChild(this, new BankAccountForMediaX());
        executionContext.executeChild(this, new LeaseBreakOptionsForOxfMediax002());

        executionContext.executeChild(this, new BankAccountForPret());
        executionContext.executeChild(this, new LeaseForOxfPret004());

        executionContext.executeChild(this, new BankAccountForMiracle());
        executionContext.executeChild(this, new LeaseItemAndTermsForOxfMiracl005());

        executionContext.executeChild(this, new BankAccountAndMandateForPoison());
        executionContext.executeChild(this, new LeaseBreakOptionsForOxfPoison003());

        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
        executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentForKalPoison001());
        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());
        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005());

        executionContext.executeChild(this, new GuaranteeForOxfTopModel001());

        executionContext.executeChild(this, new PersonForGinoVannelli());

        executionContext.executeChild(this, new PropertyForGra());
        executionContext.executeChild(this, new PropertyForViv());
        executionContext.executeChild(this, new PropertyForHan());

        final FixtureClock fixtureClock = (FixtureClock) FixtureClock.getInstance();
        fixtureClock.reset();

    }
}
