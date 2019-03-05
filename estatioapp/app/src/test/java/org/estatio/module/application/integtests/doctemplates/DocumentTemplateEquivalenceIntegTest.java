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
public class DocumentTemplateEquivalenceIntegTest extends ApplicationModuleIntegTestAbstract {

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
    public void rendering_strategies_equivalent() throws Exception {
        final List<RenderingStrategy> renderingStrategies = renderingStrategyRepository.allStrategies();
        for (final RenderingStrategy rs : renderingStrategies) {
            final RenderingStrategyData rsd = RenderingStrategyData.reverseLookup(rs);
            assertThat(rsd).isNotNull();
            assertEquivalent(rsd, rs);
        }
        for (final RenderingStrategyData rsd : RenderingStrategyData.values()) {
            final RenderingStrategy rs = rsd.findUsing(renderingStrategyRepository);
            assertThat(rs).isNotNull();
            assertEquivalent(rsd, rs);
        }
    }

    private void assertEquivalent(final RenderingStrategyData rsd, final RenderingStrategy rs) {
        assertThat(rsd.getReference()).isEqualTo(rs.getReference());
        assertThat(rsd.getInputNature()).isEqualTo(rs.getInputNature());
        assertThat(rsd.getOutputNature()).isEqualTo(rs.getOutputNature());
        assertThat(rsd.getName()).isEqualTo(rs.getName());
        assertThat(rsd.getRendererClass().getName()).isEqualTo(rs.getRendererClassName());
        assertThat(rsd.isPreviewsToUrl()).isEqualTo(rs.isPreviewsToUrl());
    }

    @Test
    public void document_types_equivalent() throws Exception {
        final List<DocumentType> documentTypes = documentTypeRepository.allTypes();
        for (final DocumentType dt : documentTypes) {
            final DocumentTypeData dtd = DocumentTypeData.reverseLookup(dt);
            assertThat((Object)dtd).isNotNull();
            assertThat(dtd.getRef()).isEqualTo(dt.getReference());
        }
        for (final DocumentTypeData dtd : DocumentTypeData.values()) {
            final DocumentType dt = dtd.findUsing(documentTypeRepository);
            assertThat(dt).isNotNull();
            assertThat(dtd.getRef()).isEqualTo(dt.getReference());
        }
    }

    @Test
    public void document_templates_equivalent() throws Exception {

        final List<DocumentType> documentTypes = documentTypeRepository.allTypes();
        for (final DocumentType dt : documentTypes) {

            final DocumentTypeData dtd = DocumentTypeData.reverseLookup(dt);
            assertThat((Object)dtd).isNotNull();
            assertThat(dtd.getRef()).isEqualTo(dt.getReference());

            for (final Map.Entry<String, DocumentTemplateData> entry : dtd.templateIterable()) {

                final String dtmdAtPath = entry.getKey();
                final DocumentTemplateData dtmd = entry.getValue();

                System.out.println(dt.getReference() + ": " + dtmdAtPath);

                // should have exactly one DocTemplate for the specified atPath
                final List<DocumentTemplate> templates = documentTemplateRepository.findByType(dt);
                final List<DocumentTemplate> templatesWithAtPath = templates.stream()
                        .filter(documentTemplate -> Objects.equals(documentTemplate.getAtPath(), dtmdAtPath))
                        .collect(Collectors.toList());
                assertThat(templatesWithAtPath).hasSize(1);
                final DocumentTemplate dtm = templatesWithAtPath.get(0);

                assertEquivalent(dtd, dtmd, dtm);
            }
        }

        for (final DocumentTemplate dtm : documentTemplateRepository.allTemplates()) {
            final DocumentTemplateData dtmd = dtm.getTemplateData();
            assertThat(dtmd).isNotNull();

            final DocumentType dt = dtm.getTypeCopy();
            final DocumentTypeData dtd = DocumentTypeData.reverseLookup(dt);
            assertThat(dtd).isNotNull();

            assertEquivalent(dtd, dtmd, dtm);
        }
    }

    private void assertEquivalent(
            final DocumentTypeData dtd,
            final DocumentTemplateData dtmd,
            final DocumentTemplate dtm) throws ClassNotFoundException {

        final RenderingStrategyData crsd = dtmd.getContentRenderingStrategy();
        final RenderingStrategy crs = dtm.getContentRenderingStrategy();

        assertThat(crsd.findUsing(renderingStrategyRepository)).isSameAs(crs);

        final RenderingStrategyData nrsd = dtmd.getNameRenderingStrategy();
        final RenderingStrategy nrs = dtm.getNameRenderingStrategy();
        assertThat(nrsd.findUsing(renderingStrategyRepository)).isSameAs(nrs);

        assertThat(dtmd.getNameText()).isEqualTo(dtm.getNameText());

        assertThat(dtmd.isPreviewOnly()).isEqualTo(dtm.isPreviewOnly());
        assertThat(dtmd.getExtension()).isEqualTo(dtm.getFileSuffix());
        assertThat(dtmd.getContentSort()).isSameAs(dtm.getSort());
        assertThat(dtmd.getMimeTypeBase()).isEqualTo(dtm.getMimeType());

        // documentTemplate.getName() =~ dtd.getName() + dtd.getNameSuffixIfAny() + "." + dtd.getExtension()
        assertThat(dtm.getName()).startsWith(dtd.getName());
        if(dtmd.getNameSuffixIfAny() != null) {
            assertThat(dtm.getName()).contains(dtmd.getNameSuffixIfAny());
        }

//        final Class<? extends AttachmentAdvisor> dtdAttachmentAdvisorClass =
//                dtmd.getAttachmentAdvisorClass();
//        final Class<? extends RendererModelFactory> dtdRendererModelFactoryClass =
//                dtmd.getRendererModelFactoryClass();
//
//        final Class<?> domainClass = dtmd.getDomainClass();

        for (final Applicability applicability : dtm.getAppliesTo()) {
            final String dtmDomainClassName = applicability.getDomainClassName();

            final Class<?> domainClass = Thread.currentThread().getContextClassLoader().loadClass(dtmDomainClassName);

            final Optional<Applicability> applicabilityIfAny = dtm.applicableTo(domainClass);
            assertThat(applicabilityIfAny).isPresent();
            assertThat(applicabilityIfAny.get()).isSameAs(applicability);

            final Class<? extends AttachmentAdvisor> dtmdAttachmentAdvisorClass = dtmd.attachmentAdvisorClassFor(domainClass);
            assertThat(applicability.getAttachmentAdvisorClassName()).isEqualTo(dtmdAttachmentAdvisorClass.getName());

            final Class<? extends RendererModelFactory> dtmdRendererModelFactoryClass = dtmd.rendererModelFactoryClassFor(domainClass);
            assertThat(applicability.getRendererModelFactoryClassName()).isEqualTo(dtmdRendererModelFactoryClass.getName());

            // check it isn't harded, by passing in some other type
            assertThat(dtmd.attachmentAdvisorClassFor(String.class)).isNull();
            assertThat(dtmd.rendererModelFactoryClassFor(String.class)).isNull();
        }

    }

    @Inject
    RenderingStrategyRepository renderingStrategyRepository;

    @Inject
    DocumentTemplateRepository documentTemplateRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

}