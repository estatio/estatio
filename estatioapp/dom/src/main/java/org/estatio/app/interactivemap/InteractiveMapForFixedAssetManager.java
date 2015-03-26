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
package org.estatio.app.interactivemap;

import java.util.List;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.wicket.svg.cpt.applib.InteractiveMap;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.asset.Property;
import org.estatio.dom.document.DocumentType;
import org.estatio.dom.document.asset.DocumentForFixedAsset;
import org.estatio.dom.document.asset.DocumentsForFixedAsset;

@ViewModel
public class InteractiveMapForFixedAssetManager extends EstatioViewModel {

    public InteractiveMapForFixedAssetManager() {
    }

    public InteractiveMapForFixedAssetManager(final Property property, final InteractiveMapForFixedAssetRepresentation representation) {
        this.property = property;
        this.representationStr = representation.name();
    }

    // //////////////////////////////////////

    private Property property;

    @Title(sequence = "1")
    @MemberOrder(sequence = "1")
    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    @MemberOrder(name = "property", sequence = "1")
    public InteractiveMapForFixedAssetManager selectProperty(
            final Property property) {
        setProperty(property);
        return this;
    }

    // //////////////////////////////////////

    private DocumentForFixedAsset document;

    @MemberOrder(sequence = "2")
    public DocumentForFixedAsset getDocument() {
        return document;
    }

    public void setDocument(DocumentForFixedAsset document) {
        this.document = document;
    }

    @MemberOrder(name = "document", sequence = "1")
    public InteractiveMapForFixedAssetManager selectDocument(
            final DocumentForFixedAsset document) {
        setDocument(document);
        return this;
    }

    public List<DocumentForFixedAsset> choices0SelectDocument() {
        return documents.findByFixedAssetAndType(getProperty(), DocumentType.INTERACTIVE_MAP);
    }

    // //////////////////////////////////////

    private String representationStr;

    @PropertyLayout(hidden = Where.ANYWHERE)
    public String getRepresentationStr() {
        return representationStr;
    }

    public void setRepresentationStr(String representationStr) {
        this.representationStr = representationStr;
    }

    @MemberOrder(sequence = "3")
    public InteractiveMapForFixedAssetRepresentation getRepresentation() {
        return InteractiveMapForFixedAssetRepresentation.valueOf(representationStr);
    }

    // //////////////////////////////////////

    @MemberOrder(name = "representation", sequence = "1")
    public InteractiveMapForFixedAssetManager selectRepresentation(
            final @ParameterLayout(named = "Representation") InteractiveMapForFixedAssetRepresentation representation) {
        setRepresentationStr(representation.name());
        return this;
    }

    // //////////////////////////////////////

    public InteractiveMap getInteractiveMap() {
        return interactiveMapService.showMap(getProperty(), getDocument(), getRepresentation());
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private InteractiveMapForFixedAssetService interactiveMapService;

    @javax.inject.Inject
    private DocumentsForFixedAsset documents;

}
