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

import org.estatio.fixture.financial.*;
import org.estatio.fixture.guarantee.GuaranteeForOxfTopModel001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.*;
import org.estatio.fixture.party.*;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.isisaddons.module.security.fixture.scripts.EstatioSecurityModuleAppSetUp;

public class EstatioDemoFixture extends DiscoverableFixtureScript {

    public EstatioDemoFixture() {
        super(null, "demo");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        execute(new EstatioBaseLineFixture(), executionContext);

        execute(new PersonForLinusTorvalds(), executionContext);

        execute(new BankAccountForAcme(), executionContext);

        execute(new BankAccountForHelloWorld(), executionContext);

        execute(new BankAccountAndMandateForTopModel(), executionContext);
        execute(new LeaseBreakOptionsForOxfTopModel001(), executionContext);

        execute(new BankAccountForMediaX(), executionContext);
        execute(new LeaseBreakOptionsForOxfMediax002(), executionContext);

        execute(new BankAccountForPret(), executionContext);
        execute(new LeaseForOxfPret004(), executionContext);

        execute(new BankAccountForMiracle(), executionContext);
        execute(new LeaseItemAndTermsForOxfMiracl005(), executionContext);

        execute(new BankAccountAndMandateForPoison(), executionContext);
        execute(new LeaseBreakOptionsForOxfPoison003(), executionContext);

        execute(new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003(), executionContext);
        execute(new LeaseItemAndLeaseTermForRentForKalPoison001(), executionContext);
        execute(new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001(), executionContext);
        execute(new InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005(), executionContext);

        execute(new GuaranteeForOxfTopModel001(), executionContext);
        
        execute(new PersonForGinoVannelli(), executionContext);

        execute(new EstatioSecurityModuleAppSetUp(), executionContext);

        
    }
}
