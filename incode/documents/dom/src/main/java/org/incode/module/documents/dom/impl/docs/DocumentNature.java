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
package org.incode.module.documents.dom.impl.docs;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.documents.dom.impl.rendering.Renderer;
import org.incode.module.documents.dom.impl.rendering.RendererFromBytesToBytes;
import org.incode.module.documents.dom.impl.rendering.RendererFromBytesToChars;
import org.incode.module.documents.dom.impl.rendering.RendererFromBytesToCharsWithPreviewToUrl;
import org.incode.module.documents.dom.impl.rendering.RendererFromCharsToBytes;
import org.incode.module.documents.dom.impl.rendering.RendererFromCharsToChars;
import org.incode.module.documents.dom.impl.rendering.RendererFromCharsToBytesWithPreviewToUrl;

public enum DocumentNature {
    CHARACTERS(
                Arrays.asList(RendererFromCharsToChars.class, RendererFromCharsToBytes.class, RendererFromCharsToBytesWithPreviewToUrl.class),
                Arrays.asList(RendererFromBytesToChars.class, RendererFromCharsToChars.class)),
    BYTES(
                Arrays.asList(RendererFromBytesToChars.class, RendererFromBytesToBytes.class, RendererFromBytesToCharsWithPreviewToUrl.class),
                Arrays.asList(RendererFromBytesToBytes.class, RendererFromCharsToBytes.class));

    private final List<Class<? extends Renderer>> inputRenderClasses;
    private final List<Class<? extends Renderer>> outputRenderClasses;

    DocumentNature(
            final List<Class<? extends Renderer>> inputRenderClasses,
            final List<Class<? extends Renderer>> outputRenderClasses) {
        this.inputRenderClasses = inputRenderClasses;
        this.outputRenderClasses = outputRenderClasses;
    }

    @Programmatic
    public boolean canActAsInputTo(final Class<? extends Renderer> candidateClass) {
        return isSubclassOfAny(candidateClass, this.inputRenderClasses);
    }

    @Programmatic
    public boolean canActAsOutputTo(final Class<? extends Renderer> candidateClass) {
        return isSubclassOfAny(candidateClass, this.outputRenderClasses);
    }

    private boolean isSubclassOfAny(
            final Class<? extends Renderer> candidateClass,
            final List<Class<? extends Renderer>> renderClasses) {
        for (Class<? extends Renderer> rendererClass : renderClasses) {
            if (rendererClass.isAssignableFrom(candidateClass)) {
                return true;
            }
        }
        return false;
    }
}
