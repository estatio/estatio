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
package org.estatio.dom.bankmandate;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRepository;
import org.estatio.dom.utils.StringUtils;

@DomainService(
    nature = NatureOfService.VIEW
)
@DomainServiceLayout(
        named = "Accounts",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "30.2"
)
public class BankMandateMenu extends UdoDomainService<BankMandateMenu> {

    public BankMandateMenu() {
        super(BankMandateMenu.class);
    }

    public List<Agreement> findBankMandates(final String search){
        return agreementRepository.findByTypeTitleAndReferenceOrName(
                BankMandateConstants.AT_MANDATE,
                StringUtils.wildcardToCaseInsensitiveRegex(search));
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<BankMandate> allBankMandates() {
        return bankMandateRepository.allBankMandates();
    }

    @Inject
    protected BankMandateRepository bankMandateRepository;

    @Inject
    private AgreementRepository agreementRepository;

}
