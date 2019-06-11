package org.estatio.module.asset.imports;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.message.MessageService;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.counts.Count;
import org.estatio.module.asset.dom.counts.CountRepository;
import org.estatio.module.base.dom.Importable;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.asset.imports.CountImport"
)
public class CountImport implements ExcelFixtureRowHandler, Importable, FixtureAwareRowHandler<CountImport> {

    private static final Logger LOG = LoggerFactory.getLogger(CountImport.class);

    public CountImport(){}

    public CountImport(final String propertyReference, final LocalDate date, final String type, final BigInteger value){
        this.propertyReference = propertyReference;
        this.date = date;
        this.type = type;
        this.value = value;
    }

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private LocalDate date;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private BigInteger value;

    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        final Property property = propertyRepository.findPropertyByReference(propertyReference);
        if (property==null) {
            logAndWarn(String.format("Property not found for reference %s", propertyReference));
            return Lists.newArrayList();
        }

        if (type == null || !(type.equals("P") || type.equals("C"))) {
            logAndWarn(String.format("Type with value %s is not correct; type should be either P or C", type));
            return Lists.newArrayList();
        }

        if (value == null) {
            logAndWarn(String.format("Cannot parse value for %s, %s, %s", propertyReference, date.toString("yyyy-MM-dd"), type));
            return Lists.newArrayList();
        }

        Count count = countRepository.findUnique(property, date);
        if (count == null) count = countRepository.create(property, date, null, null);
        if (type.equals("P")) count.setPedestrianCount(value);
        if (type.equals("C")) count.setCarCount(value);

        return Lists.newArrayList(count);
    }

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    CountRepository countRepository;


    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private FixtureScript.ExecutionContext executionContext;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private ExcelFixture2 excelFixture2;

    @Override public void handleRow(final CountImport previousRow) {
        this.importData(previousRow);
    }

    private void logAndWarn(final String message) {
        LOG.warn(message);
        messageService.warnUser(message);
    }

    @Inject MessageService messageService;

}
