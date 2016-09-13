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
package org.incode.module.documents.dom.spi;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.title.TitleService;

import org.incode.module.documents.dom.impl.docs.DocumentTemplate;

public interface DocumentNamingService {

    /**
     * @param documentName - document name as provided (could be null)
     * @param domainObject - the domain object acting as the context/input of the document (to which the resultant document will be attached)
     * @param template - the template being used to create the document
     */
    @Programmatic String nameOf(
            final String documentName,
            final Object domainObject,
            final DocumentTemplate template);


    /**
     * Default implementation replaces any ':' (invalid as a file name).
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class Default implements DocumentNamingService {

        public String getId() {
            return "incodeDocuments.DocumentNamingService$Default";
        }

        @Override
        @Programmatic
        public String nameOf(
                final String documentName,
                final Object domainObject,
                final DocumentTemplate template) {
            final String rawName = rawNameOf(documentName, domainObject, template);
            return sanitized(rawName);
        }

        private String sanitized(final String rawName) {
            return rawName.replace(":", "-");
        }

        private String rawNameOf(
                final String documentName,
                final Object domainObject,
                final DocumentTemplate template) {
            String name =
                    documentName != null
                            ? documentName
                            : template.getName() + "-" + titleService.titleOf(domainObject);
            return template.withFileSuffix(name);
        }

        @Inject
        TitleService titleService;

    }

}
