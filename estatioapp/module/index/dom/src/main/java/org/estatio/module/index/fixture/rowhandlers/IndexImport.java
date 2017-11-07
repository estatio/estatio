package org.estatio.module.index.fixture.rowhandlers;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.Importable;
import org.estatio.module.index.dom.IndexRepository;
import org.estatio.module.index.dom.IndexValue;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.IndexImport"
)
public class IndexImport implements Importable, ExcelFixtureRowHandler {

    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private String indexReference;

    @Getter @Setter
    private String indexName;

    @Getter @Setter
    private LocalDate indexBaseStartDate;

    @Getter @Setter
    private BigDecimal indexBaseFactor;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private BigDecimal value;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList();
//    }

    @Programmatic
    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: other import view models have @Action annotation here...  but in any case, is this view model actually ever surfaced in the UI?
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {
        final ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath(getAtPath());
        // Creator and implement IndexCreator, IndexBaseCreator, IndexValueCreator
        final IndexValue indexValue = indexRepository
                .findOrCreateIndex(applicationTenancy, indexReference, indexName)
                .findOrCreateBase(indexBaseStartDate, indexBaseFactor)
                .newIndexValue(startDate, value);
        return Lists.newArrayList(indexValue);
    }

    @Inject
    private IndexRepository indexRepository;

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;

}
