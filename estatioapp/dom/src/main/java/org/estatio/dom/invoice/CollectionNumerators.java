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
package org.estatio.dom.invoice;

import java.math.BigInteger;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.Numerators;
import org.estatio.domsettings.EstatioSettingsService;

@DomainService
@DomainServiceLayout(
        named = "Administration",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "120.2")
public class CollectionNumerators extends UdoDomainService<CollectionNumerators> {

    public CollectionNumerators() {
        super(CollectionNumerators.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public Numerator findCollectionNumberNumerator() {
        return numerators.findGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "2")
    public Numerator createCollectionNumberNumerator(
            final @ParameterLayout(named = "Format") String format,
            final @ParameterLayout(named = "Last value") BigInteger lastIncrement) {

        return numerators.createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, format, lastIncrement);
    }

    public String default0CreateCollectionNumberNumerator() {
        return "%09d";
    }

    public BigInteger default1CreateCollectionNumberNumerator() {
        return BigInteger.ZERO;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "3")
    public Numerator findInvoiceNumberNumerator(
            final FixedAsset fixedAsset) {
        return numerators.findScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, fixedAsset);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "4")
    public Numerator createInvoiceNumberNumerator(
            final Property property,
            final @ParameterLayout(named = "Format") String format,
            final @ParameterLayout(named = "Last value") BigInteger lastIncrement) {
        return numerators.createScopedNumerator(
                Constants.INVOICE_NUMBER_NUMERATOR_NAME, property, format, lastIncrement);
    }

    public String default1CreateInvoiceNumberNumerator() {
        return "XXX-%06d";
    }

    public BigInteger default2CreateInvoiceNumberNumerator() {
        return BigInteger.ZERO;
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    Numerators numerators;

    @javax.inject.Inject
    EstatioSettingsService settings;

}
