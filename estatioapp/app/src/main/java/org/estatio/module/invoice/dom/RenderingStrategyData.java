/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.module.invoice.dom;

import java.util.Objects;

import org.incode.module.docrendering.freemarker.dom.impl.RendererForFreemarker;
import org.incode.module.docrendering.stringinterpolator.dom.impl.RendererForStringInterpolator;
import org.incode.module.docrendering.stringinterpolator.dom.impl.RendererForStringInterpolatorCaptureUrl;
import org.incode.module.docrendering.stringinterpolator.dom.impl.RendererForStringInterpolatorPreviewAndCaptureUrl;
import org.incode.module.docrendering.xdocgoten.dom.impl.RendererForXDocReportToDocxThenGotenbergToPdf;
import org.incode.module.docrendering.xdocreport.dom.impl.RendererForXDocReportToDocx;
import org.incode.module.docrendering.xdocreport.dom.impl.RendererForXDocReportToPdf;
import org.incode.module.document.dom.impl.docs.DocumentNature;
import org.incode.module.document.dom.impl.renderers.PreviewToUrl;
import org.incode.module.document.dom.impl.renderers.Renderer;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.rendering.RenderingStrategyRepository;

import lombok.Getter;

@Getter
public enum RenderingStrategyData {

    SIPC(
            "String interpolate URL for Preview and Capture",
            DocumentNature.CHARACTERS,
            DocumentNature.BYTES,
            RendererForStringInterpolatorPreviewAndCaptureUrl.class
    ),
    SINC(
            "String interpolate URL for Capture (no preview)",
            DocumentNature.CHARACTERS,
            DocumentNature.BYTES,
            RendererForStringInterpolatorCaptureUrl.class
    ),
    SI(
            "String interpolate",
            DocumentNature.CHARACTERS,
            DocumentNature.CHARACTERS,
            RendererForStringInterpolator.class
    ),
    FMK(
            "RendererForFreemarker Rendering Strategy",
            DocumentNature.CHARACTERS, DocumentNature.CHARACTERS,
            RendererForFreemarker.class
    ),
    XDP(
            "XDocReport to .pdf",
            DocumentNature.BYTES,
            DocumentNature.BYTES,
            RendererForXDocReportToPdf.class
    ),
    XDD(
            "XDocReport to .docx",
            DocumentNature.BYTES,
            DocumentNature.BYTES,
            RendererForXDocReportToDocx.class
    ),
    XGP(
            "XDocReport to .docx, Gotenberg to .pdf",
            DocumentNature.BYTES,
            DocumentNature.BYTES,
            RendererForXDocReportToDocxThenGotenbergToPdf.class
    ),
    ;

    private final String reference;
    private final String name;
    private final DocumentNature inputNature;
    private final DocumentNature outputNature;
    private final Class<? extends Renderer> rendererClass;
    private final boolean previewsToUrl;

    RenderingStrategyData(
            final String name,
            final DocumentNature inputNature,
            final DocumentNature outputNature,
            final Class<? extends Renderer> rendererClass) {
        this.reference = name();
        this.name = name;
        this.inputNature = inputNature;
        this.outputNature = outputNature;
        this.rendererClass = rendererClass;
        this.previewsToUrl =  PreviewToUrl.class.isAssignableFrom(rendererClass);
    }

    public static RenderingStrategyData reverseLookup(final RenderingStrategy rs) {
        for (final RenderingStrategyData renderingStrategyData : RenderingStrategyData.values()) {
            if(Objects.equals(renderingStrategyData.getReference(), rs.getReference())) {
                return renderingStrategyData;
            }
        }
        return null;
    }

    public RenderingStrategy findUsing(final RenderingStrategyRepository repository) {
        return repository.findByReference(getReference());
    }
}
