package org.incode.module.document.dom.impl.docs.paperclips;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import lombok.Getter;
import lombok.Setter;

/**
 * Provides the ability to attach documents to other documents.
 *
 * <p>
 *     For example, a cover note document can have other documents attached to it.
 * </p>
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE
        , schema = "incodeDocuments"
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject()
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class PaperclipForDocument extends Paperclip {

    //region > attachedDocument (property)
    @Column(
            allowsNull = "false",
            name = "attachedToId"
    )
    @Getter @Setter
    private Document attachedDocument;
    //endregion

    //region > attachedTo (hook, derived)
    @NotPersistent
    @Override
    public Object getAttachedTo() {
        return getAttachedDocument();
    }

    @Override
    protected void setAttachedTo(final Object object) {
        setAttachedDocument((Document) object);
    }
    //endregion

    //region > SubtypeProvider SPI implementation
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SubtypeProvider extends PaperclipRepository.SubtypeProviderAbstract {
        public SubtypeProvider() {
            super(Document.class, PaperclipForDocument.class);
        }
    }
    //endregion

    //region > mixins

    //endregion

}
