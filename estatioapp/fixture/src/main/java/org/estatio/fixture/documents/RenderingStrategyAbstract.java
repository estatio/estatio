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

import javax.inject.Inject;

import org.incode.module.documents.dom.docs.DocumentNature;
import org.incode.module.documents.dom.rendering.Renderer;
import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.rendering.RenderingStrategyRepository;

import org.estatio.fixture.EstatioFixtureScript;

public abstract class RenderingStrategyAbstract extends EstatioFixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected RenderingStrategy createRenderingStrategy(
            final String reference,
            final String name,
            final DocumentNature documentNature,
            final Class<? extends Renderer> rendererClass,
            final ExecutionContext executionContext) {

        final RenderingStrategy renderingStrategy = renderingStrategyRepository.create(reference, name, documentNature, rendererClass);
        return executionContext.addResult(this, renderingStrategy);
    }

    @Inject
    protected RenderingStrategyRepository renderingStrategyRepository;

}
