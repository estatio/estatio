package org.incode.module.classification.dom.impl.classification;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Sets;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.registry.ServiceRegistry;

import org.incode.module.classification.dom.ClassificationModule;
import org.incode.module.classification.dom.impl.applicability.Applicability;
import org.incode.module.classification.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;
import org.incode.module.classification.dom.spi.ApplicationTenancyService;

public abstract class T_classify<T> {

    //region > constructor
    private final T classified;

    public T_classify(final T classified) {
        this.classified = classified;
    }

    public T getClassified() {
        return classified;
    }

    //endregion

    //region > classify
    public static class DomainEvent extends ClassificationModule.ActionDomainEvent<T_classify> {
    }

    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-plus",
            contributed = Contributed.AS_ACTION
    )
    @MemberOrder(name = "classifications", sequence = "1")
    public T classify(
            final Taxonomy taxonomy,
            final Category category) {
        classificationRepository.create(category, classified);
        return classified;
    }

    public TranslatableString disableClassify() {
        return choices0Classify().isEmpty() ? TranslatableString.tr("There are no classifications that can be added") : null;
    }

    public Taxonomy default0Classify() {
        Collection<Taxonomy> taxonomies = choices0Classify();
        return taxonomies.size() == 1 ? taxonomies.iterator().next() : null;
    }

    public Collection<Taxonomy> choices0Classify() {
        SortedSet<Applicability> applicableToClassHierarchy = Sets.newTreeSet();

        // pull together all the 'Applicability's for this domain type and all its supertypes.
        String atPath = getAtPath();
        if (atPath == null) {
            return Collections.emptyList();
        }
        
        appendDirectApplicabilities(atPath, classified.getClass(), applicableToClassHierarchy);

        // the obtain the corresponding 'Taxonomy's of each of these
        Set<Taxonomy> taxonomies = Sets.newTreeSet();
        taxonomies.addAll(
                applicableToClassHierarchy.stream()
                        .map(Applicability::getTaxonomy)
                        .distinct()
                        .collect(Collectors.toSet())
        );

        // remove any taxonomies already selected
        T_classifications t_classifications = new T_classifications(classified) {
        };
        serviceRegistry.injectServicesInto(t_classifications);
        final List<Classification> classifications = t_classifications.$$();
        final Set<Taxonomy> existing = classifications.stream().map(Classification::getTaxonomy).collect(Collectors.toSet());
        taxonomies.removeAll(existing);

        return taxonomies;
    }

    String getAtPath() {
        return applicationTenancyRepositories.stream()
                .map(x -> x.atPathFor(classified))
                .filter(x -> x != null)
                .findFirst()
                .orElse(null);
    }

    private void appendDirectApplicabilities(
            final String atPath,
            Class<?> domainType,
            final SortedSet<Applicability> applicabilities) {
        while (domainType != null) {
            appendDirectApplicatiesFor(atPath, domainType, applicabilities);
            domainType = domainType.getSuperclass();
        }
    }

    private void appendDirectApplicatiesFor(
            final String atPath,
            final Class<?> domainType,
            final SortedSet<Applicability> applicabilities) {
        // this query handles fact that atPath is hierarchy.
        List<Applicability> applicabilitiesForDomainType =
                applicabilityRepository.findByDomainTypeAndUnderAtPath(domainType, atPath);
        applicabilities.addAll(applicabilitiesForDomainType);
    }

    public Category default1Classify(final Taxonomy taxonomy) {
        Collection<Category> categories = choices1Classify(taxonomy);
        return categories.size() > 0 ? categories.iterator().next() : null;
    }

    public Collection<Category> choices1Classify(final Taxonomy taxonomy) {
        final List<Category> categories = categoryRepository.findByTaxonomy(taxonomy);
        return categories.stream()
                .filter(x -> x.getParent() != null) // exclude top-level taxonomy
                .collect(Collectors.toList());
    }

    //endregion

    //region  > (injected)
    @Inject
    ApplicabilityRepository applicabilityRepository;
    @Inject
    ClassificationRepository classificationRepository;
    @Inject
    CategoryRepository categoryRepository;
    @Inject
    ServiceRegistry serviceRegistry;
    @Inject
    List<ApplicationTenancyService> applicationTenancyRepositories;
    //endregion

}
