/*
 *  Copyright 2016 Dan Haywood
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
package org.incode.module.documents.dom.docs;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.documents.dom.rendering.Renderer;
import org.incode.module.documents.dom.rendering.RendererFromBytesToBytes;
import org.incode.module.documents.dom.rendering.RendererFromBytesToChars;
import org.incode.module.documents.dom.rendering.RendererFromBytesToUrl;
import org.incode.module.documents.dom.rendering.RendererFromCharsToBytes;
import org.incode.module.documents.dom.rendering.RendererFromCharsToChars;
import org.incode.module.documents.dom.rendering.RendererFromCharsToUrl;

public enum DocumentNature {
    CHARACTERS(OutputType.TO_CHARS, RendererFromCharsToChars.class, RendererFromCharsToBytes.class, RendererFromCharsToUrl.class),
    BYTES(OutputType.TO_BYTES, RendererFromBytesToChars.class, RendererFromBytesToBytes.class, RendererFromBytesToUrl.class);

    private final OutputType outputType;
    private final List<Class<? extends Renderer>> rendererClasses;

    DocumentNature(final OutputType outputType, final Class<? extends Renderer>... rendererClasses) {
        this.outputType = outputType;
        this.rendererClasses = Arrays.asList(rendererClasses);
    }

    @Programmatic
    public boolean canActAsInputTo(final Class<? extends Renderer> candidateClass) {
        for (Class<? extends Renderer> rendererClass : rendererClasses) {
            if (rendererClass.isAssignableFrom(candidateClass)) {
                return true;
            }
        }
        return false;
    }

    public OutputType getOutputType() {
        return outputType;
    }
}
