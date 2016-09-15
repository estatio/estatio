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
package org.incode.module.docrendering.stringinterpolator.fixture;

import org.incode.module.documents.dom.impl.docs.DocumentNature;
import org.incode.module.documents.fixture.RenderingStrategyFSAbstract;

import org.incode.module.docrendering.stringinterpolator.dom.RendererUsingStringInterpolatorPreviewAndCaptureUrl;

public class RenderingStrategyFSForStringInterpolatorPreviewAndCaptureUrl extends RenderingStrategyFSAbstract {

    public static final String REF = "SIPC";

    @Override
    protected void execute(ExecutionContext executionContext) {
        createRenderingStrategy(
                REF,
                "String interpolate URL for Preview and Capture",
                DocumentNature.CHARACTERS,
                DocumentNature.BYTES,
                RendererUsingStringInterpolatorPreviewAndCaptureUrl.class, executionContext);
    }

}
