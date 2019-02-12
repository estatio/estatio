package org.incode.module.document.dom.impl.docs;

import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

public interface DocumentLike {

    void setName(String documentName);
    void modifyBlob(Blob blob);
    void setTextData(String name, String mimeType, String renderedChars);
    void modifyClob(Clob clob);

}
