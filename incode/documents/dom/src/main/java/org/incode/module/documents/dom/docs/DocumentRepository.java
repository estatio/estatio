/*
 *  Copyright 2016 Dan Haywood
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
package org.incode.module.documents.dom.docs;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.documents.dom.types.DocumentType;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Document.class
)
public class DocumentRepository {

    @Programmatic
    public Document createBlob(
            final DocumentType type,
            final String atPath,
            final Blob blob) {
        final Document document = new Document(type, atPath, blob);
        repositoryService.persist(document);
        return document;
    }

    @Programmatic
    public Document createClob(
            final DocumentType type,
            final String atPath,
            final Clob clob) {
        final Document document = new Document(type, atPath, clob);
        repositoryService.persist(document);
        return document;
    }


    @Programmatic
    public List<Document> allDocuments() {
        return repositoryService.allInstances(Document.class);
    }

    //region > injected services

    @Inject
    RepositoryService repositoryService;

    //endregion

}
