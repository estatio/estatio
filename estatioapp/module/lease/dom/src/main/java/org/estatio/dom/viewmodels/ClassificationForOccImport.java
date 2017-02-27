package org.estatio.dom.viewmodels;

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

import org.estatio.dom.Importable;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.OccupancyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.ClassificationForOccImport"
)
public class ClassificationForOccImport implements Importable, ExcelFixtureRowHandler {

    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String unitReference;

    @Getter @Setter
    private String taxonomyReference;

    @Getter @Setter
    private String categoryReference;

    @Getter @Setter
    private LocalDate startDate;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList();
//    }

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        ClassificationForOccImport previousImport = (ClassificationForOccImport) previousRow;
        return importData(previousImport);
    }

    @Override
    public List<Object> importData(final Object previousRow) {

        final Lease lease = leaseRepository.findLeaseByReference(getLeaseReference());
        if (lease == null) {
            throw new IllegalArgumentException(String.format("No lease found for '%s'", getLeaseReference()));
        }

        final Unit unit = unitRepository.findUnitByReference(getUnitReference());
        if (unit == null) {
            throw new IllegalArgumentException(String.format("No unit found for '%s'", getUnitReference()));
        }

        final Occupancy occupancy = occupancyRepository.findByLease(lease).stream().filter(x -> x.getUnit().equals(unit)).findFirst().get();
        if (occupancy == null) {
            throw new IllegalArgumentException(String.format("No occupancy found for lease '%s' and unit '%s'", getLeaseReference(), getUnitReference()));
        }

        final Taxonomy taxonomy = (Taxonomy) categoryRepository.findByReference(getTaxonomyReference());
        if (taxonomy == null) {
            throw new IllegalArgumentException(String.format("No taxonomy found for '%s'", getTaxonomyReference()));
        }
        final Category category = categoryRepository.findByTaxonomyAndReference(taxonomy, getCategoryReference());
        if (category == null) {
            throw new IllegalArgumentException(String.format("No category found for '%s'", getCategoryReference()));
        }

        final Classification classification = classificationRepository.create(category, occupancy);

        return Lists.newArrayList(classification);
    }

    @Inject
    private OccupancyRepository occupancyRepository;

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private UnitRepository unitRepository;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private ClassificationRepository classificationRepository;

}
