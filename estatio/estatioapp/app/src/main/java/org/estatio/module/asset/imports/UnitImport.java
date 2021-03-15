package org.estatio.module.asset.imports;

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

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.StateRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.asset.dom.UnitType;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.base.dom.Importable;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.UnitImport"
)
public class UnitImport implements ExcelFixtureRowHandler, Importable, FixtureAwareRowHandler<UnitImport> {

    private static final Logger LOG = LoggerFactory.getLogger(Unit.class);

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private String ownerReference;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private BigDecimal area;

    @Getter @Setter
    private BigDecimal salesArea;

    @Getter @Setter
    private BigDecimal storageArea;

    @Getter @Setter
    private BigDecimal mezzanineArea;

    @Getter @Setter
    private BigDecimal dehorsArea;

    @Getter @Setter
    private String address1;

    @Getter @Setter
    private String address2;

    @Getter @Setter
    private String address3;

    @Getter @Setter
    private String city;

    @Getter @Setter
    private String postalCode;

    @Getter @Setter
    private String stateCode;

    @Getter @Setter
    private String countryCode;

    @Getter @Setter
    private String externalReference;

    static int counter = 0;

    //    @Override public List<Class> importAfter() {
    //        return Lists.newArrayList(PropertyImport.class, CountryImport.class);
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

        final Property property = propertyRepository.findPropertyByReference(propertyReference);
        Unit unit = unitRepository.findUnitByReference(reference);
        if (unit == null) {
            unit = property.newUnit(reference, name, UnitType.BOUTIQUE);
        }
        // set attributes
        unit.setName(name);
        unit.setType(UnitType.valueOf(type));
        unit.changeDates(startDate, endDate);
        unit.setArea(area);
        unit.setSalesArea(salesArea);
        unit.setStorageArea(storageArea);
        unit.setMezzanineArea(mezzanineArea);
        unit.setDehorsArea(dehorsArea);
        unit.setExternalReference(externalReference);
        if (communicationChannelRepository.findByOwnerAndType(unit, CommunicationChannelType.POSTAL_ADDRESS).size() == 0) {
            communicationChannelRepository.newPostal(
                    unit,
                    CommunicationChannelType.POSTAL_ADDRESS,
                    address1,
                    address2,
                    address3,
                    postalCode,
                    city,
                    stateRepository.findState(stateCode),
                    countryRepository.findCountry(countryCode)
            );
        }
        if (ownerReference != null) {
            Party party = partyRepository.findPartyByReference(ownerReference);
            if (party == null) {
                throw new IllegalArgumentException(String.format("Party with ownerReference %s not found", getOwnerReference()));
            }
            // create property owner of not found one already
            FixedAssetRole propertyOwnerRole = fixedAssetRoleRepository.findRole(unitRepository.findUnitByReference(reference), FixedAssetRoleTypeEnum.PROPERTY_OWNER);
            if (propertyOwnerRole == null) {
                unit.createRole(FixedAssetRoleTypeEnum.PROPERTY_OWNER, party, null, null);
            }
        }
        return Lists.newArrayList(unit);
    }

    //region > injected services

    @Inject
    private UnitRepository unitRepository;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private CommunicationChannelRepository communicationChannelRepository;

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private CountryRepository countryRepository;

    @Inject
    private StateRepository stateRepository;

    @Inject
    private FixedAssetRoleRepository fixedAssetRoleRepository;

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

    @Override public void handleRow(final UnitImport previousRow) {
        this.importData(previousRow);
    }

    //endregion
}
