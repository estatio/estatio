package org.estatio.module.coda.dom.doc;

import com.google.common.collect.Sets;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocHead_validateBuyer_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    PartyRepository mockPartyRepository;
    @Mock
    PartyRoleTypeRepository mockPartyRoleTypeRepository;

    PartyRoleType ecpRoleType;
    Organisation organisationWithRole;
    Organisation organisationWithoutRole;


    CodaDocHead codaDocHead;

    @Before
    public void setUp() throws Exception {
        codaDocHead = new CodaDocHead("IT01", null, null, null, null, null, null);
        codaDocHead.partyRepository = mockPartyRepository;
        codaDocHead.partyRoleTypeRepository = mockPartyRoleTypeRepository;

        // given
        ecpRoleType = new PartyRoleType();

        context.checking(new Expectations() {{
            allowing(mockPartyRoleTypeRepository).findByKey(IncomingInvoiceRoleTypeEnum.ECP.getKey());
            will(returnValue(ecpRoleType));
        }});

        organisationWithRole = new Organisation();
        organisationWithRole.setRoles(Sets.newTreeSet());
        final PartyRole partyRole = new PartyRole();
        partyRole.setRoleType(ecpRoleType);
        organisationWithRole.getRoles().add(partyRole);

        organisationWithoutRole = new Organisation();


        assertThat(codaDocHead.getCmpCodeValidationStatus()).isEqualTo(ValidationStatus.NOT_CHECKED);
        assertThat(codaDocHead.getCmpCodeBuyer()).isNull();

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    assertThat(organisationWithRole.hasPartyRoleType(ecpRoleType)).isTrue();
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    assertThat(organisationWithoutRole.hasPartyRoleType(ecpRoleType)).isFalse();
    }

    @Test
    public void when_no_such_buyer() throws Exception {

        // expecting
        context.checking(new Expectations() {{
            allowing(mockPartyRepository).findPartyByReference("IT01");
            will(returnValue(null));
        }});

        // when
        codaDocHead.validateBuyer();

        // then
        assertThat(codaDocHead.getCmpCodeValidationStatus()).isEqualTo(ValidationStatus.INVALID);
        assertThat(codaDocHead.getCmpCodeBuyer()).isNull();
        assertThat(codaDocHead.getReasonInvalid()).isEqualTo("No buyer party found for cmpCode 'IT01'");
    }

    @Test
    public void when_buyer_is_not_an_Organisation() throws Exception {

        // expecting
        context.checking(new Expectations() {{
            allowing(mockPartyRepository).findPartyByReference("IT01");
            will(returnValue(new Person()));
        }});

        // when
        codaDocHead.validateBuyer();

        // then
        assertThat(codaDocHead.getCmpCodeValidationStatus()).isEqualTo(ValidationStatus.INVALID);
        assertThat(codaDocHead.getCmpCodeBuyer()).isNull();
        assertThat(codaDocHead.getReasonInvalid()).isEqualTo("Party found for cmpCode 'IT01' is not an Organisation");
    }

    @Test
    public void when_buyer_does_not_have_ECP_role() throws Exception {

        // given
        assertThat(organisationWithoutRole.hasPartyRoleType(ecpRoleType)).isFalse();

        // expecting
        context.checking(new Expectations() {{
            allowing(mockPartyRepository).findPartyByReference("IT01");
            will(returnValue(organisationWithoutRole));

        }});

        // when
        codaDocHead.validateBuyer();

        // then
        assertThat(codaDocHead.getCmpCodeValidationStatus()).isEqualTo(ValidationStatus.INVALID);
        assertThat(codaDocHead.getCmpCodeBuyer()).isNull();
        assertThat(codaDocHead.getReasonInvalid()).isEqualTo("Organisation 'IT01' does not have ECP role");
    }

    @Test
    public void when_buyer_does_have_ECP_role() throws Exception {

        // given
        assertThat(organisationWithRole.hasPartyRoleType(ecpRoleType)).isTrue();

        // expecting
        context.checking(new Expectations() {{
            allowing(mockPartyRepository).findPartyByReference("IT01");
            will(returnValue(organisationWithRole));
        }});

        // when
        codaDocHead.validateBuyer();

        // then
        assertThat(codaDocHead.getCmpCodeValidationStatus()).isEqualTo(ValidationStatus.VALID);
        assertThat(codaDocHead.getCmpCodeBuyer()).isEqualTo(organisationWithRole);
        assertThat(codaDocHead.getReasonInvalid()).isNull();
    }

}