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
package org.estatio.app.menus.numerator;

import java.math.BigInteger;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.appsettings.EstatioSettingsService;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.NumeratorForCollectionRepository;
import org.estatio.numerator.dom.impl.Numerator;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Administration",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "120.2")
public class NumeratorForCollectionMenu extends UdoDomainService<NumeratorForCollectionMenu> {

    public NumeratorForCollectionMenu() {
        super(NumeratorForCollectionMenu.class);
    }



    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public Numerator findCollectionNumberNumerator() {
        return numeratorRepository.findCollectionNumberNumerator();
    }




    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(sequence = "2")
    public Numerator createCollectionNumberNumerator(
            final String format,
            final BigInteger lastValue,
            final ApplicationTenancy applicationTenancy) {
        return numeratorRepository.createCollectionNumberNumerator(format, lastValue, applicationTenancy);
    }

    public String default0CreateCollectionNumberNumerator() {
        return "%09d";
    }

    public BigInteger default1CreateCollectionNumberNumerator() {
        return BigInteger.ZERO;
    }




    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public Numerator findInvoiceNumberNumerator(
            final FixedAsset fixedAsset,
            final ApplicationTenancy applicationTenancy) {
        return numeratorRepository.findInvoiceNumberNumerator(fixedAsset, applicationTenancy);
    }




    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(sequence = "4")
    public Numerator createInvoiceNumberNumerator(
            final Property property,
            final String format,
            final BigInteger lastIncrement,
            final ApplicationTenancy applicationTenancy) {
        return numeratorRepository.createInvoiceNumberNumerator(property, format, lastIncrement, applicationTenancy);
    }

    public String default1CreateInvoiceNumberNumerator() {
        return "XXX-%06d";
    }

    public BigInteger default2CreateInvoiceNumberNumerator() {
        return BigInteger.ZERO;
    }





    @javax.inject.Inject
    NumeratorForCollectionRepository numeratorRepository;

    @javax.inject.Inject
    EstatioSettingsService settings;

}
