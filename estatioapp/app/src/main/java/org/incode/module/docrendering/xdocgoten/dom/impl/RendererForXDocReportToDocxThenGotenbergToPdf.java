package org.incode.module.docrendering.xdocgoten.dom.impl;

import org.incode.module.docrendering.gotenberg.dom.impl.RendererForGotenbergDocxToPdfAbstract;
import org.incode.module.docrendering.xdocreport.dom.impl.RendererForXDocReportToDocx;

public class RendererForXDocReportToDocxThenGotenbergToPdf extends RendererForGotenbergDocxToPdfAbstract {

    public RendererForXDocReportToDocxThenGotenbergToPdf() {
        super(new RendererForXDocReportToDocx());
    }

}
