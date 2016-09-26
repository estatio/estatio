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
package org.incode.module.documents.dom.impl.docs;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.incode.module.documents.dom.impl.types.DocumentType;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.util.List;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = DocumentAbstract.class
)
public class DocumentRepository {

    public String getId() {
        return "incodeDocuments.DocumentRepository";
    }

    //region > create
    @Programmatic
    public Document create(
            final DocumentType type,
            final String atPath,
            final String documentName,
            final String mimeType) {
        final DateTime createdAt = clockService.nowAsDateTime();
        final Document document = new Document(type, atPath, documentName, mimeType, createdAt);
        repositoryService.persist(document);
        return document;
    }
    //endregion


    @Programmatic
    public List<Document> findBetween(final LocalDate startDate, final LocalDate endDateIfAny) {

        final DateTime startDateTime = startDate.toDateTimeAtStartOfDay();

        final QueryDefault<Document> query;
        if (endDateIfAny != null) {
            final DateTime endDateTime = endDateIfAny.plusDays(1).toDateTimeAtStartOfDay();
            query = new QueryDefault<>(Document.class,
                    "findByCreatedAtBetween",
                    "startDateTime", startDateTime,
                    "endDateTime", endDateTime);
        }
        else {
            query = new QueryDefault<>(Document.class,
                    "findByCreatedAtAfter",
                    "startDateTime", startDateTime);
        }

        return repositoryService.allMatches(query);
    }

    @Programmatic
    public List<DocumentAbstract> allDocuments() {
        return repositoryService.allInstances(DocumentAbstract.class);
    }




    //region > injected services

    @Inject
    RepositoryService repositoryService;
    @Inject
    private ClockService clockService;

    //endregion

}
