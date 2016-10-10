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
package org.incode.module.docrendering.stringinterpolator.dom;

import java.io.IOException;

import javax.inject.Inject;

import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.documents.dom.impl.renderers.RendererFromCharsToChars;
import org.incode.module.documents.dom.impl.types.DocumentType;

public class RendererUsingStringInterpolator implements RendererFromCharsToChars {

    @Override
    public String renderCharsToChars(
            final DocumentType documentType,
            final String variant,
            final String atPath,
            final long templateVersion,
            final String templateChars,
            final Object dataModel) throws IOException {
        final StringInterpolatorService.Root root = (StringInterpolatorService.Root) dataModel;
        return stringInterpolator.interpolate(root, templateChars);
    }

    @Inject
    StringInterpolatorService stringInterpolator;
}
