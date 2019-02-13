package org.incode.module.document.dom.impl.types;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.types.NameType;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeDocuments"
)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@Queries({
        @Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.types.DocumentType "
                        + "WHERE reference == :reference ")
})
@Uniques({
        @Unique(
                name = "DocumentType_reference_IDX",
                members = { "reference" }
        ),
        @Unique(
                name = "DocumentType_name_IDX",
                members = { "name" }
        )
})
@DomainObject(
        editing = Editing.DISABLED,
        bounded = true
)
@DomainObjectLayout(
        titleUiEvent = DocumentType.TitleUiEvent.class,
        iconUiEvent = DocumentType.IconUiEvent.class,
        cssClassUiEvent = DocumentType.CssClassUiEvent.class,
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class DocumentType implements Comparable<DocumentType> {

    //region > ui event classes
    public static class TitleUiEvent extends DocumentModule.TitleUiEvent<DocumentType>{}
    public static class IconUiEvent extends DocumentModule.IconUiEvent<DocumentType>{}
    public static class CssClassUiEvent extends DocumentModule.CssClassUiEvent<DocumentType>{}
    //endregion

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends DocumentModule.PropertyDomainEvent<DocumentType, T> { }
    public static abstract class CollectionDomainEvent<T> extends DocumentModule.CollectionDomainEvent<DocumentType, T> { }
    public static abstract class ActionDomainEvent extends DocumentModule.ActionDomainEvent<DocumentType> { }
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TitleSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.DocumentType$TitleSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(DocumentType.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTranslatableTitle(titleOf(ev.getSource()));
        }
        private TranslatableString titleOf(final DocumentType documentType) {
            return TranslatableString.tr(
                    "[{reference}] {name}",
                        "reference", documentType.getReference(),
                        "name", documentType.getName()
            );
        }
        @Inject
        TitleService titleService;
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class IconSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.DocumentType$IconSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(DocumentType.IconUiEvent ev) {
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
            return "incodeDocuments.DocumentType$CssClassSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(DocumentType.CssClassUiEvent ev) {
            if(ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion


    //region > constructor
    DocumentType() {
        // for testing
    }
    public DocumentType(final String reference, final String name) {
        this.reference = reference;
        this.name = name;
    }
    //endregion

    //region > reference (property)
    public static class ReferenceDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = ReferenceType.Meta.MAX_LEN)
    @Property(
            domainEvent = ReferenceDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String reference;
    //endregion

    //region > name (property)
    public static class NameDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = NameType.Meta.MAX_LEN)
    @Property(
            domainEvent = NameDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String name;
    //endregion




    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "reference", "name");
    }

    @Override
    public int compareTo(final DocumentType other) {
        return ObjectContracts.compare(this, other, "reference");
    }

    //endregion

    //region > injected services
    @Inject
    DocumentTemplateRepository documentTemplateRepository;
    //endregion

    //region > types
    public static class ReferenceType {

        private ReferenceType() {}

        public static class Meta {

            public static final int MAX_LEN = 24;

            private Meta() {}

        }
    }
    //endregion
}
