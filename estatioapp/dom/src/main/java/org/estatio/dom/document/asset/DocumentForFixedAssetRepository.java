/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.dom.document.asset;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.value.Blob;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.document.Document;
import org.estatio.dom.document.DocumentType;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = DocumentForFixedAsset.class)
public class DocumentForFixedAssetRepository extends UdoDomainRepositoryAndFactory<DocumentForFixedAsset> {

    public DocumentForFixedAssetRepository() {
        super(DocumentForFixedAssetRepository.class, DocumentForFixedAsset.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public DocumentForFixedAsset newDocument(
            final String name,
            final Blob file,
            final FixedAsset fixedAsset,
            final DocumentType type) {
        DocumentForFixedAsset document = newTransientInstance(DocumentForFixedAsset.class);
        document.setName(name);
        document.setFile(file);
        document.setFixedAsset(fixedAsset);
        persistIfNotAlready(document);
        return document;
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Document> allDocuments() {
        return allInstances(Document.class);
    }

    @Programmatic
    public List<DocumentForFixedAsset> findByFixedAsset(final FixedAsset fixedAsset) {
        return allMatches("findByFixedAsset", "fixedAsset", fixedAsset);
    }

    @Programmatic
    public List<DocumentForFixedAsset> findByFixedAssetAndType(final FixedAsset fixedAsset, final DocumentType type) {
        return allMatches("findByFixedAssetAndType", "fixedAsset", fixedAsset, "type", type);
    }

    @Programmatic
    public DocumentForFixedAsset findFirstByFixedAssetAndType(final FixedAsset fixedAsset, final DocumentType type) {
        return firstMatch("findByFixedAssetAndType", "fixedAsset", fixedAsset, "type", type);
    }
}
