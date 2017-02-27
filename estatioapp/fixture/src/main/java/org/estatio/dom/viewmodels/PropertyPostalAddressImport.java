package org.estatio.dom.viewmodels;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.StateRepository;

import org.estatio.dom.Importable;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.PropertyPostalAddressImport"
)
public class PropertyPostalAddressImport implements ExcelFixtureRowHandler, Importable {

    @Getter @Setter
    private String propertyReference;

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

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(PropertyImport.class, CountryImport.class);
//    }

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        return importData(previousRow);
    }

    @Override
    public List<Object> importData(final Object previousRow) {

        final Property property = propertyRepository.findPropertyByReference(propertyReference);
        if (property == null) {
            throw new ApplicationException(String.format("Property with reference %s not found.", propertyReference));
        }
        if (communicationChannelRepository.findByOwnerAndType(property, CommunicationChannelType.POSTAL_ADDRESS).size() == 0) {
            communicationChannelRepository.newPostal(
                    property,
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

        return Lists.newArrayList();
    }

    //region > injected services
    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private CommunicationChannelRepository communicationChannelRepository;

    @Inject
    private StateRepository stateRepository;

    @Inject
    private CountryRepository countryRepository;

    //endregion

}