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
package org.estatio.module.application.integtests.doctemplates;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.incode.module.document.dom.impl.applicability.Applicability;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.impl.applicability.RendererModelFactory;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.rendering.RenderingStrategyRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.application.integtests.ApplicationModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.invoice.dom.DocumentTemplateData;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.RenderingStrategyData;
import org.estatio.module.lease.seed.DocumentTypesAndTemplatesForLeaseFixture;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A temporary test which is to confirm that the code set up on DocumentTypeData is equivalent to the
 * DB seed data for DocumentTemplate/Applicability/RenderingStrategy etc.
 */
public class DocumentTemplateSeedingIntegTest extends ApplicationModuleIntegTestAbstract {

    @Before
    public void setUp() throws Exception {
        //
        // using this date to ensure that prototyping still works (epoch date set back then)
        //
        final LocalDate templateDate = new LocalDate(2012,1,1);

        fixtureScripts.runFixtureScript(new DocumentTypesAndTemplatesForLeaseFixture(templateDate), null);
        fixtureScripts.runFixtureScript(new DocumentTypesAndTemplatesForCapexFixture(templateDate), null);
    }

    @Test
    public void same_number_of_rendering_strategies() throws Exception {
        final List<RenderingStrategy> renderingStrategies = renderingStrategyRepository.allStrategies();
        for (final RenderingStrategy rs : renderingStrategies) {
            final RenderingStrategyData rsd = RenderingStrategyData.reverseLookup(rs);
            assertThat(rsd).isNotNull();
            assertThat(rsd.getReference()).isEqualTo(rs.getReference());
        }
        for (final RenderingStrategyData rsd : RenderingStrategyData.values()) {
            final RenderingStrategy rs = rsd.findUsing(renderingStrategyRepository);
            assertThat(rs).isNotNull();
            assertThat(rsd.getReference()).isEqualTo(rs.getReference());
        }
    }

    @Test
    public void same_number_of_document_types() throws Exception {
        final List<DocumentType> documentTypes = documentTypeRepository.allTypes();
        for (final DocumentType dt : documentTypes) {
            final DocumentTypeData dtd = DocumentTypeData.reverseLookup(dt);
            assertThat(dtd).isNotNull();
            assertThat(dtd.getRef()).isEqualTo(dt.getReference());
        }
        for (final DocumentTypeData dtd : DocumentTypeData.values()) {
            final DocumentType dt = dtd.findUsing(documentTypeRepository);
            assertThat(dt).isNotNull();
            assertThat(dtd.getRef()).isEqualTo(dt.getReference());
        }
    }

    @Test
    public void same_number_of_document_templates() throws Exception {
        final List<DocumentType> documentTypes = documentTypeRepository.allTypes();
        for (final DocumentType dt : documentTypes) {

            final DocumentTypeData dtd = DocumentTypeData.reverseLookup(dt);
            assertThat(dtd).isNotNull();
            assertThat(dtd.getRef()).isEqualTo(dt.getReference());

            for (final Map.Entry<String, DocumentTemplateData> entry : dtd.getDocumentTemplateDataByPath().entrySet()) {

                final String dtmdAtPath = entry.getKey();
                final DocumentTemplateData dtmd = entry.getValue();

                System.out.println(dt.getReference() + ": " + dtmdAtPath);

                // should have exactly one DocTemplate for the specified atPath
                final List<DocumentTemplate> templates = documentTemplateRepository.findByType(dt);
                final List<DocumentTemplate> templatesWithAtPath = templates.stream()
                        .filter(documentTemplate -> Objects.equals(documentTemplate.getAtPath(), dtmdAtPath))
                        .collect(Collectors.toList());
                assertThat(templatesWithAtPath).hasSize(1);
                final DocumentTemplate documentTemplate = templatesWithAtPath.get(0);

                final RenderingStrategyData crsd = dtmd.getContentRenderingStrategy();
                final RenderingStrategy crs = documentTemplate.getContentRenderingStrategy();
                assertThat(crsd.findUsing(renderingStrategyRepository)).isSameAs(crs);

                final RenderingStrategyData nrsd = dtmd.getNameRenderingStrategy();
                final RenderingStrategy nrs = documentTemplate.getNameRenderingStrategy();
                assertThat(nrsd.findUsing(renderingStrategyRepository)).isSameAs(nrs);

                assertThat(dtmd.getNameText()).isEqualTo(documentTemplate.getNameText());

                assertThat(dtmd.isPreviewOnly()).isEqualTo(documentTemplate.isPreviewOnly());
                assertThat(dtmd.getExtension()).isEqualTo(documentTemplate.getFileSuffix());
                assertThat(dtmd.getContentSort()).isSameAs(documentTemplate.getSort());
                assertThat(dtmd.getMimeTypeBase()).isEqualTo(documentTemplate.getMimeType());

                // documentTemplate.getName() =~ dtd.getName() + dtd.getNameSuffixIfAny() + "." + dtd.getExtension()
                assertThat(documentTemplate.getName()).startsWith(dtd.getName());
                if(dtmd.getNameSuffixIfAny() != null) {
                    assertThat(documentTemplate.getName()).contains(dtmd.getNameSuffixIfAny());
                }

                final Class<? extends AttachmentAdvisor> dtdAttachmentAdvisorClass =
                        dtmd.getAttachmentAdvisorClass();
                final Class<? extends RendererModelFactory> dtdRendererModelFactoryClass =
                        dtmd.getRendererModelFactoryClass();

                final Class<?> domainClass = dtmd.getDomainClass();

                final Optional<Applicability> applicabilityIfAny = documentTemplate.applicableTo(domainClass);
                if(applicabilityIfAny.isPresent()) {
                    final Applicability applicability = applicabilityIfAny.get();
                    assertThat(applicability.getAttachmentAdvisorClassName())
                            .isEqualTo(dtdAttachmentAdvisorClass.getName());
                    assertThat(applicability.getRendererModelFactoryClassName())
                            .isEqualTo(dtdRendererModelFactoryClass.getName());

                } else {
                    assertThat(dtdAttachmentAdvisorClass).isNull();
                    assertThat(dtdRendererModelFactoryClass).isNull();
                }
            }
        }
    }

    @Inject
    RenderingStrategyRepository renderingStrategyRepository;

    @Inject
    DocumentTemplateRepository documentTemplateRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

}