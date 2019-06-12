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
package org.estatio.module.lease.app;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.lease.dom.invoicing.NumeratorForOutgoingInvoicesRepository;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.numerator.NumeratorForCollectionMenu"
)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "900.15")
public class NumeratorForOutgoingInvoicesMenu extends UdoDomainService<NumeratorForOutgoingInvoicesMenu> {

    public NumeratorForOutgoingInvoicesMenu() {
        super(NumeratorForOutgoingInvoicesMenu.class);
    }



    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public Numerator findCollectionNumberNumerator() {
        return numeratorRepository.findCollectionNumberNumerator();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public Numerator findInvoiceNumberNumerator(
            final FixedAsset fixedAsset,
            final Party seller) {
        return numeratorRepository.findInvoiceNumberNumerator(fixedAsset, seller);
    }

    public List<Party> choices1FindInvoiceNumberNumerator(final FixedAsset fixedAsset) {
        return allOwnersOf(fixedAsset);
    }


    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(sequence = "4")
    public Numerator createInvoiceNumberNumerator(
            final Property property,
            final Party seller,
            final String format,
            final BigInteger lastIncrement) {
        return numeratorRepository.findOrCreateInvoiceNumberNumerator(property, seller, format, lastIncrement);
    }

    public List<Party> choices1CreateInvoiceNumberNumerator(final Property property) {
        return allOwnersOf(property);
    }

    public String default2CreateInvoiceNumberNumerator() {
        return "XXX-%06d";
    }

    public BigInteger default3CreateInvoiceNumberNumerator() {
        return BigInteger.ZERO;
    }



    private List<Party> allOwnersOf(final FixedAsset fixedAsset) {
        return fixedAssetRoleRepository
                .findByAssetAndType(fixedAsset, FixedAssetRoleTypeEnum.PROPERTY_OWNER).stream()
                .map(FixedAssetRole::getParty)
                .collect(Collectors.toList());
    }

    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;


    @javax.inject.Inject
    NumeratorForOutgoingInvoicesRepository numeratorRepository;


}
