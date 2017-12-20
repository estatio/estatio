package org.incode.platform.dom.communications.integtests.app.services.fakeemail;

import java.util.List;

import javax.activation.DataSource;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Title;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "incodeCommunicationsDemo.EmailMessage",
        editing = Editing.DISABLED
)
@lombok.Data
public class EmailMessage {

    public String getTo1() {
        return getTo() != null && !getTo().isEmpty() ? getTo().get(0) : null;
    }

    private List<String> to;
    @Programmatic
    public List<String> getTo() {
        return to;
    }

    private List<String> cc;
    @Programmatic
    public List<String> getCc() {
        return cc;
    }

    private List<String> bcc;
    @Programmatic
    public List<String> getBcc() {
        return bcc;
    }

    @Title
    private String subject;
    @PropertyLayout(multiLine = 10)
    private String body;

    private DataSource[] attachments;
    @Programmatic
    public DataSource[] getAttachments() {
        return attachments;
    }
    public int getNumAttachments() {
        return attachments != null ? getAttachments().length : 0;
    }

}
