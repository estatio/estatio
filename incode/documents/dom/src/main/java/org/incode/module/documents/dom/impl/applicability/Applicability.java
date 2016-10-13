package org.incode.module.documents.dom.impl.applicability;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.types.FqcnType;

import lombok.Getter;
import lombok.Setter;

/**
 * Indicates whether a domain object('s type) is applicable to a particular {@link DocumentTemplate}, providing the
 * (name of) the {@link Binder} to use to create the data model to feed into that template.
 */
@javax.jdo.annotations.PersistenceCapable(
        schema = "incodeDocuments",
        table = "Applicability",
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
    // none currently
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Applicability_documentTemplate_domainClassName_UNQ",
                members = { "documentTemplate", "domainClassName" } )
})
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        titleUiEvent = Applicability.TitleUiEvent.class,
        iconUiEvent = Applicability.IconUiEvent.class,
        cssClassUiEvent = Applicability.CssClassUiEvent.class
)
public class Applicability implements Comparable<Applicability> {

    //region > ui event classes
    public static class TitleUiEvent extends DocumentsModule.TitleUiEvent<Applicability>{}
    public static class IconUiEvent extends DocumentsModule.IconUiEvent<Applicability>{}
    public static class CssClassUiEvent extends DocumentsModule.CssClassUiEvent<Applicability>{}
    //endregion

    //region > event classes
    public static abstract class PropertyDomainEvent<T> extends DocumentsModule.PropertyDomainEvent<Applicability, T> { }
    public static abstract class CollectionDomainEvent<T> extends DocumentsModule.CollectionDomainEvent<Applicability, T> { }
    public static abstract class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<Applicability> { }
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TitleSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.Applicability$TitleSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(Applicability.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTitle(titleOf(ev.getSource()));
        }
        private String titleOf(final Applicability applicability) {
            final TitleBuffer buf = new TitleBuffer();
            buf.append(simpleNameOf(applicability.getDomainClassName()));
            // can't use titleService.titleOf(...) if using guava, can't call events within events...
            buf.append(applicability.getDocumentTemplate().getName());
            return buf.toString();
        }
        private static String simpleNameOf(final String domainType) {
            int lastDot = domainType.lastIndexOf(".");
            return domainType.substring(lastDot+1);
        }
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class IconSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.Applicability$IconSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(Applicability.IconUiEvent ev) {
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
            return "incodeDocuments.Applicability$CssClassSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(Applicability.CssClassUiEvent ev) {
            if(ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion


    //region > constructor
    Applicability() {
        // for testing only
    }
    public Applicability(final DocumentTemplate documentTemplate, final Class<?> domainClass, final Class<? extends Binder> binderClass) {
        this(documentTemplate, domainClass.getName(), binderClass.getName());
    }

    public Applicability(
            final DocumentTemplate documentTemplate,
            final String domainClassName,
            final String binderClassName) {
        setDocumentTemplate(documentTemplate);
        setDomainClassName(domainClassName);
        setBinderClassName(binderClassName);
    }
    //endregion


    //region > documentTemplate (property)
    public static class DocumentTemplateDomainEvent extends PropertyDomainEvent<DocumentTemplate> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", name = "documentTemplateId")
    @Property(
            domainEvent = DocumentTemplateDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DocumentTemplate documentTemplate;
    // endregion

    //region > domainClassName (property)
    public static class DomainClassNameDomainEvent extends PropertyDomainEvent<String> { }

    /**
     * The class used as the input for the document
     */
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = FqcnType.Meta.MAX_LEN)
    @Property(
            domainEvent = DomainClassNameDomainEvent.class
    )
    private String domainClassName;

    // endregion

    //region > binderClassName (property)
    public static class BinderClassNameDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = FqcnType.Meta.MAX_LEN)
    @Property(
            domainEvent = BinderClassNameDomainEvent.class
    )
    private String binderClassName;
    // endregion


    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "documentTemplate", "domainClassName", "binderClassName");
    }

    @Override
    public int compareTo(final Applicability other) {
        return ObjectContracts.compare(this, other, "documentTemplate", "domainClassName", "binderClassName");
    }

    //endregion

}
