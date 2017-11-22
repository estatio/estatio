package org.estatio.module.capex.fixtures.document.personas;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class IncomingPdfFixture extends IncomingPdfAbstract {

    @Getter @Setter
    private String runAs;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        List<String> resourceNames = Lists.newArrayList(
            "fakeOrder1.pdf",
            "fakeInvoice1.pdf"
        );

        uploadDocuments(resourceNames, getRunAs());
    }


}
