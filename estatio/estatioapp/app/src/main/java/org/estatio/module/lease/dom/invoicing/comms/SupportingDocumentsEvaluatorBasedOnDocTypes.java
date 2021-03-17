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
package org.estatio.module.lease.dom.invoicing.comms;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.spi.SupportingDocumentsEvaluator;

import org.estatio.module.invoice.dom.DocumentTypeData;

@DomainService(
        nature = NatureOfService.DOMAIN,
        menuOrder = "100"
)
public class SupportingDocumentsEvaluatorBasedOnDocTypes implements SupportingDocumentsEvaluator {

    @Override
    public List<Document> supportedBy(final Document candidateSupportingDocument) {
        return null;
    }

    @Override
    public Evaluation evaluate(final Document candidateSupportingDocument) {
        return DocumentTypeData.isPrimaryType(candidateSupportingDocument)
                ? Evaluation.NOT_SUPPORTING
                : Evaluation.UNKNOWN;
    }

}
