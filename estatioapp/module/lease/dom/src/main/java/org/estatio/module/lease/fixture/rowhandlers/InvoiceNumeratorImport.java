package org.estatio.module.lease.fixture.rowhandlers;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.Importable;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.invoice.Constants;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.InvoiceNumeratorImport"
)
public class InvoiceNumeratorImport implements ExcelFixtureRowHandler, Importable {

    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private BigInteger lastIncrement;

    @Getter @Setter
    private String formatStr;

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
        // find or create app tenancy (because of use wildcards)
        ApplicationTenancy applicationTenancy = securityApplicationTenancyRepository.newTenancy(
                atPath,
                atPath,
                securityApplicationTenancyRepository.findByPath(atPath.split("/%/")[0])
        );
        Property property = propertyRepository.findPropertyByReference(propertyReference);

        Numerator numerator;
        if (atPath.contains("/%/")) {
            numerator = numeratorRepository
                    .findScopedNumeratorIncludeWildCardMatching(Constants.NumeratorName.INVOICE_NUMBER, property, applicationTenancy);
        } else {
            numerator = numeratorRepository
                    .findNumerator(Constants.NumeratorName.INVOICE_NUMBER, property, applicationTenancy);
        }

        if (numerator == null) {
            numerator = numeratorRepository
                    .createScopedNumerator(Constants.NumeratorName.INVOICE_NUMBER, property, formatStr, lastIncrement, applicationTenancy);
        }
        return Lists.newArrayList(numerator);
    }

    @Inject
    private ApplicationTenancyRepository securityApplicationTenancyRepository;

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private NumeratorRepository numeratorRepository;

}