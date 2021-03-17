package org.incode.module.document.dom.mixins;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.docs.DocumentLike;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.types.DocumentType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        objectType = "org.incode.module.document.dom.mixins.DocumentPreview",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        iconUiEvent = DocumentLike.IconUiEvent.class
)
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public abstract class DocumentPreview<T> implements DocumentLike {

    @Getter @Setter
    private DocumentSort sort;

    @Getter @Setter
    private DocumentType type;

    @Getter @Setter
    private DocumentState state;

    @Title
    @Getter @Setter
    private String name;

    @Getter @Setter
    private String mimeType;

    @Getter @Setter
    @Property(
            notPersisted = true, // exclude from auditing
            hidden = Where.EVERYWHERE
    )
    private byte[] blobBytes;

    @XmlTransient
    public abstract T getDomainObject();
    public abstract void setDomainObject(final T domainObject);

    //region > blob (derived property)
    @Property(notPersisted = true)
    public Blob getBlob() {
        // TODO: is this guard necessary?
        if (hideBlob()) {
            return null;
        }

        return new Blob(getName(), getMimeType(), getBlobBytes());
    }
    @Programmatic
    public void modifyBlob(Blob blob) {
        setName(blob.getName());
        setMimeType(blob.getMimeType().toString());
        setBlobBytes(blob.getBytes());
        setSort(DocumentSort.BLOB);
    }
    public boolean hideBlob() {
        return !getSort().isBytes();
    }
    //endregion

    //region > clobChars (persisted property, hidden)
    @Getter @Setter
    @Property(
            notPersisted = true, // exclude from auditing
            hidden = Where.EVERYWHERE
    )
    private String clobChars;
    //endregion

    //region > clob (derived property)
    @Property(notPersisted = true)
    public Clob getClob() {
        // TODO: is this guard necessary?
        if (hideClob()) {
            return null;
        }
        return new Clob(getName(), getMimeType(), getClobChars());
    }
    @Programmatic
    public void modifyClob(Clob clob) {
        setName(clob.getName());
        setMimeType(clob.getMimeType().toString());
        setClobChars(clob.getChars().toString());
        setSort(DocumentSort.CLOB);
    }

    public boolean hideClob() {
        return !getSort().isCharacters();
    }
    //endregion

    //region > text (persisted property)
    @Getter @Setter
    @Property(notPersisted = true)
    private String text;

    @Override
    @Programmatic
    public void setTextData(String name, String mimeType, String text) {
        setName(name);
        setMimeType(mimeType);
        setText(text);
        setSort(DocumentSort.TEXT);
    }

    public boolean hideText() {
        return getSort() != DocumentSort.TEXT;
    }
    //endregion

}
