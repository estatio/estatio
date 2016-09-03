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

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.documents.dom.rendering.Renderer;
import org.incode.module.documents.dom.rendering.RendererToBytes;
import org.incode.module.documents.dom.rendering.RendererToChars;

public enum DocumentNature {
    CHARACTERS(RendererToChars.class),
    BYTES(RendererToBytes.class);

    private final Class<? extends Renderer> rendererClass;

    DocumentNature(final Class<? extends Renderer> rendererClass) {
        this.rendererClass = rendererClass;
    }

    // TODO: this logic is broken; what we actually care about (and isn't visible to us) is what the renderer reads from.
    // introduce a marker interface, RendererFromChars ? RendererFromBytes ?

    @Programmatic
    public boolean compatibleWith(final Class<? extends Renderer> candidateClass) {
        return rendererClass.isAssignableFrom(candidateClass);
    }
}
