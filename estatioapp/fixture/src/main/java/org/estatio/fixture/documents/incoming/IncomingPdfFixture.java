package org.estatio.fixture.documents.incoming;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.value.Blob;

import org.estatio.capex.dom.documents.DocumentMenu;

public class IncomingPdfFixture extends FixtureScript {


    @Override
    protected void execute(final ExecutionContext executionContext) {

        List<String> resourceNames = Lists.newArrayList(
            "fakeOrder1.pdf",
            "fakeInvoice1.pdf"
        );

        for (String resourceName : resourceNames) {
            final URL url = Resources.getResource(getClass(), resourceName);
            byte[] bytes;
            try {
                bytes = Resources.toByteArray(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            final Blob blob = new Blob(resourceName, "application/pdf", bytes);
            wrap(documentMenu).upload(blob);
        }
    }

    @Inject
    DocumentMenu documentMenu;

}
