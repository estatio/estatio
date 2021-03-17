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
package org.estatio.module.lease.dom.invoicing.summary.comms;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.communications.dom.impl.comms.Communication;

import org.estatio.module.invoice.dom.DocumentTypeData;

public abstract class DocAndCommAbstract_communication<T extends DocAndCommAbstract<T>> extends DocAndCommAbstract_abstract<T> {

    public DocAndCommAbstract_communication(final T docAndComm, final DocumentTypeData documentTypeData) {
        super(docAndComm, documentTypeData);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Property()
    @MemberOrder(sequence = "5") // workaround, DocAndCommForInvoiceDoc.layout.xml not being honoured for table, for some reason
    public Communication $$() {
        return super.getCommunication();
    }

}
