package org.estatio.module.registration.imports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.factory.FactoryService;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.dom.Importable;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.asset.registration.FixedAssetRegistration;
import org.estatio.dom.asset.registration.FixedAssetRegistrationRepository;
import org.estatio.dom.asset.registration.FixedAssetRegistrationTypeRepository;
import org.estatio.module.registration.dom.LandRegister;
import org.estatio.module.registration.dom.LandRegisters;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.LandRegisterImport"
)
public class LandRegisterImport implements ExcelFixtureRowHandler, Importable {

    private static final Logger LOG = LoggerFactory.getLogger(LandRegisterImport.class);

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private String unitReference;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String comuneAmministrativo;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String comuneCatastale;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String codiceComuneCatastale;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigDecimal rendita;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String foglio;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String particella;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String subalterno;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String categoria;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String classe;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String consistenza;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String description;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private LocalDate startDate;

    static int counter = 0;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(UnitImport.class);
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

        counter++;
        if (counter == 1) {
            System.out.println();
            LOG.info("importing");
        }

        Unit unit = unitRepository.findUnitByReference(unitReference);
        if (unit == null) {
            unit = unitRepository.findUnitByReference(unitReference.replace(" ", "+"));
        }
        if (unit == null) {
            throw new IllegalArgumentException("Unknown unit: ".concat(unitReference));
        }

        LandRegister landRegister = null;
        List<FixedAssetRegistration> farts = fixedAssetRegistrationRepository.findBySubject(unit);
        if (!farts.isEmpty()) {
            landRegister = (LandRegister) farts.get(0);
        }

        if (landRegister == null) {
            landRegister = (LandRegister) fixedAssetRegistrationTypeRepository.findByTitle("LandRegister").create(factoryService);
            container.persistIfNotAlready(landRegister);
        }

        landRegister.setSubject(unit);
        landRegister.setComuneAmministrativo(comuneAmministrativo);
        landRegister.setComuneCatastale(comuneCatastale);
        landRegister.setCodiceComuneCatastale(codiceComuneCatastale);
        landRegister.setRendita(rendita == null ? null : rendita.setScale(2, RoundingMode.HALF_EVEN));
        landRegister.setFoglio(foglio);
        landRegister.setParticella(particella);
        landRegister.setSubalterno(subalterno);
        landRegister.setCategoria(categoria);
        landRegister.setClasse(classe);
        landRegister.setConsistenza(consistenza);


        System.out.print(".");

        return null;

    }

    //region > injected services
    @Inject
    UnitRepository unitRepository;

    @Inject
    FixedAssetRegistrationRepository fixedAssetRegistrationRepository;

    @Inject
    FixedAssetRegistrationTypeRepository fixedAssetRegistrationTypeRepository;

    @Inject
    LandRegisters landRegisterRepository;

    @Inject
    DomainObjectContainer container;

    @Inject
    FactoryService factoryService;

    //endregion

}
