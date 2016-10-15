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
package org.incode.module.communications.dom.spi;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.communications.dom.mixins.Document_email;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.types.DocumentType;

/**
 * As used by the {@link Document_email} mixin.
 */
public interface DocumentCommunicationSupport {

    @Programmatic
    DocumentType emailCoverNoteDocumentTypeFor(final Document document);

    @Programmatic
    void inferEmailHeaderFor(final Document document, final CommHeaderForEmail header);

    @Programmatic
    void inferPrintHeaderFor(final Document document, final CommHeaderForPrint header);

}
