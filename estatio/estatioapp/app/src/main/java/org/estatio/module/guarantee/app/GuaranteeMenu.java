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
package org.estatio.module.guarantee.app;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.guarantee.dom.Guarantee;
import org.estatio.module.guarantee.dom.GuaranteeRepository;
import org.estatio.module.guarantee.dom.GuaranteeType;
import org.estatio.module.lease.dom.Lease;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.guarantee.GuaranteeMenu"
)
@DomainServiceLayout(
        named = "Finance",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "30.4"
)
public class GuaranteeMenu extends UdoDomainService<Guarantee> {

    public GuaranteeMenu() {
        super(GuaranteeMenu.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Guarantee newGuarantee(
            final Lease lease,
            final @Parameter(regexPattern = ReferenceType.Meta.REGEX) String reference,
            final String name,
            final GuaranteeType guaranteeType,
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate,
            final String description,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal contractualAmount,
            final BigDecimal startAmount
            ) {

        return guaranteeRepository.newGuarantee(lease,reference,name,guaranteeType,startDate,endDate,description,contractualAmount,startAmount);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Guarantee> findGuarantees(
            final @ParameterLayout(named = "Reference, Name or Comments", describedAs = "May include wildcards '*' and '?'") String refOrNameOrComments) {
        String pattern = StringUtils.wildcardToCaseInsensitiveRegex(refOrNameOrComments);
        return guaranteeRepository.findGuarantees(refOrNameOrComments);
    }


    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "99")
    public List<Guarantee> allGuarantees() {
        return guaranteeRepository.allGuarantees();
    }

    // //////////////////////////////////////

    @Inject
    private AgreementTypeRepository agreementTypeRepository;

    @Inject
    private AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    private FinancialAccountRepository financialAccountRepository;

    @Inject
    private GuaranteeRepository guaranteeRepository;

}
