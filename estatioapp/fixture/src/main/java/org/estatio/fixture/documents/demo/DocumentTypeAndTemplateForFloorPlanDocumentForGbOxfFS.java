/*
 *  Copyright 2016 Eurocommercial Properties NV
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
package org.estatio.fixture.documents.demo;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.apache.wicket.util.io.IOUtils;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.rendering.RenderingStrategyRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.module.document.fixture.DocumentTemplateFSAbstract;
import org.incode.module.docrendering.freemarker.fixture.RenderingStrategyFSForFreemarker;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.FixedAssetRepository;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForOxfGb;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGbOxf;

public class DocumentTypeAndTemplateForFloorPlanDocumentForGbOxfFS extends DocumentTemplateFSAbstract {

    public static final String TYPE_REF = "FLOOR_PLAN";

    public static final String FILE_SUFFIX = "svg";
    public static final String NAME = PropertyAndOwnerAndManagerForOxfGb.REF + "." + FILE_SUFFIX;

    protected Clob readSvgResourceAsClob(String fileName) {
        try {
            InputStream is = getClass().getResourceAsStream("/svg/" + fileName);
            Clob blob = new Clob(fileName, "image/svg+xml", IOUtils.toCharArray(is));
            is.close();
            return blob;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new RenderingStrategyFSForSvg());
        executionContext.executeChild(this, new ApplicationTenancyForGbOxf());
        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForOxfGb());

        upsertType(TYPE_REF, "Floor plan", executionContext);

        final FixedAsset property =
                fixedAssetRepository.matchAssetsByReferenceOrName(PropertyAndOwnerAndManagerForOxfGb.REF).get(0);

        final DocumentType documentType =
                documentTypeRepository.findByReference(DocumentTypeAndTemplateForFloorPlanDocumentForGbOxfFS.TYPE_REF);
        final RenderingStrategy svgRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategyFSForSvg.REF);
        final RenderingStrategy freemarkerRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategyFSForFreemarker.REF);

        final Clob clob = readSvgResourceAsClob(NAME);

        final DocumentTemplate documentTemplate = upsertDocumentClobTemplate(documentType, clockService.now(),
                ApplicationTenancyForGbOxf.PATH, FILE_SUFFIX,
                false,
                clob, svgRenderingStrategy,
                "Floorplan for ${property.reference}", freemarkerRenderingStrategy,
                executionContext);

        paperclipRepository.attach(documentTemplate, null, property);
    }

    // //////////////////////////////////////

    @Inject
    private DocumentTypeRepository documentTypeRepository;
    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;

    @Inject
    private ClockService clockService;

    @Inject
    private DocumentTemplateRepository documentTemplateRepository;

    @Inject
    private PaperclipRepository paperclipRepository;

    @Inject
    FixedAssetRepository fixedAssetRepository;

}
