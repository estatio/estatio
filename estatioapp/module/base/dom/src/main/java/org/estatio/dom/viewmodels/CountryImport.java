package org.estatio.dom.viewmodels;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.dom.Importable;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.CountryImport"
)
public class CountryImport implements ExcelFixtureRowHandler, Importable {

    @Getter @Setter
    @Property(optionality = Optionality.MANDATORY)
    private String code;

    @Getter @Setter
    @Property(optionality = Optionality.MANDATORY)
    private String alpha2Code;

    @Getter @Setter
    @Property(optionality = Optionality.MANDATORY)
    private String name;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList();
//    }

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        return importData(previousRow);
    }

    @Override
    public List<Object> importData(Object previousRow) {

        Country country = countryRepository.findCountry(code);
        if (country != null) {
            return Lists.newArrayList(country);
        }
        return Lists.newArrayList(countryRepository.createCountry(code, alpha2Code, name));
    }

    @Inject
    private CountryRepository countryRepository;

}