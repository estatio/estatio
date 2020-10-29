package org.estatio.module.application.imports;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.module.base.dom.Importable;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.lease.dom.occupancy.tags.*;
import org.incode.module.classification.dom.impl.category.Category;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;
import org.incode.module.classification.dom.impl.classification.ClassificationRepository;
import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.IFMClassificationImport"
)
public class IFMClassificationImport implements Importable, ExcelFixtureRowHandler {

    @Getter @Setter
    private String brandName;

    @Getter @Setter
    private String sectorName;

    @Getter @Setter
    private String activityName;

    @Getter @Setter
    private String referenceIFM;

    @Getter @Setter
    private String classificationIFM;

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        IFMClassificationImport previousImport = (IFMClassificationImport) previousRow;
        return importData(previousImport);
    }

    @Override
    public List<Object> importData(final Object previousRow) {

        final Taxonomy taxonomyIFM = (Taxonomy) categoryRepository.findByReference("IFMFR_SECTOR");

        if (taxonomyIFM == null) { throw new RuntimeException("IFM Taxonomy could not be found"); }

        List<Occupancy> occupancies = Lists.newArrayList();

        if (getReferenceIFM() != null && getBrandName() != null && getActivityName() != null) {
            final Category categoryIFM = categoryRepository.findByTaxonomyAndReference(taxonomyIFM, getReferenceIFM());
            if (categoryIFM == null) {
                throw new IllegalArgumentException(String.format("No IFM category with reference '%s' found", getReferenceIFM()));
            }

            final Brand brand = brandRepository.findByName(getBrandName());
            if (brand == null) {
                throw new IllegalArgumentException(String.format("No brand found with name '%s' found", getBrandName()));
            }

            final Sector sector = sectorRepository.findByName(getSectorName());
            if (sector == null) {
                throw new IllegalArgumentException(String.format("No sector found with name '%s'", getSectorName()));
            }

            final Activity activity = activityRepository.findBySectorAndName(sector, getActivityName());
            if (activity == null) {
                throw new IllegalArgumentException(String.format("No activity found with name '%s'", getActivityName()));
            }

            occupancies = occupancyRepository.findByBrand(brand, true).stream()
                    .filter(occupancy -> occupancy.getApplicationTenancy().getPath().startsWith("/FRA") && occupancy.getActivity() == activity)
                    .collect(Collectors.toList());

            // Remove existing IFM classifications and add new one
            occupancies.stream().forEach(occupancy -> {
                classificationRepository.findByClassified(occupancy).stream()
                        .filter(classification -> classification.getTaxonomy() == taxonomyIFM)
                        .forEach(classification -> classificationRepository.remove(classification));

                classificationRepository.create(categoryIFM, occupancy);
            });
        }

        return Lists.newArrayList(occupancies);
    }

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private OccupancyRepository occupancyRepository;

    @Inject
    private BrandRepository brandRepository;

    @Inject
    private ActivityRepository activityRepository;

    @Inject
    private SectorRepository sectorRepository;

    @Inject
    private ClassificationRepository classificationRepository;

}
