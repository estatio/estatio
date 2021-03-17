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
package org.estatio.module.lease.fixtures.numerators.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.EstatioApplicationTenancyRepositoryForLease;
import org.estatio.module.lease.dom.invoicing.NumeratorForOutgoingInvoicesRepository;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.dom.Party;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bi;

@EqualsAndHashCode(of={"property"}, callSuper = false)
@ToString(of={"property"})
@Accessors(chain = true)
public final class PropertyOwnerNumeratorBuilder
        extends BuilderScriptAbstract<Numerator, PropertyOwnerNumeratorBuilder> {

    @Getter @Setter
    private Property property;

    @Getter @Setter
    private Party owner;

    @Getter
    private Numerator object;

    @Override
    protected void execute(final ExecutionContext ec) {

        checkParam("property", ec, Property.class);
        checkParam("owner", ec, Party.class);

        ApplicationTenancy applicationTenancy =
                estatioApplicationTenancyRepository.findOrCreateTenancyFor(
                        property, owner);
        this.object =
                estatioNumeratorRepository.createInvoiceNumberNumerator(
                        property,
                        owner,
                        numeratorReferenceFor(property),
                        bi(0));

        ec.addResult(this, property.getReference(), object);
    }

    public static String numeratorReferenceFor(final Property property) {
        return property.getReference().concat("-%04d");
    }

    @Inject
    NumeratorForOutgoingInvoicesRepository estatioNumeratorRepository;

    @Inject
    EstatioApplicationTenancyRepositoryForLease estatioApplicationTenancyRepository;
}
