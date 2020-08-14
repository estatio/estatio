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
package org.estatio.module.coda.fixtures.elements.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.coda.dom.elements.CodaElement;
import org.estatio.module.coda.dom.elements.CodaMapping;
import org.estatio.module.coda.dom.elements.CodaMappingRepository;
import org.estatio.module.coda.dom.elements.CodaTransactionType;
import org.estatio.module.coda.dom.elements.DocumentType;
import org.estatio.module.coda.fixtures.elements.builders.CodaMappingBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum CodaMapping_enum
        implements PersonaWithBuilderScript<CodaMapping, CodaMappingBuilder>,
        PersonaWithFinder<CodaMapping> {

    FRL5_12345_INC1(CodaElement_enum.FRL5_12345, Charge_enum.FrIncomingCharge1, "/FRA", IncomingInvoiceType.CAPEX, true, DocumentType.INVOICE_IN, CodaTransactionType.STAT),
    FRL4_77777_INC1(CodaElement_enum.FRL4_77777, Charge_enum.FrIncomingCharge1, "/FRA", IncomingInvoiceType.CAPEX, true, DocumentType.INVOICE_IN, CodaTransactionType.STAT),
    ;

    private final CodaElement_enum codaElement_d;
    private final Charge_enum charge_d;
    private final String atPath;
    private IncomingInvoiceType incomingInvoiceType;
    private boolean propertyFullyOwned;
    private DocumentType documentType;
    private CodaTransactionType codaTransactionType;

    @Override
    public CodaMappingBuilder builder() {
        return new CodaMappingBuilder()
                .setPrereq((f,ec)->{
                    final CodaElement codaElement = f.objectFor(codaElement_d, ec);
                    f.setCodaElement(codaElement);
                })
                .setPrereq((f,ec)->{
                    final Charge charge = f.objectFor(charge_d, ec);
                    f.setCharge(charge);
                })
                .setAtPath(atPath)
                .setIncomingInvoiceType(incomingInvoiceType)
                .setDocumentType(documentType)
                .setCodaTransactionType(codaTransactionType)
                .setPropertyFullyOwned(propertyFullyOwned)
                ;
    }

    @Override
    public CodaMapping findUsing(final ServiceRegistry2 serviceRegistry) {
        return serviceRegistry.lookupService(CodaMappingRepository.class).findByAll(atPath, documentType, incomingInvoiceType, codaTransactionType, charge_d.findUsing(serviceRegistry),propertyFullyOwned, null, null, null, null,codaElement_d.findUsing(serviceRegistry));
    }

}
