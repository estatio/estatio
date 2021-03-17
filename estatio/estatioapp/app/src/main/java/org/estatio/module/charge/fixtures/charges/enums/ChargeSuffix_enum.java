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
package org.estatio.module.charge.fixtures.charges.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum ChargeSuffix_enum {

    Rent("_RENT"),
    Marketing("_MARKETING"),
    ServiceCharge ("_SERVICE_CHARGE"),
    ServiceCharge2 ("_SERVICE_CHARGE2"),
    IncomingCharge1 ("_INCOMING_CHARGE_1"),
    IncomingCharge2 ("_INCOMING_CHARGE_2"),
    IncomingCharge3 ("_INCOMING_CHARGE_3"),
    TurnoverRent ("_TURNOVER_RENT"),
    Percentage ("_PERCENTAGE"),
    Deposit ("_DEPOSIT"),
    Discount ("_DISCOUNT"),
    EntryFee ("_ENTRY_FEE"),
    Tax ("_TAX"),
    ServiceChargeIndexable ("_SVC_CHG_INDEXABLE"),
    ;

    private final String suffix;

}
