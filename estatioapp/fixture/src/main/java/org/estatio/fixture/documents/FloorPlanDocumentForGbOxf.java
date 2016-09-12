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
package org.estatio.fixture.documents;

import java.util.HashMap;

import javax.inject.Inject;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Clob;

import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.docs.DocumentTemplateRepository;
import org.incode.module.documents.dom.links.PaperclipRepository;
import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.types.DocumentType;
import org.incode.module.documents.dom.types.DocumentTypeRepository;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetRepository;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGbOxf;

public class FloorPlanDocumentForGbOxf extends DocumentTemplateAbstract {

    public static final String FILE_SUFFIX = "svg";
    public static final String NAME = PropertyForOxfGb.REF + "." + FILE_SUFFIX;

    @Override
    protected void execute(ExecutionContext executionContext) {

        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new DocumentTypeForFloorPlan());
            executionContext.executeChild(this, new RenderingStrategyForSvg());
            executionContext.executeChild(this, new ApplicationTenancyForGbOxf());
            executionContext.executeChild(this, new PropertyForOxfGb());
        }

        final FixedAsset property =
                fixedAssetRepository.matchAssetsByReferenceOrName(PropertyForOxfGb.REF).get(0);

        final DocumentType documentType =
                documentTypeRepository.findByReference(DocumentTypeForFloorPlan.REF);
        final RenderingStrategy renderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategyForSvg.REF);

        final Clob clob = readSvgResourceAsClob(NAME);

        final DocumentTemplate documentTemplate = createDocumentClobTemplate(documentType, clockService.now(),
                ApplicationTenancyForGbOxf.PATH, clob,
                FILE_SUFFIX, renderingStrategy, executionContext);

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
