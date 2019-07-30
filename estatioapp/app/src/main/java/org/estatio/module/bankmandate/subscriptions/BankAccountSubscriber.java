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
package org.estatio.module.bankmandate.subscriptions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.bankmandate.dom.BankMandateRepository;
import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.financial.dom.BankAccount;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class BankAccountSubscriber extends UdoDomainService<BankAccountSubscriber> {

    public BankAccountSubscriber() {
        super(BankAccountSubscriber.class);
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final BankAccount.BicUpdatedEvent event) {

        BankAccount bankAccount = event.getSource();
        switch (event.getEventPhase()) {
        case VALIDATE:

            if (bankMandateRepository.findBankMandatesFor(bankAccount).size() > 0){
                event.invalidate("This bank account is used on a bank mandate; the bic cannot be updated");
            }

            break;
        default:
            break;
        }

    }

    @Inject BankMandateRepository bankMandateRepository;


}