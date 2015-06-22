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

import org.estatio.fixture.asset._PropertyForGraIt;
import org.estatio.fixture.asset._PropertyForHanSe;
import org.estatio.fixture.asset._PropertyForVivFr;
import org.estatio.fixture.financial._BankAccountAndMandateForPoisonNl;
import org.estatio.fixture.financial.BankAccountAndMandateForTopModelGb;
import org.estatio.fixture.financial.BankAccountForAcmeNl;
import org.estatio.fixture.financial._BankAccountForHelloWorldNl;
import org.estatio.fixture.financial.BankAccountForMediaXGb;
import org.estatio.fixture.financial.BankAccountForMiracleGb;
import org.estatio.fixture.financial.BankAccountForPretGb;
import org.estatio.fixture.guarantee.GuaranteeForOxfTopModel001Gb;
import org.estatio.fixture.interactivemap.InteractiveMapDocumentForOxf;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfMediax002Gb;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.fixture.lease._LeaseForOxfPret004Gb;
import org.estatio.fixture.lease.LeaseItemAndLeaseTermForRentForKalPoison001;

import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMiracl005Gb;
import org.estatio.fixture.party.PersonForGinoVannelliGb;
import org.estatio.fixture.party.PersonForLinusTorvaldsNl;

import org.estatio.fixture.project.ProgramForGra;
import org.estatio.fixture.project.ProgramForKal;
import org.estatio.fixture.project.ProjectsForGra;
import org.estatio.fixture.project.ProjectsForKal;


public class EstatioDemoFixture extends DiscoverableFixtureScript {

    public EstatioDemoFixture() {
        super(null, "demo");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.executeChild(this, new EstatioBaseLineFixture());

        executionContext.executeChild(this, new PersonForLinusTorvaldsNl());

        executionContext.executeChild(this, new BankAccountForAcmeNl());

        executionContext.executeChild(this, new _BankAccountForHelloWorldNl());

        executionContext.executeChild(this, new BankAccountAndMandateForTopModelGb());
        executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());

        executionContext.executeChild(this, new BankAccountForMediaXGb());
        executionContext.executeChild(this, new LeaseBreakOptionsForOxfMediax002Gb());

        executionContext.executeChild(this, new BankAccountForPretGb());
        executionContext.executeChild(this, new _LeaseForOxfPret004Gb());

        executionContext.executeChild(this, new BankAccountForMiracleGb());
        executionContext.executeChild(this, new LeaseItemAndTermsForOxfMiracl005Gb());

        executionContext.executeChild(this, new _BankAccountAndMandateForPoisonNl());
        executionContext.executeChild(this, new LeaseBreakOptionsForOxfPoison003Gb());

        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
        executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentForKalPoison001());
        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());
        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005());

        executionContext.executeChild(this, new GuaranteeForOxfTopModel001Gb());

        executionContext.executeChild(this, new PersonForGinoVannelliGb());


        executionContext.executeChild(this, new _PropertyForGraIt());
        executionContext.executeChild(this, new _PropertyForVivFr());
        executionContext.executeChild(this, new _PropertyForHanSe());

        
        executionContext.executeChild(this, new ProgramForGra());
        executionContext.executeChild(this, new ProgramForKal());
        
        executionContext.executeChild(this, new ProjectsForKal());
        executionContext.executeChild(this, new ProjectsForGra());


        executionContext.executeChild(this, new InteractiveMapDocumentForOxf());
        
        
        final FixtureClock fixtureClock = (FixtureClock) FixtureClock.getInstance();
        fixtureClock.reset();

    }
}
