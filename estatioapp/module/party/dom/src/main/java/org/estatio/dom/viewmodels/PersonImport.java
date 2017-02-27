package org.estatio.dom.viewmodels;

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
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonGenderType;
import org.estatio.dom.party.PersonRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.PersonImport"
)
public class PersonImport implements ExcelFixtureRowHandler, Importable {

    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private  String initials;

    @Getter @Setter
    private String firstName;

    @Getter @Setter
    private String lastName;

    @Getter @Setter
    private  String gender;

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

        final ApplicationTenancy applicationTenancy = securityApplicationTenancyRepository.findByPath(atPath);
        Person person = (Person) partyRepository.findPartyByReference(reference);
        if (person == null) {
            person = personRepository.newPerson(
                    reference,
                    initials,
                    firstName,
                    lastName,
                    gender == null ? PersonGenderType.UNKNOWN : PersonGenderType.valueOf(gender), applicationTenancy);
        }
        person.setApplicationTenancyPath(applicationTenancy.getPath());
        person.setFirstName(firstName);
        person.setLastName(lastName);

        return Lists.newArrayList();
    }

    @Inject
    private ApplicationTenancyRepository securityApplicationTenancyRepository;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private PersonRepository personRepository;

}