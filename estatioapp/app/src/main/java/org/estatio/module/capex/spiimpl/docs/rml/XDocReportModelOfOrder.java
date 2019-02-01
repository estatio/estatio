package org.estatio.module.capex.spiimpl.docs.rml;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.isisaddons.module.xdocreport.dom.service.XDocReportModel;

import org.incode.module.document.dom.impl.applicability.RendererModelFactoryAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.module.capex.dom.order.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class XDocReportModelOfOrder extends RendererModelFactoryAbstract<Order> {

    public XDocReportModelOfOrder() {
        super(Order.class);
    }

    @Override
    protected Object doNewRendererModel(
            final DocumentTemplate documentTemplate, final Order demoObject) {
        return new DataModel(demoObject);
    }

    @AllArgsConstructor
    public static class DataModel implements XDocReportModel {

        // for freemarker
        @Getter
        private final Order demoObject;

        // for XDocReport
        @Override
        public Map<String, Data> getContextData() {
            return ImmutableMap.of("demoObject", Data.object(demoObject));
        }

    }
}
