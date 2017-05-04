/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.capex.dom.documents;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "incomingDocument.IncomingDocumentMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Documents"
)
public class IncomingDocumentMenu {

    @Action(semantics = SemanticsOf.SAFE)
    public List<HasDocumentAbstract> incomingDocuments() {
        return factory.map(repository.findIncomingDocuments());
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<HasDocumentAbstract> incomingOrderDocuments() {
        return factory.map(repository.findUnclassifiedIncomingOrders());
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<HasDocumentAbstract> incomingInvoiceDocuments() {
        return factory.map(repository.findUnclassifiedIncomingInvoices());
    }

    @Inject
    IncomingDocumentRepository repository;

    @Inject
    HasDocumentAbstract.Factory factory;


}
