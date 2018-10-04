package org.incode.module.document.dom.impl.docs;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

@DomainObject(
        objectType = "org.incode.module.document.dom.impl.docs.DocumentTemplateForTesting",
        editing = Editing.DISABLED
)
public class DocumentTemplateForTesting extends DocumentTemplate {
}
