package org.estatio.module.asset.imports;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;
import org.incode.module.classification.dom.impl.classification.Classification;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.ClassificationForPropertyImport"
)
public class ClassificationForPropertyImport implements Importable, ExcelFixtureRowHandler {

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private String taxonomyReference;

    @Getter @Setter
    private String categoryReference;

    @Getter @Setter
    private LocalDate startDate;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(TaxonomyImport.class);
//    }

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        ClassificationForPropertyImport previousImport = (ClassificationForPropertyImport) previousRow;
        return importData(previousImport);
    }

    @Override
    public List<Object> importData(final Object previousRow) {

        final Property property = propertyRepository.findPropertyByReference(getPropertyReference());
        if(property == null){
            throw new IllegalArgumentException(String.format("No property found for '%s'",getPropertyReference()));
        }

        final Taxonomy taxonomy = (Taxonomy) categoryRepository.findByReference(getTaxonomyReference());
        if(taxonomy == null){
            throw new IllegalArgumentException(String.format("No taxonomy found for '%s'",getTaxonomyReference()));
        }
        final Category category = categoryRepository.findByTaxonomyAndReference(taxonomy, getCategoryReference());
        if(category == null){
            throw new IllegalArgumentException(String.format("No category found for '%s'",getCategoryReference()));
        }

        final Classification classification = classificationRepository.create(category, property);

        return Lists.newArrayList(classification);
    }

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private ClassificationRepository classificationRepository;

}
