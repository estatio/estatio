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
package org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.financial.fixtures.bankaccount.builders.BankAccountBuilder;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum BankAccount_enum
        implements PersonaWithBuilderScript<BankAccount, BankAccountBuilder>, PersonaWithFinder<BankAccount> {

    AcmeNl          (OrganisationAndComms_enum.AcmeNl,       "NL31ABNA0580744433"),
    HelloWorldGb    (OrganisationAndComms_enum.HelloWorldGb, "GB31ABNA0580744434"),
    HelloWorldNl    (OrganisationAndComms_enum.HelloWorldNl, "NL31ABNA0580744434"),
    MediaXGb        (OrganisationAndComms_enum.MediaXGb,     "NL31ABNA0580744436"),
    MiracleGb       (OrganisationAndComms_enum.MiracleGb,    "NL31ABNA0580744439"),

    // nb: this is misnamed, is actually second bank account for HelloWorldGb party
    Oxford          (OrganisationAndComms_enum.HelloWorldGb, "NL31ABNA0580744432"),

    PoisonNl        (OrganisationAndComms_enum.PoisonNl,     "NL31ABNA0580744437"),
    PretGb          (OrganisationAndComms_enum.PretGb,       "NL31ABNA0580744438"),
    TopModelGb      (OrganisationAndComms_enum.TopModelGb,   "NL31ABNA0580744435")
    ;

    private final OrganisationAndComms_enum organisation_d;
    private final String iban;


    @Override
    public BankAccountBuilder builder() {
        return new BankAccountBuilder()
                .setIban(iban)
                .setPrereq((f,ec) -> f.setParty(f.objectFor(organisation_d, ec)));
    }

    @Override
    public BankAccount findUsing(final ServiceRegistry2 serviceRegistry) {
        final BankAccountRepository bankAccountRepository = serviceRegistry.lookupService(BankAccountRepository.class);
        final Organisation owner = organisation_d.findUsing(serviceRegistry);
        return bankAccountRepository.findBankAccountByReference(owner, iban);
    }

}
