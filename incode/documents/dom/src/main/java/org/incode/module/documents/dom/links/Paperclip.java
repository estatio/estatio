/*
 *  Copyright 2015 incode.org
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.incode.module.documents.dom.links;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;

import com.google.common.base.Function;
import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.docs.Document;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeDocuments",
        table = "Paperclip"
)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByDocument", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.links.Paperclip "
                        + "WHERE document == :document "),
        @javax.jdo.annotations.Query(
                name = "findByAttachedTo", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.links.Paperclip "
                        + "WHERE attachedToStr == :attachedToStr "),
        @javax.jdo.annotations.Query(
                name = "findByAttachedToAndRoleName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.links.Paperclip "
                        + "WHERE attachedToStr == :attachedToStr "
                        + "   && roleName == :roleName ")
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "DocumentLink_attachedTo_document_idx",
                members = { "attachedToStr", "document" }),
        @javax.jdo.annotations.Index(
                name="DocumentLink_document_attachedTo_UNQ",
                members = {"document", "attachedToStr"})
})
@javax.jdo.annotations.Uniques({
    // none currently
})
@DomainObject(
        objectType = "incodeDocuments.Paperclip"
)
@DomainObjectLayout(
        titleUiEvent = Paperclip.TitleUiEvent.class,
        iconUiEvent = Paperclip.IconUiEvent.class,
        cssClassUiEvent = Paperclip.CssClassUiEvent.class,
        bookmarking = BookmarkPolicy.AS_ROOT
)
public abstract class Paperclip implements Comparable<Paperclip> {

    //region > ui event classes
    public static class TitleUiEvent extends DocumentsModule.TitleUiEvent<Paperclip>{}
    public static class IconUiEvent extends DocumentsModule.IconUiEvent<Paperclip>{}
    public static class CssClassUiEvent extends DocumentsModule.CssClassUiEvent<Paperclip>{}
    //endregion

    //region > domain events
    public static abstract class PropertyDomainEvent<T> extends DocumentsModule.PropertyDomainEvent<Paperclip, T> { }
    public static abstract class CollectionDomainEvent<T> extends DocumentsModule.CollectionDomainEvent<Paperclip, T> { }
    public static abstract class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<Paperclip> { }
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TitleSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Paperclip.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTranslatableTitle(titleOf(ev.getSource()));
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
        @Inject
        TitleService titleService;
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService
    public static class IconSubscriber extends AbstractSubscriber {
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
    @DomainService
    public static class CssClassSubscriber extends AbstractSubscriber {
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
    @Column(allowsNull = "false", length = DocumentsModule.JdoColumnLength.BOOKMARK)
    @Property(
            hidden = Where.EVERYWHERE
    )
    private String attachedToStr;
    //endregion

    //region > attachedTo (derived property, hooks)
    /**
     * Polymorphic association to the object providing the attachedTo.
     *
     * NB: strictly speaking these should be abstract; this is a workaround (see ISIS-582)
     */
    @NotPersistent
    public Object getAttachedTo() { return null; }
    protected void setAttachedTo(Object object) {}
    //endregion

    //region > document (property)
    public static class DocumentDomainEvent extends PropertyDomainEvent<Document> { }
    @Getter @Setter
    @Column(
            allowsNull = "false",
            name = "documentId"
    )
    @Property(
            domainEvent = DocumentDomainEvent.class
    )
    private Document document;
    //endregion

    //region > roleName (property, optional)
    public static class RoleNameDomainEvent extends Paperclip.PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "true", length = DocumentsModule.JdoColumnLength.NAME)
    @Property(
            domainEvent = RoleNameDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String roleName;
    //endregion

    //region > documentCreatedAt (property)

    public static class DocumentCreatedAtDomainEvent extends PropertyDomainEvent<LocalDateTime> { }

    /**
     * Copy of the date/time that the document was created.
     */
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = DocumentCreatedAtDomainEvent.class,
            editing = Editing.DISABLED
    )
    private LocalDateTime documentCreatedAt;
    //endregion


    //region > delete (action)
    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Document delete() {
        final Document document = getDocument();
        paperclipRepository.delete(this);
        return document;
    }
    //endregion


    //region > toString, compareTo

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "attachedToStr", "document", "roleName", "createdAt");
    }

    @Override
    public int compareTo(final Paperclip other) {
        return ObjectContracts.compare(this, other, "attachedToStr", "document", "roleName", "createdAt");
    }

    //endregion

    //region > Functions
    public static class Functions {
        public static Function<Paperclip, Document> document() {
            return document(Document.class);
        }
        public static <T extends Document> Function<Paperclip, T> document(Class<T> cls) {
            return input -> (T)input.getDocument();
        }
        public static Function<Paperclip, Object> attachedTo() {
            return attachedTo(Object.class);
        }
        public static <T extends Object> Function<Paperclip, T> attachedTo(final Class<T> cls) {
            return input -> (T)input.getAttachedTo();
        }
    }
    //endregion


    //region > injected services
    @Inject
    PaperclipRepository paperclipRepository;
    //endregion

}
