package org.incode.module.classification.dom.impl.classification;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import org.incode.module.classification.dom.ClassificationModule;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeClassification",
        table = "Classification"
)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByTaxonomy", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.classification.Classification "
                        + "WHERE taxonomy == :taxonomy "
                        + "ORDER BY classifiedStr"),
        @javax.jdo.annotations.Query(
                name = "findByClassified", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.classification.Classification "
                        + "WHERE classifiedStr == :classifiedStr "
                        + "ORDER BY taxonomy, category"),
        @javax.jdo.annotations.Query(
                name = "findByCategory", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.classification.Classification "
                        + "WHERE category == :category "
                        + "ORDER BY taxonomy, category")
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "ClassificationLink_category_classified_IDX",
                members = { "category", "classifiedStr" }
        ),
        @javax.jdo.annotations.Index(
                name = "ClassificationLink_category_classified_IDX",
                members = { "taxonomy", "classifiedStr" }
        )
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "ClassificationLink_classified_category_UNQ",
                members = { "classifiedStr", "taxonomy" }
        )
})
@DomainObject(
        objectType = "incodeClassification.Classification"
)
@DomainObjectLayout(
        titleUiEvent = Classification.TitleUiEvent.class,
        iconUiEvent = Classification.IconUiEvent.class,
        cssClassUiEvent = Classification.CssClassUiEvent.class
)
public abstract class Classification implements Comparable<Classification> {

    //region > ui event classes
    public static class TitleUiEvent extends ClassificationModule.TitleUiEvent<Classification>{}
    public static class IconUiEvent extends ClassificationModule.IconUiEvent<Classification>{}
    public static class CssClassUiEvent extends ClassificationModule.CssClassUiEvent<Classification>{}
    //endregion

    //region > event classes
    public static abstract class PropertyDomainEvent<T> extends ClassificationModule.PropertyDomainEvent<Classification, T> { }
    public static abstract class CollectionDomainEvent<T> extends ClassificationModule.CollectionDomainEvent<Classification, T> { }
    public static abstract class ActionDomainEvent extends ClassificationModule.ActionDomainEvent<Classification> { }
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TitleSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Classification.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTitle(titleOf(ev.getSource()));
        }
        private String titleOf(final Classification classification) {
            return String.format("%s: %s",
                            titleService.titleOf(classification.getClassified()),
                            // hmm; this fails if using guava, can't call events within events...
                            // titleService.titleOf(classification.getCategory()));
                            classification.getCategory().getFullyQualifiedName());
        }
        @Inject
        TitleService titleService;
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class IconSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Classification.IconUiEvent ev) {
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
        public void on(Classification.CssClassUiEvent ev) {
            if(ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion


    //region > classifiedStr (property)
    public static class ClassifiedStrDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = ClassificationModule.JdoColumnLength.BOOKMARK)
    @Property(
            domainEvent = ClassifiedStrDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String classifiedStr;
    //endregion

    //region > classified (derived property, hooks)
    /**
     * Polymorphic association to the classified object.
     */
    @Programmatic
    public abstract Object getClassified();
    protected abstract void setClassified(Object object);
    //endregion


    //region > taxonomy (property, derived but persisted)
    public static class TaxonomyDomainEvent extends PropertyDomainEvent<Taxonomy> { }

    /**
     * The owning {@link Taxonomy} of the {@link #getCategory() category}.
     */
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", name = "taxonomyId")
    @Property(
            domainEvent = TaxonomyDomainEvent.class,
            editing = Editing.DISABLED
    )
    private Taxonomy taxonomy;
    //endregion

    //region > category (property)
    public static class CategoryDomainEvent extends PropertyDomainEvent<Category> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", name = "categoryId")
    @Property(
            domainEvent = CategoryDomainEvent.class,
            editing = Editing.ENABLED
    )
    private Category category;

    public List<Category> choicesCategory() {
        final List<Category> categories = categoryRepository.findByTaxonomy(getTaxonomy());
        return categories.stream()
                .filter(x -> x.getParent() != null) // exclude top-level taxonomy
                .collect(Collectors.toList());
    }

    //endregion


    //region > remove (action)
    public static class RemoveDomainEvent extends ActionDomainEvent { }
    @Action(
            domainEvent = RemoveDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
    )
    @ActionLayout(
            cssClass = "btn-warning",
            cssClassFa = "trash"
    )
    public Object remove() {
        final Object aliased = getClassified();
        classificationRepository.remove(this);
        return aliased;
    }

    //endregion

    //region > toString, compareTo

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "classifiedStr", "category");
    }

    @Override
    public int compareTo(final Classification other) {
        return ObjectContracts.compare(this, other, "classifiedStr", "category");
    }

    //endregion

    //region > injected services
    @Inject
    ClassificationRepository classificationRepository;
    @Inject
    CategoryRepository categoryRepository;
    //endregion

}
