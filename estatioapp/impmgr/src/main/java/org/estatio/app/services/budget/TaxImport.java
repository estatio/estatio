package org.estatio.app.services.budget;

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
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRate;
import org.estatio.module.tax.dom.TaxRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
public class TaxImport implements ExcelFixtureRowHandler, Importable {

    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private String externalReference;

    @Getter @Setter
    private BigDecimal ratePercentage;

    @Getter @Setter
    private LocalDate rateStartDate;

    @Getter @Setter
    private String rateExternalReference;

//    @Override
//    public List<Class> importAfter() {
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

        final ApplicationTenancy applicationTenancy = securityApplicationTenancyRepository.findByPath(atPath);

        final Tax tax = taxRepository.findOrCreate(reference, name, applicationTenancy);
        tax.setExternalReference(externalReference);
        tax.setDescription(description);
        final TaxRate rate = tax.newRate(rateStartDate, ratePercentage);
        rate.setExternalReference(rateExternalReference);

        return Lists.newArrayList(tax);
    }

    @Inject
    private ApplicationTenancyRepository securityApplicationTenancyRepository;

    @Inject
    private TaxRepository taxRepository;

}
