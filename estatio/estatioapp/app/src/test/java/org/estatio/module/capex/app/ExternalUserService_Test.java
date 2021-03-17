package org.estatio.module.capex.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalConfigurationUtil;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;

public class ExternalUserService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock PersonRepository mockPersonRepository;

    @Mock ProjectRepository mockProjectRepository;

    @Mock FixedAssetRoleRepository mockFixedAssetRoleRepository;

    @Test
    public void getPropertiesForExternalUser_works_when_no_roles() {

        // given
        ExternalUserService service = new ExternalUserService();
        service.personRepository = mockPersonRepository;
        service.fixedAssetRoleRepository = mockFixedAssetRoleRepository;

        Person meAsPerson = new Person();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockPersonRepository).me();
            will(returnValue(meAsPerson));
            oneOf(mockFixedAssetRoleRepository).findByParty(meAsPerson);
            will(returnValue(Collections.EMPTY_LIST));
        }});

        // when
        Assertions.assertThat(service.getPropertiesForExternalUser()).isEmpty();

    }

    @Test
    public void getPropertiesForExternalUser_works_when_roles() {

        // given
        ExternalUserService service = new ExternalUserService();
        service.personRepository = mockPersonRepository;
        service.fixedAssetRoleRepository = mockFixedAssetRoleRepository;

        Person meAsPerson = new Person();
        Property property1 = new Property();
        Property property2 = new Property();
        Unit unit = new Unit();

        FixedAssetRole role1 = new FixedAssetRole();
        role1.setAsset(property1);
        FixedAssetRole role2 = new FixedAssetRole();
        role2.setAsset(property1);
        FixedAssetRole role3 = new FixedAssetRole();
        role3.setAsset(unit);
        FixedAssetRole role4 = new FixedAssetRole();
        role4.setAsset(property2);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockPersonRepository).me();
            will(returnValue(meAsPerson));
            oneOf(mockFixedAssetRoleRepository).findByParty(meAsPerson);
            will(returnValue(Arrays.asList(role1, role2, role3, role4)));
        }});

        // when
        final List<Property> propertiesForExternalUser = service.getPropertiesForExternalUser();
        Assertions.assertThat(propertiesForExternalUser).hasSize(2);
        Assertions.assertThat(propertiesForExternalUser).contains(property1);
        Assertions.assertThat(propertiesForExternalUser).contains(property2);

    }

    @Test
    public void getProjectsForExternalUser_works_when_no_qualifying_property_found() {

        // given
        Property property = new Property();
        Person meAsPerson = new Person();

        ExternalUserService service = new ExternalUserService(){
            @Override public List<Property> getPropertiesForExternalUser() {
                return Arrays.asList(property);
            }
        };
        service.personRepository = mockPersonRepository;

        // expect
        context.checking(new Expectations(){{
            oneOf(mockPersonRepository).me();
            will(returnValue(meAsPerson));
        }});

        // when
        Assertions.assertThat(service.getProjectsForExternalUser()).isEmpty();

    }

    @Test
    public void getProjectsForExternalUser_works_when_qualifying_property_found() {

        // given
        Property property = new Property();
        property.setReference("FAB");
        Person meAsPerson = new Person();
        Project project = new Project();

        ExternalUserService service = new ExternalUserService(){
            @Override public List<Property> getPropertiesForExternalUser() {
                return Arrays.asList(property);
            }
        };
        service.personRepository = mockPersonRepository;
        service.projectRepository = mockProjectRepository;

        // expect
        context.checking(new Expectations(){{
            oneOf(mockPersonRepository).me();
            will(returnValue(meAsPerson));
            oneOf(mockProjectRepository).findByReference(IncomingInvoiceApprovalConfigurationUtil.PROPERTY_REF_EXTERNAL_PROJECT_REF_MAP.get("FAB"));
            will(returnValue(project));
        }});

        // when
        final List<Project> projectsForExternalUser = service.getProjectsForExternalUser();
        Assertions.assertThat(projectsForExternalUser).hasSize(1);
        Assertions.assertThat(projectsForExternalUser).contains(project);

    }

    @Test
    public void getBuyersForExternalUser_works_when_no_owner_candidates() throws Exception {

        // given
        Property property = new Property(){
            @Override public List<FixedAssetRole> ownerCandidates() {
                return Collections.EMPTY_LIST;
            }
        };

        ExternalUserService service = new ExternalUserService(){
            @Override public List<Property> getPropertiesForExternalUser() {
                return Arrays.asList(property);
            }
        };

        // when
        Assertions.assertThat(service.getBuyersForExternalUser()).isEmpty();

    }

    @Mock ClockService mockClockService;

    @Test
    public void getBuyersForExternalUser_works_when_multiple_owner_candidates() throws Exception {

        // given
        final LocalDate date = new LocalDate(2020, 1, 1);
        Party owner1 = new Organisation();
        Party owner2 = new Organisation();
        FixedAssetRole role1 = new FixedAssetRole();
        role1.setParty(owner1);
        FixedAssetRole role2 = new FixedAssetRole();
        role2.setParty(owner2);
        role1.clockService = mockClockService;
        role2.clockService = mockClockService;

        Property property = new Property(){
            @Override public List<FixedAssetRole> ownerCandidates() {
                return Arrays.asList(role1, role2);
            }
        };

        ExternalUserService service = new ExternalUserService(){
            @Override public List<Property> getPropertiesForExternalUser() {
                return Arrays.asList(property);
            }
        };

        // expect
        context.checking(new Expectations(){{
            exactly(2).of(mockClockService).now();
            will(returnValue(date));
        }});

        // when
        Assertions.assertThat(service.getBuyersForExternalUser()).isEmpty();

    }

    @Test
    public void getBuyersForExternalUser_works_when_one_owner_candidate() throws Exception {

        // given
        final LocalDate date = new LocalDate(2020, 1, 1);
        Party owner = new Organisation();
        FixedAssetRole role = new FixedAssetRole();
        role.setParty(owner);
        role.clockService = mockClockService;

        Property property = new Property(){
            @Override public List<FixedAssetRole> ownerCandidates() {
                return Arrays.asList(role);
            }
        };

        ExternalUserService service = new ExternalUserService(){
            @Override public List<Property> getPropertiesForExternalUser() {
                return Arrays.asList(property);
            }
        };

        // expect
        context.checking(new Expectations(){{
            oneOf(mockClockService).now();
            will(returnValue(date));
        }});

        // when
        final List<Party> buyersForExternalUser = service.getBuyersForExternalUser();
        Assertions.assertThat(buyersForExternalUser).hasSize(1);
        Assertions.assertThat(buyersForExternalUser).contains(owner);

    }
}