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
package org.estatio.module.capex.seed.ordertmplt;

import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.fixture.DocumentTemplateFSAbstract;

import org.estatio.module.invoice.dom.DocumentTypeData;

public class DocumentTypeFSForOrderConfirm extends DocumentTemplateFSAbstract {


    @Override
    protected void execute(final ExecutionContext executionContext) {
        upsertType(DocumentTypeData.ORDER_CONFIRM, executionContext);
    }


    private DocumentType upsertType(
            DocumentTypeData documentTypeData,
            ExecutionContext executionContext) {
        return upsertType(documentTypeData.getRef(), documentTypeData.getName(), executionContext);
    }


}
