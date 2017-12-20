package org.incode.module.document.dom.impl.docs;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.renderers.Renderer;
import org.incode.module.document.dom.impl.renderers.RendererFromBytesToBytes;
import org.incode.module.document.dom.impl.renderers.RendererFromBytesToChars;
import org.incode.module.document.dom.impl.renderers.RendererFromBytesToCharsWithPreviewToUrl;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToBytes;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToChars;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToBytesWithPreviewToUrl;

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
