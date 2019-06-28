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
package org.estatio.module.invoicegroup.fixtures;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.invoicegroup.dom.InvoiceGroup;
import org.estatio.module.invoicegroup.dom.InvoiceGroupRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum InvoiceGroup_enum implements PersonaWithBuilderScript<InvoiceGroup, InvoiceGroupBuilder>,
        PersonaWithFinder<InvoiceGroup> {

    BudNl   ("BUD", new Property_enum[] {Property_enum.BudNl}),
    RonIt   ("RON", new Property_enum[] {Property_enum.RonIt}),
    GraIt   ("GRA", new Property_enum[] {Property_enum.GraIt}),
    HanSe   ("HAN", new Property_enum[] {Property_enum.HanSe}),
    KalNl   ("KAL", new Property_enum[] {Property_enum.KalNl}),
    MacFr   ("MAC", new Property_enum[] {Property_enum.MacFr}),
    MnsFr   ("MNS", new Property_enum[] {Property_enum.MnsFr}),
    OxfGb   ("OXF", new Property_enum[] {Property_enum.OxfGb}),
    VivFr   ("VIV", new Property_enum[] {Property_enum.VivFr, Property_enum.BvvFr});

    private final String ref;
    private final Property_enum[] properties_d;

    @Override
    public InvoiceGroup findUsing(final ServiceRegistry2 serviceRegistry) {
        final InvoiceGroupRepository repository =
                serviceRegistry.lookupService(InvoiceGroupRepository.class);
        return repository.findByReference(ref).orElseThrow(() -> new IllegalStateException(
                String.format("Invoice group '%s' not setup", ref)));
    }

    public InvoiceGroupBuilder builder() {
        return new InvoiceGroupBuilder()
                        .setRef(ref)
                        .setPrereq((f, ec) -> {
                            for (final Property_enum property_d : properties_d) {
                                final Property property = f.objectFor(property_d, ec);
                                f.getProperties().add(property);
                            }
                        });
    }

    @Programmatic
    public static class PersistAll extends FixtureScript {

        @Override
        protected void execute(final ExecutionContext executionContext) {
            for (final InvoiceGroup_enum datum : values()) {
                executionContext.executeChild(this, datum.builder());
            }
        }
    }
}
