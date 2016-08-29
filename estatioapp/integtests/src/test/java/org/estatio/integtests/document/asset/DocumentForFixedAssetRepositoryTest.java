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
package org.estatio.integtests.document.asset;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.document.Document;
import org.estatio.dom.document.DocumentType;
import org.estatio.dom.document.asset.DocumentForFixedAsset;
import org.estatio.dom.document.asset.DocumentForFixedAssetRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.interactivemap.InteractiveMapDocumentForOxf;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DocumentForFixedAssetRepositoryTest extends EstatioIntegrationTest {

    @Inject
    DocumentForFixedAssetRepository documentForFixedAssetRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new InteractiveMapDocumentForOxf());
            }
        });

    }

    public static class AllDocuments extends DocumentForFixedAssetRepositoryTest {

        @Test
        public void allDocuments() throws Exception {
            // given
            // when
            final List<Document> result = documentForFixedAssetRepository.allDocuments();
            // then
            assertThat(result.size(), is(1));
        }
    }

    public static class FindByFixedAsset extends DocumentForFixedAssetRepositoryTest {

        @Inject
        PropertyRepository propertyRepository;

        @Test
        public void findByFixedAsset() throws Exception {
            // given
            final Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            // when
            final List<DocumentForFixedAsset> document = documentForFixedAssetRepository.findByFixedAsset(property);
            // then
            assertThat(document.size(), is(1));
        }
    }

    public static class FindByFixedAssetAndType extends DocumentForFixedAssetRepositoryTest {

        @Inject
        PropertyRepository propertyRepository;

        @Test
        public void findByFixedAsset() throws Exception {
            // given
            final Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            // when
            final List<DocumentForFixedAsset> document = documentForFixedAssetRepository.findByFixedAssetAndType(property, DocumentType.INTERACTIVE_MAP);
            // then
            assertThat(document.size(), is(1));
        }
    }
}