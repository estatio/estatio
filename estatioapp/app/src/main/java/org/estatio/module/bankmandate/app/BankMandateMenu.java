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
package org.estatio.module.bankmandate.app;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementRepository;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.dom.BankMandateAgreementTypeEnum;
import org.estatio.module.bankmandate.dom.BankMandateRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.bankmandate.BankMandateMenu"
)
@DomainServiceLayout(
        named = "Finance",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "30.2"
)
public class BankMandateMenu extends UdoDomainService<BankMandateMenu> {

    public BankMandateMenu() {
        super(BankMandateMenu.class);
    }



    @Action(semantics = SemanticsOf.SAFE)
    public List<Agreement> findBankMandates(final String search){
        return agreementRepository.findByTypeTitleAndReferenceOrName(
                BankMandateAgreementTypeEnum.MANDATE.getTitle(),
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
