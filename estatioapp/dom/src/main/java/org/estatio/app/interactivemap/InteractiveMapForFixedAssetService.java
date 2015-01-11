/*
 *  Copyright 2013~2014 Dan Haywood
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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.services.linking.DeepLinkService;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.document.InteractiveMapDocument;
import org.estatio.dom.document.InteractiveMapDocuments;
import org.isisaddons.wicket.svg.cpt.applib.InteractiveMap;
import org.isisaddons.wicket.svg.cpt.applib.InteractiveMapAttribute;
import org.isisaddons.wicket.svg.cpt.applib.InteractiveMapElement;

@DomainService
public class InteractiveMapForFixedAssetService {

    @ActionSemantics(Of.SAFE)
    @NotInServiceMenu
    public InteractiveMap showMap(Property property, InteractiveMapForFixedAssetRepresentation representation) {

        Map<Color, Integer> colorMap = new HashMap<>();

        InteractiveMapDocument document = documents.findByFixedAsset(property);

        try {
            String svgString = new String(document.getFile().getBytes(), "UTF-8");
            InteractiveMap interactiveMap = new InteractiveMap(svgString);
            for (Unit unit : units.findByProperty(property)) {
                final Color color = representation.getColorService().getColor(unit);
                colorMap = ColorMapHelper.addToMap(colorMap, color);

                // shape
                InteractiveMapElement element = new InteractiveMapElement(unit.getReference());
                //element.addAttribute(new InteractiveMapAttribute("fill", color.getColor()));
                URI link = deepLinkService.deepLinkFor(unit);
                element.addAttribute(new InteractiveMapAttribute("xlink:href", link.toString()));
                interactiveMap.addElement(element);

                // label
                // interactiveMap.addElement(new InteractiveMapElement("label" +
                // i.toString(), unit.getDescription()));

                // sub label
                // interactiveMap.addElement(new
                // InteractiveMapElement("subLabel" + i.toString(),
                // unit.getCategory().name()));

            }

            int legendId = 1;
            for (Color color : ColorMapHelper.sortByValue(colorMap)) {
//                // label
//                interactiveMap.addElement(new InteractiveMapElement(String.format("legend%dText", legendId), color.getLabel()));
//                // color
//                final InteractiveMapElement element = new InteractiveMapElement(String.format("legend%dShape", legendId));
//                element.addAttribute(new InteractiveMapAttribute("fill", color.getColor()));
//                interactiveMap.addElement(element);
//                legendId++;
            }

            return interactiveMap;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public List<InteractiveMapDocument> choices0ShowMap() {
        return documents.allDocuments();
    }

    public boolean hideShowMap(Property property, InteractiveMapForFixedAssetRepresentation representation) {
        InteractiveMapDocument document = documents.findByFixedAsset(property);
        return document == null;
    }

    // //////////////////////////////////////

    @Inject
    private InteractiveMapDocuments documents;

    @Inject
    private Units units;

    @Inject
    private DeepLinkService deepLinkService;
}
