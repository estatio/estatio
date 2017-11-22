package org.estatio.module.lease.imports;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.ObjectUtils;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.lease.dom.LeaseType;
import org.estatio.module.lease.dom.LeaseTypeRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.LeaseTypeImport"
)
public class LeaseTypeImport implements ExcelFixtureRowHandler, Importable {

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String atPath;

//    @Override public List<Class> importAfter() {
//        return Lists.newArrayList();
//    }

    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: other import view models have @Action annotation here...  but in any case, is this view model actually ever surfaced in the UI?
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        final ApplicationTenancy appTenancy = applicationTenancyRepository.findByPath(atPath);
        final LeaseType leaseType = leaseTypeRepository.findOrCreate(reference, name, appTenancy);
        if (ObjectUtils.compare(name, leaseType.getName()) != 0) {
            leaseType.setName(name);
        }

        return Lists.newArrayList(leaseType);
    }

    //region > injected services

    @Inject
    private LeaseTypeRepository leaseTypeRepository;

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;

    //endregion

}
