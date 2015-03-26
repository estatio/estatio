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

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.base.Strings;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractService;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.services.linking.DeepLinkService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.svg.cpt.applib.Color;
import org.isisaddons.wicket.svg.cpt.applib.InteractiveMap;
import org.isisaddons.wicket.svg.cpt.applib.InteractiveMapAttribute;
import org.isisaddons.wicket.svg.cpt.applib.InteractiveMapElement;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.document.DocumentType;
import org.estatio.dom.document.asset.DocumentForFixedAsset;
import org.estatio.dom.document.asset.DocumentsForFixedAsset;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Occupancies;
import org.estatio.dom.lease.Occupancy;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class InteractiveMapForFixedAssetService extends AbstractService {

    @Prototype
    // TODO: Work in progress, therefore only accessible in prototype mode
    public InteractiveMapForFixedAssetManager maps(Property property) {
        return new InteractiveMapForFixedAssetManager(property, InteractiveMapForFixedAssetRepresentation.DEFAULT);
    }

    public InteractiveMap showMap(
            final Property property,
            final @ParameterLayout(named = "Document") DocumentForFixedAsset document,
            final @ParameterLayout(named = "Representation") InteractiveMapForFixedAssetRepresentation representation) {
        if (property == null || representation == null || document == null) {
            return null;
        }

        Map<Color, Integer> colorMap = new HashMap<>();

        InteractiveMapForFixedAssetColorService colorService = representation.getColorService();
        getContainer().injectServicesInto(colorService);

        try {
            String svgString = new String(document.getFile().getBytes(), "UTF-8");
            InteractiveMap interactiveMap = new InteractiveMap(svgString);
            interactiveMap.setTitle(document.getName());
            for (Unit unit : units.findByProperty(property)) {

                final Color color = colorService.getColor(unit);

                // shape
                InteractiveMapElement element = new InteractiveMapElement(unit.getReference());
                if (color != null) {
                    colorMap = ColorMapHelper.addToMap(colorMap, color);
                    element.addAttribute(new InteractiveMapAttribute("fill", color.getColor()));
                    element.addAttribute(new InteractiveMapAttribute("class", color.getLabel()));
                }
                String leaseName = getLeaseName(unit);
                if (!Strings.isNullOrEmpty(leaseName)) {
                    interactiveMap.getElementTitle2Id().put(leaseName, unit.getReference());
                }

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

            for (Color color : ColorMapHelper.sortByValue(colorMap)) {
                interactiveMap.addLegendItem(color);
            }

            return interactiveMap;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public DocumentForFixedAsset default1ShowMap(Property fixedAsset) {
        if (choices1ShowMap(fixedAsset).size() == 0) {
            return null;
        }
        return choices1ShowMap(fixedAsset).get(0);
    }

    public List<DocumentForFixedAsset> choices1ShowMap(Property fixedAsset) {
        return documents.findByFixedAssetAndType(fixedAsset, DocumentType.INTERACTIVE_MAP);
    }

    private String getLeaseName(Unit unit) {
        LocalDate today = LocalDate.now();
        List<Occupancy> occupancyList = occupancies.occupancies(unit);
        for (Occupancy occupancy : occupancyList) {
            LocalDate startDate = occupancy.getStartDate();
            LocalDate endDate = occupancy.getEndDate();
            if ((startDate == null || today.isAfter(startDate)) && (endDate == null || today.isBefore(endDate))) {
                Lease lease = occupancy.getLease();
                return lease.getName();
            }
        }
        return null;
    }

    public boolean hideShowMap(
            final Property property,
            final DocumentForFixedAsset document,
            final InteractiveMapForFixedAssetRepresentation representation) {
        return shouldShowSvgActions(property);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @NotInServiceMenu
    public Blob downloadMap(
            final Property property,
            final @ParameterLayout(named = "Document") DocumentForFixedAsset document,
            final @ParameterLayout(named = "Representation") InteractiveMapForFixedAssetRepresentation representation) {
        InteractiveMap interactiveMap = showMap(property, document, representation);
        String svgContent = interactiveMap.parse();
        StringReader input = new StringReader(svgContent);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Transcoder transcoder = new PDFTranscoder();
        try {
            transcoder.transcode(new TranscoderInput(input), new TranscoderOutput(output));
        } catch (TranscoderException tx) {
            throw new RuntimeException("An error occurred while transcoding SVG document '"
                    + interactiveMap.getTitle() + "' to PDF", tx);
        }

        return new Blob(property.getName() + ".pdf", "application/pdf", output.toByteArray());
    }

    public boolean hideDownloadMap(
            final Property property,
            final DocumentForFixedAsset document,
            final InteractiveMapForFixedAssetRepresentation representation) {
        return shouldShowSvgActions(property);
    }

    public DocumentForFixedAsset default1DownloadMap(Property fixedAsset) {
        if (choices1DownloadMap(fixedAsset).size() == 0) {
            return null;
        }
        return choices1DownloadMap(fixedAsset).get(0);
    }

    public List<DocumentForFixedAsset> choices1DownloadMap(Property fixedAsset) {
        return documents.findByFixedAssetAndType(fixedAsset, DocumentType.INTERACTIVE_MAP);
    }

    // //////////////////////////////////////

    private Boolean hasMaps;

    private boolean shouldShowSvgActions(final Property property) {
        // lazy loading...
        if (hasMaps == null) {
            hasMaps = documents.findByFixedAssetAndType(property, null).size() > 0;
        }
        return hasMaps;
    }

    // //////////////////////////////////////

    @Inject
    private DocumentsForFixedAsset documents;

    @Inject
    private Units units;

    @Inject
    private Occupancies occupancies;

    @Inject
    private DeepLinkService deepLinkService;
}
