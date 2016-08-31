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

import javax.inject.Inject;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.value.Blob;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.document.Document;
import org.estatio.dom.document.DocumentType;

//@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
//@DomainServiceLayout(named = "Other", menuBar = DomainServiceLayout.MenuBar.PRIMARY, menuOrder = "80.10")
public class DocumentForFixedAssetMenu {

    public String getId() {
        return "documentsForFixedAsset";
    }

    public String iconName() {
        return "Document";
    }

    @MemberOrder(sequence = "2")
    public List<Document> allDocuments() {
        return documentForFixedAssetRepository.allDocuments();
    }

    @MemberOrder(sequence = "1")
    public DocumentForFixedAsset newDocument(
            final String name,
            final Blob file,
            final FixedAsset fixedAsset,
            final DocumentType type) {
        return documentForFixedAssetRepository.newDocument(name, file, fixedAsset, type);
    }

    // //////////////////////////////////////

    @Inject
    private DocumentForFixedAssetRepository documentForFixedAssetRepository;

}
