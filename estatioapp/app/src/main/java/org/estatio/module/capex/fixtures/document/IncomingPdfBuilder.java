package org.estatio.module.capex.fixtures.document;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.app.DocumentMenu;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class IncomingPdfBuilder
        extends BuilderScriptAbstract<IncomingPdfBuilder> {

    @Getter @Setter
    private Class<?> contextClass;
    @Getter @Setter
    private String resourceName;
    @Getter @Setter
    private String runAs;

    @Getter
    private Document document;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("contextClass", executionContext, Class.class);
        checkParam("resourceName", executionContext, String.class);
        defaultParam("runAs", executionContext, executionContext.getParameter("runAs"));

        final URL url = Resources.getResource(contextClass, resourceName);
        byte[] bytes;
        try {
            bytes = Resources.toByteArray(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Blob blob = new Blob(resourceName, "application/pdf", bytes);
        if(this.runAs != null) {
            document = sudoService.sudo(this.runAs, () -> wrap(documentMenu).upload(blob));
        } else {
            document = wrap(documentMenu).upload(blob);
        }

    }

    @Inject
    DocumentMenu documentMenu;
    @Inject
    SudoService sudoService;

}
