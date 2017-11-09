package org.estatio.module.application.imports;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;

import org.estatio.module.base.dom.Importable;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.TaxonomyImport"
)
public class TaxonomyImport implements Importable, ExcelFixtureRowHandler {

    @Getter @Setter
    private String taxonomyReference;

    @Getter @Setter
    private String taxonomyName;

    @Getter @Setter
    private Integer taxonomyOrdinal;

    @Getter @Setter
    private String applicabilityAtPath;

    @Getter @Setter
    private String applicabilityDomainType;

    @Getter @Setter
    private String categoryReference;

    @Getter @Setter
    private String categoryName;

    @Getter @Setter
    private Integer categoryOrdinal;

    @Getter @Setter
    private String parentCategoryReference;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList();
//    }

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        TaxonomyImport previousImport = (TaxonomyImport) previousRow;
        return importData(previousImport);
    }

    @Override
    public List<Object> importData(final Object previousRow) {

        final TaxonomyImport previousTaxonomyImport = (TaxonomyImport) previousRow;

        final List<Object> createdTaxonomiesAndCategories = Lists.newArrayList();

        if (getTaxonomyReference() == null) {
            final String previousReference = previousTaxonomyImport.getTaxonomyReference();
            if (previousReference == null) {
                throw new IllegalArgumentException("Taxonomy reference is null and previous row's taxonomy reference also null");
            }
            setTaxonomyReference(previousReference);
        }

        final Taxonomy taxonomy;
        Category taxonomyAsCategory = categoryRepository.findByReference(getTaxonomyReference());
        if (taxonomyAsCategory != null) {
            if (!(taxonomyAsCategory instanceof Taxonomy)) {
                throw new IllegalArgumentException(
                        String.format("Ref: '%s' is a reference to a Category, not a Taxonomy", getTaxonomyReference()));
            } else {
                taxonomy = (Taxonomy) taxonomyAsCategory;
            }
        } else {
            taxonomy = categoryRepository.createTaxonomy(getTaxonomyName());
            taxonomy.setReference(getTaxonomyReference());
            taxonomy.setOrdinal(getTaxonomyOrdinal());

            createdTaxonomiesAndCategories.add(taxonomy);
        }

        if (getApplicabilityAtPath() != null) {
            final ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath(getApplicabilityAtPath());
            if (applicationTenancy == null) {
                throw new IllegalArgumentException(
                        String.format("No such application tenancy with atPath '%s'", getApplicabilityAtPath()));
            }

            if (getApplicabilityDomainType() != null) {
                final TranslatableString whetherApplicable = taxonomy
                        .validateApplicable(getApplicabilityAtPath(), getApplicabilityDomainType());
                if (whetherApplicable == null) {
                    taxonomy.applicable(getApplicabilityAtPath(), getApplicabilityDomainType());
                    taxonomy.getAppliesTo();
                }
            }
        }

        // Get Parent category
        Optional<Category> parentCategory = Optional.empty();

        if (getParentCategoryReference() != null && getParentCategoryReference().length() > 0) {
            parentCategory = Optional.ofNullable(categoryRepository.findByTaxonomyAndReference(taxonomy, getParentCategoryReference()));
            if (!parentCategory.isPresent()) {
                throw new IllegalArgumentException(
                        String.format("No category with reference '%s' found", getParentCategoryReference()));
            }
        }

        //
        if (getCategoryReference() != null) {
            final Category parent = parentCategory.isPresent() ? parentCategory.get() : taxonomy;
            final Optional<Category> categoryIfAny = Optional.ofNullable(categoryRepository.findByTaxonomyAndReference(taxonomy, getCategoryReference()));
            if (!categoryIfAny.isPresent()) {
                    createdTaxonomiesAndCategories.add(
                            parent.addChild(getCategoryName(), getCategoryReference(), getCategoryOrdinal()));
            } else {
                // update existing
                final Category category = categoryIfAny.get();
                category.setName(getCategoryName());
                category.setParent(parent);
            }
        }
        return createdTaxonomiesAndCategories;
    }

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;

}
