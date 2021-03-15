package org.estatio.module.application.ervimport;

import java.math.BigDecimal;
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

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.asset.dom.erv.EstimatedRentalValue;
import org.estatio.module.asset.dom.erv.EstimatedRentalValueRepository;
import org.estatio.module.asset.dom.erv.Type;
import org.estatio.module.base.dom.Importable;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.application.ervimport.ErvImport"
)
public class ErvImport implements ExcelFixtureRowHandler, Importable, FixtureAwareRowHandler<ErvImport> {

    private static final Logger LOG = LoggerFactory.getLogger(ErvImport.class);

    public ErvImport(){}

    public ErvImport(final Unit unit,
            final LocalDate date,
            final Type type
    ){
        this.unitReference = unit.getReference();
        this.unitName = unit.getName();
        this.externalReference = unit.getExternalReference();
        this.unitArea = unit.getArea();
        this.date = date;
        this.type = type.name();
    }

    public ErvImport(final EstimatedRentalValue erv){
        this.unitReference = erv.getUnit().getReference();
        this.unitName = erv.getUnit().getName();
        this.externalReference = erv.getUnit().getExternalReference();
        this.unitArea = erv.getUnit().getArea();
        this.date = erv.getDate();
        this.type = erv.getType().name();
        this.value = erv.getValue();
    }

    public ErvImport(final EstimatedRentalValue previousErv, final LocalDate date){
        this.unitReference = previousErv.getUnit().getReference();
        this.unitName = previousErv.getUnit().getName();
        this.externalReference = previousErv.getUnit().getExternalReference();
        this.unitArea = previousErv.getUnit().getArea();
        this.date = date;
        this.type = previousErv.getType().name();
        this.previousDate = previousErv.getDate();
        this.previousValue = previousErv.getValue();
    }

    @Getter @Setter
    private String unitReference;

    @Getter @Setter
    private String unitName;

    @Getter @Setter
    private String externalReference;

    @Getter @Setter
    private BigDecimal unitArea;

    @Getter @Setter
    private String currentBrand;

    @Getter @Setter
    private LocalDate date;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private BigDecimal value;

    @Getter @Setter
    private LocalDate previousDate;

    @Getter @Setter
    private BigDecimal previousValue;

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

        final Unit unit = unitRepository.findUnitByReference(unitReference);
        if (unit==null) {
            logAndWarn(String.format("Unit not found for reference %s", unitReference));
            return Lists.newArrayList();
        }

        Type typeEnum;

        try {
            typeEnum = Type.valueOf(type);
        } catch (Exception e){
            logAndWarn(String.format("Type not found for %s", type));
            return Lists.newArrayList();
        }

        if (value == null) {
            logAndWarn(String.format("No value found for %s", unitReference));
            return Lists.newArrayList();
        }

        EstimatedRentalValue erv = estimatedRentalValueRepository.upsert(unit, date, typeEnum, value);

        return Lists.newArrayList(erv);
    }

    @Inject
    UnitRepository unitRepository;

    @Inject
    EstimatedRentalValueRepository estimatedRentalValueRepository;


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

    @Override public void handleRow(final ErvImport previousRow) {
        this.importData(previousRow);
    }

    private void logAndWarn(final String message) {
        LOG.warn(message);
        messageService.warnUser(message);
    }

    @Inject MessageService messageService;

}
