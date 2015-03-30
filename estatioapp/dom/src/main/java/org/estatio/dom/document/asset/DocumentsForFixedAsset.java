/*
 *  Copyright 2015 Eurocommercial Properties NV
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
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.value.Blob;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.UdoDomainService;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.document.Document;
import org.estatio.dom.document.DocumentType;

@DomainService(nature = NatureOfService.DOMAIN)
@DomainServiceLayout(named = "Other", menuBar = DomainServiceLayout.MenuBar.PRIMARY, menuOrder = "80.10")
public class DocumentsForFixedAsset extends UdoDomainRepositoryAndFactory<DocumentForFixedAsset> {

    public DocumentsForFixedAsset()
    {
        super(DocumentsForFixedAsset.class, DocumentForFixedAsset.class);
    }

    public String getId() {
        return "documentsForFixedAsset";
    }

    public String iconName() {
        return "Document";
    }

    @MemberOrder(sequence = "2")
    public List<Document> allDocuments() {
        return container.allInstances(Document.class);
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

    @MemberOrder(sequence = "1")
    public DocumentForFixedAsset newDocument(
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "File") Blob file,
            final FixedAsset fixedAsset,
            final @ParameterLayout(named = "Type") DocumentType type) {
        DocumentForFixedAsset document = container.newTransientInstance(DocumentForFixedAsset.class);
        document.setName(name);
        document.setFile(file);
        document.setFixedAsset(fixedAsset);
        container.persist(document);
        return document;
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private DomainObjectContainer container;
}
