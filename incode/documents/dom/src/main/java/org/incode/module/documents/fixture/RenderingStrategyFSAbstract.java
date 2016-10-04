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
package org.incode.module.documents.fixture;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.documents.dom.impl.docs.DocumentNature;
import org.incode.module.documents.dom.impl.renderers.Renderer;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategy;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategyRepository;

public abstract class RenderingStrategyFSAbstract extends FixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected RenderingStrategy upsertRenderingStrategy(
            final String reference,
            final String name,
            final DocumentNature inputNature,
            final DocumentNature outputNature,
            final Class<? extends Renderer> rendererClass,
            final ExecutionContext executionContext) {

        RenderingStrategy renderingStrategy = renderingStrategyRepository.findByReference(reference);
        if (renderingStrategy != null) {
            renderingStrategy.setName(name);
            renderingStrategy.setInputNature(inputNature);
            renderingStrategy.setOutputNature(outputNature);
            renderingStrategy.setRendererClassName(rendererClass.getName());
        } else {
            renderingStrategy =
                    renderingStrategyRepository.create(reference, name, inputNature, outputNature, rendererClass);
        }
        return executionContext.addResult(this, renderingStrategy);
    }

    @Inject
    protected RenderingStrategyRepository renderingStrategyRepository;

}
