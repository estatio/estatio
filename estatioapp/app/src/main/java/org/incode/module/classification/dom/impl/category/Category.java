package org.incode.module.classification.dom.impl.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.util.ObjectContracts;

import org.incode.module.classification.dom.ClassificationModule;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "incodeClassification",
        identityType = IdentityType.DATASTORE
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME) // can just check if has a parent
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByTaxonomy", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.category.Category "
                        + "WHERE taxonomy == :taxonomy "
                        + "ORDER BY fullyQualifiedOrdinal "),
        @javax.jdo.annotations.Query(
                name = "findByTaxonomyAndReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.category.Category "
                        + "WHERE taxonomy == :taxonomy "
                        + "&&    reference == :reference "
                        + "ORDER BY fullyQualifiedOrdinal "),
        @javax.jdo.annotations.Query(
                name = "findByParent", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.category.Category "
                        + "WHERE parent == :parent "
                        + "ORDER BY fullyQualifiedOrdinal "),
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.category.Category "
                        + "WHERE reference == :reference "
                        + "ORDER BY fullyQualifiedOrdinal "),
        @javax.jdo.annotations.Query(
                name = "findByParentAndName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.category.Category "
                        + "WHERE parent == :parent "
                        + "&&    name == :name "
                        + "ORDER BY fullyQualifiedOrdinal "),
        @javax.jdo.annotations.Query(
                name = "findByParentAndReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.classification.dom.impl.category.Category "
                        + "WHERE parent == :parent "
                        + "&&    reference == :reference "
                        + "ORDER BY fullyQualifiedOrdinal "),
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Classification_fullyQualifiedName_UNQ",
                members = { "fullyQualifiedName" }),
        @javax.jdo.annotations.Unique(
                name = "Classification_parent_Name_UNQ",
                members = { "parent", "name" })
})
@DomainObject()
@DomainObjectLayout(
        titleUiEvent = Category.TitleUiEvent.class,
        iconUiEvent = Category.IconUiEvent.class,
        cssClassUiEvent = Category.CssClassUiEvent.class
)
public class Category implements Comparable<Category> {

    //region > ui event classes
    public static class TitleUiEvent extends ClassificationModule.TitleUiEvent<Category> {
    }

    public static class IconUiEvent extends ClassificationModule.IconUiEvent<Category> {
    }

    public static class CssClassUiEvent extends ClassificationModule.CssClassUiEvent<Category> {
    }
    //endregion

    //region > event classes
    public static abstract class PropertyDomainEvent<T> extends ClassificationModule.PropertyDomainEvent<Category, T> {
    }

    public static abstract class CollectionDomainEvent<T> extends ClassificationModule.CollectionDomainEvent<Category, T> {
    }

    public static abstract class ActionDomainEvent extends ClassificationModule.ActionDomainEvent<Category> {
    }
    //endregion

    //region > title, icon, cssClass

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TitleSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Category.TitleUiEvent ev) {
            if (ev.getTitle() != null) {
                return;
            }
            ev.setTitle(titleOf(ev.getSource()));
        }

        private String titleOf(final Category category) {
            return category.getFullyQualifiedName() +
                    (category.getReference() != null
                            ? " [" + category.getReference() + "]"
                            : "");
        }
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class IconSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Category.IconUiEvent ev) {
            if (ev.getIconName() != null) {
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
        public void on(Category.CssClassUiEvent ev) {
            if (ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion

    //region > constructorle
    protected Category(
            final Category parent,
            final String name,
            final String reference,
            final Integer ordinal) {

        setTaxonomy(parent != null ? parent.getTaxonomy() : null);
        setParent(parent);
        setName(name);
        setReference(reference);
        setOrdinal(ordinal != null ? ordinal : 0);

        deriveFullyQualifiedName();
        deriveFullyQualifiedOrdinal();
    }

    public Category refreshDerivedValues() {
        deriveFullyQualifiedName();
        deriveFullyQualifiedOrdinal();
        return this;
    }

    private void deriveFullyQualifiedName() {
        StringBuilder buf = new StringBuilder();
        prependName(this, buf);
        setFullyQualifiedName(buf.toString());
    }

    private static void prependName(Category category, final StringBuilder buf) {
        while (category != null) {
            prependNameOf(category, buf);
            category = category.getParent();
        }
    }

    private static void prependNameOf(final Category category, final StringBuilder buf) {
        if (buf.length() > 0) {
            buf.insert(0, "/");
        }
        buf.insert(0, category.getName());
    }

    private void deriveFullyQualifiedOrdinal() {
        StringBuilder buf = new StringBuilder();
        prependOrdinal(this, buf);
        setFullyQualifiedOrdinal(buf.toString());
    }

    private static void prependOrdinal(Category category, final StringBuilder buf) {
        while (category != null) {
            prependOrdinalOf(category, buf);
            category = category.getParent();
        }
    }

    private static void prependOrdinalOf(final Category category, final StringBuilder buf) {
        if (buf.length() > 0) {
            buf.insert(0, ".");
        }
        buf.insert(0, category.getOrdinal());
    }
    //endregion

    //region > taxonomy (property)
    public static class TaxonomyDomainEvent extends PropertyDomainEvent<Category> {
    }

    @Column(allowsNull = "true", name = "taxonomyId") // conceptually, not-null; however a taxonomy will refer to itself
    @Property(
            domainEvent = TaxonomyDomainEvent.class,
            editing = Editing.DISABLED
    )
    @Getter @Setter
    private Taxonomy taxonomy;
    //endregion

    //region > fullyQualifiedName (derived property, persisted)
    public static class FullyQualifiedNameDomainEvent extends PropertyDomainEvent<String> {
    }

    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = ClassificationModule.JdoColumnLength.CATEGORY_FQNAME)
    @Property(
            domainEvent = FullyQualifiedNameDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String fullyQualifiedName;
    //endregion

    //region > parent (property)
    public static class ParentDomainEvent extends PropertyDomainEvent<Category> {
    }

    @Column(allowsNull = "true", name = "parentId")
    @Property(
            domainEvent = ParentDomainEvent.class,
            editing = Editing.DISABLED
    )
    @Getter @Setter
    private Category parent;

    public boolean hideParent() {
        return getParent() == getTaxonomy();
    }
    //endregion

    //region > name (property)
    public static class NameDomainEvent extends PropertyDomainEvent<String> {
    }

    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = ClassificationModule.JdoColumnLength.CATEGORY_NAME)
    @Property(domainEvent = NameDomainEvent.class)
    private String name;

    public TranslatableString validateName(final String name) {
        if (name == null)
            return null;
        final Category existingCategoryIfAny = categoryRepository.findByParentAndName(getParent(), name);
        return existingCategoryIfAny != null
                ? TranslatableString.tr("A category with name '{name}' already exists (under this parent)", "name", name)
                : null;
    }

    public void modifyName(final String name) {
        setName(name);
        deriveFullyQualifiedName();
        List<Category> childCategories = categoryRepository.findByParentCascade(this);
        for (Category childCategory : childCategories) {
            childCategory.deriveFullyQualifiedName();
        }
    }

    public void clearName() {
        modifyName(null);
    }

    //endregion

    //region > reference (property)
    public static class ReferenceDomainEvent extends PropertyDomainEvent<String> {
    }

    /**
     * Optional reference.
     */
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "true", length = ClassificationModule.JdoColumnLength.CATEGORY_REFERENCE)
    @Property(domainEvent = ReferenceDomainEvent.class)
    private String reference;

    public TranslatableString validateReference(final String reference) {
        if (reference == null)
            return null;
        final Category existingCategoryIfAny = categoryRepository.findByParentAndReference(getParent(), reference);
        return existingCategoryIfAny != null
                ? TranslatableString.tr("A category with reference '{reference}' already exists (under this parent)", "reference", reference)
                : null;
    }

    public void modifyReference(final String reference) {
        setReference(reference);
    }

    //endregion

    //region > fullyQualifiedOrdinal (derived property, persisted)
    public static class FullyQualifiedOrdinalDomainEvent extends PropertyDomainEvent<String> {
    }

    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = ClassificationModule.JdoColumnLength.CATEGORY_FQORDINAL)
    @Property(
            domainEvent = FullyQualifiedOrdinalDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String fullyQualifiedOrdinal;
    //endregion

    //region > ordinal (property)
    public static class OrdinalDomainEvent extends PropertyDomainEvent<Integer> {
    }

    /**
     * Optional ordinal.
     */
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(
            domainEvent = OrdinalDomainEvent.class,
            editing = Editing.ENABLED
    )
    @PropertyLayout(
            named = "(Sorting) ordinal"
    )
    private Integer ordinal;

    public void modifyOrdinal(final Integer ordinal) {
        setOrdinal(ordinal != null ? ordinal : 0);
        deriveFullyQualifiedOrdinal();
        List<Category> childCategories = categoryRepository.findByParentCascade(this);
        for (Category childCategory : childCategories) {
            childCategory.deriveFullyQualifiedOrdinal();
        }
    }

    public void clearOrdinal() {
        modifyOrdinal(null);
    }

    //endregion

    //region > children (property)
    @Persistent(mappedBy = "parent", dependentElement = "false")
    @Collection(editing = Editing.DISABLED)
    @Getter @Setter
    private SortedSet<Category> children = new TreeSet<>();
    //endregion

    //region > addChild (action)

    @Action()
    @ActionLayout(
            cssClassFa = "fa-plus",
            named = "Add"
    )
    @MemberOrder(name = "children", sequence = "1")
    public Category addChild(
            @ParameterLayout(named = "Name")
            final String name,
            @Nullable
            @ParameterLayout(named = "Reference")
            final String reference,
            @Nullable
            @ParameterLayout(named = "(Sorting) ordinal")
            final Integer ordinal) {
        return categoryRepository.createChild(this, name, reference, ordinal);
    }

    public TranslatableString validate0AddChild(final String name) {
        final Optional<Category> any =
                new ArrayList<>(getChildren()).stream().filter(x -> Objects.equals(x.getName(), name)).findAny();
        return any.isPresent()
                ? TranslatableString.tr(
                "There is already a child classification with the name of '{name}'",
                "name", name)
                : null;
    }

    public TranslatableString validate1AddChild(final String reference) {
        if (reference == null) {
            return null;
        }

        final Optional<Category> any =
                new ArrayList<>(getChildren()).stream().filter(x -> Objects.equals(x.getReference(), reference)).findAny();
        return any.isPresent()
                ? TranslatableString.tr(
                "There is already a child classification with the reference of '{reference}'",
                "reference", reference)
                : null;
    }
    // endregion

    //region > removeChild (action)

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(
            cssClassFa = "fa-minus",
            named = "Remove"
    )
    @MemberOrder(name = "children", sequence = "2")
    public Category removeChild(final Category category) {
        categoryRepository.removeCascade(category);
        return this;
    }

    public java.util.Collection<Category> choices0RemoveChild() {
        return getChildren();
    }

    public TranslatableString validateRemoveChild(final Category category) {
        return categoryRepository.validateRemoveCascade(category);
    }

    // endregion

    //region > all (derived collection)
    public static class AllDomainEvent extends CollectionDomainEvent<Category> {
    }

    @javax.jdo.annotations.NotPersistent
    @Collection(notPersisted = true, editing = Editing.DISABLED)
    public List<Category> getAll() {
        return categoryRepository.findByParentCascade(this);
    }
    //endregion

    //region > toString, compareTo

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "fullyQualifiedName");
    }

    @Override
    public int compareTo(final Category other) {
        return ObjectContracts.compare(this, other, "fullyQualifiedName");
    }

    //endregion

    //region > injected services

    @Inject
    protected RepositoryService repositoryService;
    @Inject
    protected CategoryRepository categoryRepository;
    @Inject
    protected ClassificationRepository classificationRepository;

    //endregion

}
