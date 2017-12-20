package org.incode.module.classification.dom.impl.category;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;
import org.incode.module.classification.dom.impl.classification.Classification;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Category.class
)
public class CategoryRepository {

    //region > findByTaxonomy (programmatic)
    @Programmatic
    public List<Category> findByTaxonomy(final Taxonomy taxonomy) {
        return repositoryService.allMatches(
                new QueryDefault<>(Category.class,
                        "findByTaxonomy",
                        "taxonomy", taxonomy));
    }
    //endregion

    //region > findByTaxonomy (programmatic)
    @Programmatic
    public Category findByTaxonomyAndReference(final Taxonomy taxonomy, final String reference) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(Category.class,
                        "findByTaxonomyAndReference",
                        "taxonomy", taxonomy,
                        "reference", reference));
    }
    //endregion

    //region > findByParent (programmatic)
    @Programmatic
    public List<Category> findByParent(final Category parent) {
        return repositoryService.allMatches(
                new QueryDefault<>(Category.class,
                        "findByParent",
                        "parent", parent));
    }
    //endregion

    //region > findByReference (programmatic)
    @Programmatic
    public Category findByReference(final String reference) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(Category.class,
                        "findByReference",
                        "reference", reference));
    }
    //endregion

    //region > findByParentCascade (programmatic)
    @Programmatic
    public List<Category> findByParentCascade(Category category) {
        final List<Category> allCategories = Lists.newArrayList();
        append(category, allCategories);
        return allCategories;
    }

    void append(final Category parent, final List<Category> all) {
        final List<Category> children = findByParent(parent);
        all.addAll(children);
        for (Category category : children) {
            append(category, all);
        }
    }

    //endregion

    //region > findByParentAndName (programmatic)
    @Programmatic
    public Category findByParentAndName(final Category parent, final String name) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(Category.class,
                        "findByParentAndName",
                        "parent", parent,
                        "name", name));
    }
    //endregion

    //region > findByParentAndReference (programmatic)
    @Programmatic
    public Category findByParentAndReference(final Category parent, final String reference) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(Category.class,
                        "findByParentAndReference",
                        "parent", parent,
                        "reference", reference));
    }
    //endregion

    //region > createTaxonomy (programmatic)
    @Programmatic
    public Taxonomy createTaxonomy(final String name) {
        final Taxonomy taxonomy = new Taxonomy(name);
        repositoryService.persistAndFlush(taxonomy);
        taxonomy.setTaxonomy(taxonomy);
        return taxonomy;
    }
    //endregion

    //region > createChild (programmatic)
    @Programmatic
    public Category createChild(Category parent, String name, String reference, Integer ordinal) {
        final Category category = new Category(parent, name, reference, ordinal);
        repositoryService.persistAndFlush(category);
        return category;

    }
    //endregion

    //region > removeCascade (programmatic)
    @Programmatic
    public void removeCascade(final Category category) {
        SortedSet<Category> children = category.getChildren();
        for (final Category child : children) {
            removeCascade(child);
        }
        repositoryService.remove(category);
    }
    //endregion

    //region > validateRemoveCascade (programmatic)
    @Programmatic
    public TranslatableString validateRemoveCascade(final Category category) {
        List<Classification> classifications = classificationRepository.findByCategory(category);
        if (!classifications.isEmpty()) {
            return TranslatableString.tr("Child '{child}' is classified by '{object}' and cannot be removed",
                    "child", category.getFullyQualifiedName(), "object", classifications.get(0).getClassified().toString());
        } else {
            SortedSet<Category> children = category.getChildren();
            for (final Category child : children) {
                TranslatableString childValidation = validateRemoveCascade(child);
                if (childValidation != null) {
                    return childValidation;
                }
            }

            return null;
        }
    }
    //endregion

    //region > injected
    @Inject
    RepositoryService repositoryService;
    @Inject
    ClassificationRepository classificationRepository;

    //endregion

}
