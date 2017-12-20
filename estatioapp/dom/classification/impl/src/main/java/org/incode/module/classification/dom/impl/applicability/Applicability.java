package org.incode.module.classification.dom.impl.applicability;

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
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

import org.incode.module.classification.dom.ClassificationModule;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;

import lombok.Getter;
import lombok.Setter;

/**
 * Indicates whether a domain object('s type) is applicable to a particular {@link Category}, with respect to
 * the application tenancy of that domain object.
 */
@javax.jdo.annotations.PersistenceCapable(
        schema = "incodeClassification",
        table = "Applicability",
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByDomainTypeAndUnderAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.applicability.Applicability "
                        + "WHERE domainType == :domainType "
                        + "&&    :atPath.startsWith(atPath) "
                        + "ORDER BY taxonomy, atPath ")
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Applicability_domainType_atPath_taxonomy_UNQ",
                members = { "domainType", "atPath", "taxonomy" } )
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
    public static class TitleUiEvent extends ClassificationModule.TitleUiEvent<Applicability>{}
    public static class IconUiEvent extends ClassificationModule.IconUiEvent<Applicability>{}
    public static class CssClassUiEvent extends ClassificationModule.CssClassUiEvent<Applicability>{}
    //endregion

    //region > event classes
    public static abstract class PropertyDomainEvent<T> extends ClassificationModule.PropertyDomainEvent<Applicability, T> { }
    public static abstract class CollectionDomainEvent<T> extends ClassificationModule.CollectionDomainEvent<Applicability, T> { }
    public static abstract class ActionDomainEvent extends ClassificationModule.ActionDomainEvent<Applicability> { }
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TitleSubscriber extends AbstractSubscriber {
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
            buf.append(simpleNameOf(applicability.getDomainType()));
            buf.append(" [");
            buf.append(applicability.getAtPath());
            buf.append("]: ");
            // can't use titleService.titleOf(...) if using guava, can't call events within events...
            buf.append(applicability.getTaxonomy().getName());
            return buf.toString();
        }
        private static String simpleNameOf(final String domainType) {
            int lastDot = domainType.lastIndexOf(".");
            return domainType.substring(lastDot+1);
        }
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class IconSubscriber extends AbstractSubscriber {
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
    public Applicability(final Taxonomy taxonomy, final String atPath, final Class<?> domainType) {
        this(taxonomy, atPath, domainType.getName());
    }

    public Applicability(final Taxonomy taxonomy, final String atPath, final String domainTypeName) {
        setAtPath(atPath);
        setTaxonomy(taxonomy);
        setDomainType(domainTypeName);
    }
    //endregion


    //region > taxonomy (property)
    public static class TaxonomyDomainEvent extends PropertyDomainEvent<Taxonomy> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", name = "taxonomyId")
    @Property(
            domainEvent = TaxonomyDomainEvent.class,
            editing = Editing.DISABLED
    )
    private Taxonomy taxonomy;
    // endregion

    //region > atPath (property)

    public static class AtPathDomainEvent extends Applicability.PropertyDomainEvent<String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = ClassificationModule.JdoColumnLength.AT_PATH)
    @Property(
            domainEvent = AtPathDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            named = "Application tenancy"
    )
    private String atPath;

    // endregion

    //region > domainType (property)

    public static class DomainTypeDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = ClassificationModule.JdoColumnLength.APPLICABILITY_DOMAIN_TYPE)
    @Property(
            domainEvent = DomainTypeDomainEvent.class
    )
    private String domainType;

    // endregion


    //region > toString, compareTo

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "taxonomy", "atPath", "domainType");
    }

    @Override
    public int compareTo(final Applicability other) {
        return ObjectContracts.compare(this, other, "taxonomy", "atPath", "domainType");
    }

    //endregion

}
