package org.incode.module.document.dom.impl.docs;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.DocumentModule;

public interface DocumentLike {

    void setName(String documentName);
    void modifyBlob(Blob blob);
    void setTextData(String name, String mimeType, String renderedChars);
    void modifyClob(Clob clob);

    Blob getBlob();

    DocumentState getState();

    String getMimeType();

    class IconUiEvent extends DocumentModule.IconUiEvent<Document>{}

    @DomainService(nature = NatureOfService.DOMAIN) class IconSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.Document$IconSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(IconUiEvent ev) {
            if(ev.getIconName() != null) {
                return;
            }
            final Document document = ev.getSource();
            final String documentName = document.getName();
            String iconName = "";
            if(documentName.endsWith(".xls") || documentName.endsWith(".xlsx")) {
                iconName = "xlsx";
            }
            if(documentName.endsWith(".doc") || documentName.endsWith(".docx")) {
                iconName = "docx";
            }
            if(documentName.endsWith(".ppt") || documentName.endsWith(".pptx")) {
                iconName = "pptx";
            }
            if(documentName.endsWith(".pdf")) {
                iconName = "pdf";
            }
            if(documentName.endsWith(".html")) {
                iconName = "html";
            }
            ev.setIconName(iconName);
        }
    }
}
