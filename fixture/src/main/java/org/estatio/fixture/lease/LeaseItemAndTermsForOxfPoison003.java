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
package org.estatio.fixture.lease;

public class LeaseItemAndTermsForOxfPoison003 extends LeaseItemAndTermsAbstract {

    @Override
    protected void execute(ExecutionContext fixtureResults) {
        createLeaseTermsForOxfPoison003(fixtureResults);
    }

    private void createLeaseTermsForOxfPoison003(ExecutionContext executionContext) {
        executeChild(new LeaseItemAndLeaseTermForRentOf2ForOxfPoison003(), executionContext);
        executeChild(new LeaseItemAndLeaseTermForServiceChargeForOxfPoison003(), executionContext);
        executeChild(new LeaseItemAndLeaseTermForTurnoverRentForOxfPoison003(), executionContext);
    }

}
