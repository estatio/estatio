package org.estatio.fixture.documents.incoming;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class IncomingPdfFixtureForInvoice extends IncomingPdfAbstract {

    @Getter @Setter
    private String runAs;

    public static String resourceName = "fakeInvoice2.pdf";

    @Override
    protected void execute(final ExecutionContext executionContext) {

        List<String> resourceNames = Lists.newArrayList(
                resourceName
        );

        uploadDocuments(resourceNames, getRunAs());
    }


}
