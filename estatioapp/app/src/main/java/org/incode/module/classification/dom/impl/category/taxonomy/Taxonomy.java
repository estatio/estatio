package org.incode.module.classification.dom.impl.category.taxonomy;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.classification.dom.impl.applicability.Applicability;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "incodeClassification"
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@DomainObject(
        editing = Editing.DISABLED
)
public class Taxonomy extends Category {

    //region > constructor
    public Taxonomy(final String name) {
        super(null, name, null, 1);
    }
    //endregion

    //region > applicabilties (collection)
    public static class ApplicabilitiesDomainEvent extends CollectionDomainEvent<Applicability> {
    }

    @javax.jdo.annotations.Persistent(mappedBy = "taxonomy", dependentElement = "true")
    @Collection(
            domainEvent = ApplicabilitiesDomainEvent.class,
            editing = Editing.DISABLED
    )
    @Getter @Setter
    private SortedSet<Applicability> appliesTo = new TreeSet<>();

    //endregion

    //region > applicable (action)
    public static class ApplicableToDomainEvent extends ActionDomainEvent {
    }

    @Action(
            domainEvent = ApplicableToDomainEvent.class
    )
    @ActionLayout(
            cssClassFa = "fa-plus"
    )
    @MemberOrder(name = "appliesTo", sequence = "1")
    public Category applicable(
            @ParameterLayout(named = "Application tenancy")
            final String atPath,
            @ParameterLayout(named = "Domain type")
            final String domainTypeName) {
        Applicability applicability = new Applicability(this, atPath, domainTypeName);
        repositoryService.persistAndFlush(applicability);
        return this;
    }

    public TranslatableString validateApplicable(final String atPath, final String domainTypeName) {

        return isApplicable(atPath, domainTypeName) ? null :
                TranslatableString.tr(
                                "Already applicable for '{atPath}' and '{domainTypeName}'",
                                "atPath", atPath,
                                "domainTypeName", domainTypeName);
    }

    private boolean isApplicable(final String atPath, final String domainTypeName) {
        SortedSet<Applicability> applicabilities = getAppliesTo();
        for (Applicability applicability : applicabilities) {
            if (applicability.getAtPath().equals(atPath) && applicability.getDomainType().equals(domainTypeName)) {
                return false;
            }
        }
        return true;
    }

    private boolean isApplicable_does_not_work(final String atPath, final String domainTypeName) {
        //TODO: We are staring like a rabbit (or deer) in the headlights but have no clue why this doesn't work... Beer anyone?
        return getAppliesTo().stream().noneMatch(applicability -> applicability.getAtPath().equals(atPath) && applicability.getDomainType().equals(domainTypeName));
    }
    //endregion

    //region > notApplicable (action)
    public static class NotApplicableDomainEvent extends ActionDomainEvent {
    }

    @Action(
            domainEvent = NotApplicableDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
    )
    @ActionLayout(
            cssClassFa = "fa-minus"
    )
    @MemberOrder(name = "appliesTo", sequence = "2")
    public Category notApplicable(final Applicability applicability) {
        repositoryService.remove(applicability);
        return this;
    }

    public TranslatableString disableNotApplicable() {
        return choices0NotApplicable().isEmpty() ? TranslatableString.tr("No applicabilities to remove") : null;
    }

    public SortedSet<Applicability> choices0NotApplicable() {
        return getAppliesTo();
    }
    //endregion

    //region > injected
    @Inject
    CategoryRepository categoryRepository;
    //endregion
}

