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
package org.estatio.dom.lease.invoicing.dnc;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.spi.SupportingDocumentsEvaluator;

import org.estatio.dom.invoice.Constants;

@DomainService(
        nature = NatureOfService.DOMAIN,
        menuOrder = "100"
)
public class SupportingDocumentsEvaluatorBasedOnDocTypes implements SupportingDocumentsEvaluator {

    private static final ArrayList<String> REGULAR_DOC_TYPES =
            Lists.newArrayList(Constants.DOC_TYPE_REF_INVOICE, Constants.DOC_TYPE_REF_PRELIM);

    @Override
    public Document supportedBy(final Document candidateSupportingDocument) {
        return null;
    }

    @Override
    public boolean isSupporting(final Document candidateSupportingDocument) {
        String docTypeRef = candidateSupportingDocument.getType().getReference();

        return !REGULAR_DOC_TYPES.contains(docTypeRef);
    }

}
