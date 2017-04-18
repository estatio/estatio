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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;

@DomainObject(
        objectType = "capex.IncomingDocumentViewModel"
)
@XmlRootElement(name = "incomingInvoice")
@XmlType(
        propOrder = {
                "document"
        }
)
public class IncomingDocumentViewModel extends HasDocumentAbstract {

    public IncomingDocumentViewModel() {}
    public IncomingDocumentViewModel(final Document document) {
        super(document);
    }


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class Mapper {

        @Programmatic
        public IncomingDocumentViewModel map(final Document document) {
            return serviceRegistry2.injectServicesInto(new IncomingDocumentViewModel(document));
        }

        @Programmatic
        public List<IncomingDocumentViewModel> map(final List<Document> documents) {
            return Lists.newArrayList(
                    FluentIterable.from(documents)
                            .transform(doc -> map(doc))
                            .toList());
        }

        @Inject
        ServiceRegistry2 serviceRegistry2;

    }
}
