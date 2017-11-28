package org.estatio.module.capex.fixtures.document;

import java.util.Arrays;
import java.util.List;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.fixtures.document.enums.IncomingPdf_enum;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class IncomingPdfFixture extends IncomingPdfAbstract {

    @Getter @Setter
    private String runAs;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        final List<IncomingPdf_enum> enums =
                Arrays.asList(IncomingPdf_enum.FakeOrder1, IncomingPdf_enum.FakeInvoice1);

        for (IncomingPdf_enum datum : enums) {
            final Document document = datum.uploadUsing(serviceRegistry, getRunAs(), executionContext);
            executionContext.addResult(this,datum.getResourceName(), document );
        }
    }


}
