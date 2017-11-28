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

import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.fixtures.chargegroups.enums.ChargeGroup_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum ChargeNoCountry_enum {

    Rent (ChargeGroup_enum.Rent,
            "Rent", "_RENT", Applicability.OUTGOING),
    ServiceCharge (ChargeGroup_enum.ServiceCharge,
            "Service Charge 1", "_SERVICE_CHARGE", Applicability.OUTGOING),
    ServiceCharge2 (ChargeGroup_enum.ServiceCharge,
            "Service Charge 2", "_SERVICE_CHARGE2",
            Applicability.OUTGOING),
    IncomingCharge1 (ChargeGroup_enum.ServiceCharge,
            "Incoming Charge 1", "_INCOMING_CHARGE_1",
            Applicability.INCOMING),
    IncomingCharge2 (ChargeGroup_enum.ServiceCharge,
            "Incoming Charge 2", "_INCOMING_CHARGE_2",
            Applicability.INCOMING),
    IncomingCharge3 (ChargeGroup_enum.ServiceCharge,
            "Incoming Charge 3", "_INCOMING_CHARGE_3",
            Applicability.INCOMING),
    TurnoverRent (ChargeGroup_enum.TurnoverRent,
            "Turnover Rent", "_TURNOVER_RENT",
            Applicability.OUTGOING),
    Percentage (ChargeGroup_enum.Percentage,
            "Percentage", "_PERCENTAGE", Applicability.OUTGOING),
    Deposit (ChargeGroup_enum.Deposit,
            "Deposit", "_DEPOSIT", Applicability.OUTGOING),
    Discount (ChargeGroup_enum.Discount,
            "Discount", "_DISCOUNT", Applicability.OUTGOING),
    EntryFee (ChargeGroup_enum.EntryFee,
            "Entry Fee", "_ENTRY_FEE", Applicability.OUTGOING),
    ServiceChargeIndexable (ChargeGroup_enum.ServiceChargeIndexable,
            "Service Charge Indexable", "_SVC_CHG_INDEXABLE", Applicability.OUTGOING),
    Tax (ChargeGroup_enum.Tax,
            "Tax", "_TAX", Applicability.IN_AND_OUT),
    Marketing (ChargeGroup_enum.Marketing,
            "Marketing", "_MARKETING", Applicability.IN_AND_OUT),
    ;

    private final ChargeGroup_enum chargeGroup;
    private final String descriptionPrefix;
    private final String chargeSuffix;
    private final Applicability applicability;

}
