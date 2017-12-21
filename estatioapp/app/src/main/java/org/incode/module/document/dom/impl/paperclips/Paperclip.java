package org.incode.module.document.dom.impl.paperclips;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.types.BookmarkType;
import org.incode.module.document.dom.types.NameType;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeDocuments"
)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByDocument", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.paperclips.Paperclip "
                        + "WHERE document == :document "
                        + "ORDER BY documentCreatedAt DESC "),
        @javax.jdo.annotations.Query(
                name = "findByAttachedTo", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.paperclips.Paperclip "
                        + "WHERE attachedToStr == :attachedToStr "
                        + "ORDER BY documentCreatedAt DESC "),
        @javax.jdo.annotations.Query(
                name = "findByAttachedToAndRoleName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.paperclips.Paperclip "
                        + "WHERE attachedToStr == :attachedToStr "
                        + "   && roleName      == :roleName "
                        + "ORDER BY documentCreatedAt DESC "),
        @javax.jdo.annotations.Query(
                name = "findByDocumentAndAttachedTo", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.paperclips.Paperclip "
                        + "WHERE document      == :document "
                        + "   && attachedToStr == :attachedToStr "
                        + "ORDER BY documentCreatedAt DESC "),
        @javax.jdo.annotations.Query(
                name = "findByDocumentAndAttachedToAndRoleName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.paperclips.Paperclip "
                        + "WHERE document      == :document "
                        + "   && attachedToStr == :attachedToStr "
                        + "   && roleName      == :roleName "
                        + "ORDER BY documentCreatedAt DESC ")
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "Paperclip_attachedTo_document_idx",
                members = { "attachedToStr", "document" })
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name="Paperclip_document_attachedTo_roleName_idx",
                members = {"document", "attachedToStr", "roleName"})
})
@DomainObject()
@DomainObjectLayout(
        // titleUiEvent = Paperclip.TitleUiEvent.class,
        iconUiEvent = Paperclip.IconUiEvent.class,
        cssClassUiEvent = Paperclip.CssClassUiEvent.class,
        bookmarking = BookmarkPolicy.AS_ROOT
)
public abstract class Paperclip implements Comparable<Paperclip> {

    //region > ui event classes
    // public static class TitleUiEvent extends DocumentModule.TitleUiEvent<Paperclip>{}
    public static class IconUiEvent extends DocumentModule.IconUiEvent<Paperclip>{}
    public static class CssClassUiEvent extends DocumentModule.CssClassUiEvent<Paperclip>{}
    //endregion

    //region > domain events
    public static abstract class PropertyDomainEvent<T> extends DocumentModule.PropertyDomainEvent<Paperclip, T> { }
    public static abstract class CollectionDomainEvent<T> extends DocumentModule.CollectionDomainEvent<Paperclip, T> { }
    public static abstract class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Paperclip> { }
    //endregion


    //region > title, icon, cssClass

    // can't implement as a subscriber because guava doesn't support events within events
    public TranslatableString title() {
        return titleOf(this);
    }
    private TranslatableString titleOf(final Paperclip paperclip) {
        if(paperclip.getRoleName() != null) {
            return TranslatableString.tr("{document} attached to {attachedTo} ({roleName})",
                    "document", titleService.titleOf(paperclip.getDocument()),
                    "attachedTo", titleService.titleOf(paperclip.getAttachedTo()),
                    "roleName", paperclip.getRoleName());
        } else {
            return TranslatableString.tr("{document} attached to {attachedTo} ",
                    "document", titleService.titleOf(paperclip.getDocument()),
                    "attachedTo", titleService.titleOf(paperclip.getAttachedTo()));
        }
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class IconSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.Paperclip$IconSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(Paperclip.IconUiEvent ev) {
            if(ev.getIconName() != null) {
                return;
            }
            ev.setIconName("");
        }
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class CssClassSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.Paperclip$CssClassSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(Paperclip.CssClassUiEvent ev) {
            if(ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion


    //region > attachedToStr (property, hidden)
    @Getter @Setter
    @Column(allowsNull = "false", length = BookmarkType.Meta.MAX_LEN)
    @Property(
            hidden = Where.EVERYWHERE
    )
    private String attachedToStr;
    //endregion

    //region > attachedTo (derived property, hooks)
    /**
     * Polymorphic association to the object providing the paperclipAttachedTo.
     *
     * NB: strictly speaking these should be abstract; this is a workaround (see ISIS-582)
     */
    @NotPersistent
    public Object getAttachedTo() { return null; }
    protected void setAttachedTo(Object object) {}
    //endregion

    //region > document (property)
    public static class DocumentDomainEvent extends PropertyDomainEvent<DocumentAbstract> { }
    @Getter @Setter
    @Column(
            allowsNull = "false",
            name = "documentId"
    )
    @Property(
            domainEvent = DocumentDomainEvent.class
    )
    private DocumentAbstract document;
    //endregion

    //region > roleName (property, optional)
    public static class RoleNameDomainEvent extends Paperclip.PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "true", length = RoleNameType.Meta.MAX_LEN)
    @Property(
            domainEvent = RoleNameDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String roleName;
    //endregion


    //region > documentCreatedAt (hidden property)

    /**
     * Copy of the date/time that the document was created (only populated for Documents, not templates)
     *
     * <p>
     *     Used simply for ordering of {@link Paperclip}s, to locate the &quot;most recent&quot;.
     * </p>
     */
    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(
            hidden = Where.EVERYWHERE
    )
    private DateTime documentCreatedAt;
    //endregion

    //region > documentDate (derived property)

    public static class DocumentDateDomainEvent extends PropertyDomainEvent<LocalDateTime> { }

    /**
     * Either the {@link Document#getCreatedAt()} or {@link Document#getRenderedAt()}, depending upon the
     * {@link Document#getState()} of the {@link Document}.  Returns <tt>null</tt> for {@link DocumentTemplate}s.
     */
    @NotPersistent
    @Property(
            domainEvent = DocumentDateDomainEvent.class,
            editing = Editing.DISABLED
    )
    public DateTime getDocumentDate() {
        final DocumentAbstract documentAbstract = getDocument();
        if(documentAbstract instanceof Document) {
            final Document document = (Document) documentAbstract;
            DocumentState state = document.getState();
            return state.dateOf(document);
        }
        return null;
    }
    //endregion

    //region > documentState (derived property)

    public static class DocumentStateDomainEvent extends PropertyDomainEvent<LocalDateTime> { }

    @NotPersistent
    @Property(
            domainEvent = DocumentStateDomainEvent.class,
            editing = Editing.DISABLED
    )
    public DocumentState getDocumentState() {
        final DocumentAbstract documentAbstract = getDocument();
        if(documentAbstract instanceof Document) {
            final Document document = (Document) documentAbstract;
            return document.getState();
        }
        return null;
    }

    //endregion


    //region > toString, compareTo

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "attachedToStr", "document", "roleName", "documentCreatedAt", "documentDate", "documentState");
    }

    @Override
    public int compareTo(final Paperclip other) {
        return ObjectContracts.compare(this, other, "attachedToStr", "document", "roleName", "documentCreatedAt", "documentDate", "documentState");
    }

    //endregion


    //region > injected services

    @Inject
    TitleService titleService;
    //endregion

    //region > types
    public static class RoleNameType {

        private RoleNameType() {}

        public static class Meta {

            public static final int MAX_LEN = NameType.Meta.MAX_LEN;

            private Meta() {}

        }

    }

    //endregion
}
