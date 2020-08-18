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
package org.estatio.module.coda.fixtures.elements.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.coda.dom.elements.CodaElement;
import org.estatio.module.coda.dom.elements.CodaMapping;
import org.estatio.module.coda.dom.elements.CodaMappingRepository;
import org.estatio.module.coda.dom.elements.CodaTransactionType;
import org.estatio.module.coda.dom.elements.DocumentType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"charge", "codaElement", "propertyFullyOwned", "codaTransactionType"}, callSuper = false)
@ToString(of={"charge", "codaElement", "propertyFullyOwned", "codaTransactionType"})
@Accessors(chain = true)
public final class CodaMappingBuilder
        extends BuilderScriptAbstract<CodaMapping, CodaMappingBuilder> {

    @Getter @Setter
    private CodaElement codaElement;

    @Getter @Setter
    private Charge charge;

    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private IncomingInvoiceType incomingInvoiceType;

    @Getter @Setter
    private boolean propertyFullyOwned;

    @Getter @Setter
    private DocumentType documentType;

    @Getter @Setter
    private CodaTransactionType codaTransactionType;

    @Getter
    private CodaMapping object;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("codaElement", executionContext, CodaElement.class);
        checkParam("charge", executionContext, Charge.class);
        checkParam("atPath", executionContext, String.class);
        checkParam("incomingInvoiceType", executionContext, IncomingInvoiceType.class);
        checkParam("propertyFullyOwned", executionContext, boolean.class);
        checkParam("documentType", executionContext, DocumentType.class);
        checkParam("codaTransactionType", executionContext, CodaTransactionType.class);

        object = codaMappingRepository.findOrCreate(atPath, documentType, incomingInvoiceType, codaTransactionType, charge, propertyFullyOwned, null, null, null, null,codaElement);

        executionContext.addResult(this, object.getCodaElement().getCode(), object);
    }

    @Inject
    CodaMappingRepository codaMappingRepository;
}

