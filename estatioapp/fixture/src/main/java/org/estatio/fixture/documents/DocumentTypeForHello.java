package org.estatio.fixture.documents;

public class DocumentTypeForHello extends DocumentTypeAbstract {

    public static final String REF = "HELLO";

    @Override
    protected void execute(final ExecutionContext executionContext) {
        createType(REF, "Hello world!", executionContext);
    }
}
