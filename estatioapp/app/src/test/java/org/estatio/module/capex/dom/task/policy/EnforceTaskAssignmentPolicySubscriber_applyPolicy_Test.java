package org.estatio.module.capex.dom.task.policy;

import java.util.Optional;

import com.google.common.collect.Lists;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.togglz.junit.TogglzRule;

import org.apache.isis.applib.services.metamodel.MetaModelService3;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class EnforceTaskAssignmentPolicySubscriber_applyPolicy_Test {

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    MetaModelService3 mockMetaModelService3;
    @Mock
    StateTransitionService mockStateTransitionService;
    @Mock
    PersonRepository mockPersonRepository;
    @Mock
    PartyRoleTypeRepository mockPartyRoleTypeRepository;

    EnforceTaskAssignmentPolicySubscriber subscriber;

    BankAccount domainObject;

    Class<BankAccountVerificationStateTransition> stateTransitionClass;

    BankAccountVerificationStateTransition pendingTransition;
    Task pendingTask;
    Person personTaskAssignedTo;
    PartyRoleType roleTaskAssignedTo;
    PartyRoleType previousRoleTaskAssignedTo;

    Person personForMe;

    @Before
    public void setUp() throws Exception {
        subscriber = new EnforceTaskAssignmentPolicySubscriber();
        subscriber.stateTransitionService = mockStateTransitionService;
        subscriber.personRepository = mockPersonRepository;
        subscriber.metaModelService3 = mockMetaModelService3;
        subscriber.partyRoleTypeRepository = mockPartyRoleTypeRepository;

        domainObject = new BankAccount();
        stateTransitionClass = BankAccountVerificationStateTransition.class;
        pendingTransition = new BankAccountVerificationStateTransition();
        personTaskAssignedTo = new Person();
        personTaskAssignedTo.setReference("JBLOGGS");
        roleTaskAssignedTo = new PartyRoleType();
        roleTaskAssignedTo.setKey("Treasurer");
        previousRoleTaskAssignedTo = new PartyRoleType();
        previousRoleTaskAssignedTo.setKey("INCOMING_INVOICE_MANAGER");

        pendingTask = new Task(
                roleTaskAssignedTo, personTaskAssignedTo,
                "some description", LocalDateTime.now(),
                "bankAccount.BankAccountVerificationStateTransition" // objectType of transition class
        );

        pendingTransition.setTask(pendingTask);

        personForMe = new Person();
        personForMe.getRoles().add(new PartyRole(personForMe, roleTaskAssignedTo));

        assertThat(pendingTransition.getTask()).isNotNull();
        assertThat(personForMe.getRoles()).isNotEmpty();
        assertThat(Lists.newArrayList(personForMe.getRoles()).stream().map(PartyRole::getRoleType))
                .contains(roleTaskAssignedTo);

    }


    @Test
    public void not_vetoed_if_approveByProxy_is_enabled() throws Exception {

        // given
        togglzRule.enable(EstatioTogglzFeature.ApproveByProxy);


        // when
        final Optional<String> reasonIfAny = subscriber.applyPolicy(null, null);

        // then
        assertThat(reasonIfAny).isNotPresent();
    }

    @Test
    public void not_vetoed_if_no_pending_transition() throws Exception {

        // expecting
        expecting_stateTransitionService_pendingTransitionOf_toReturn(null);

        // when
        final Optional<String> reasonIfAny = subscriber.applyPolicy(stateTransitionClass, domainObject);

        // then
        assertThat(reasonIfAny).isNotPresent();
    }

    @Test
    public void not_vetoed_if_pending_transition_has_no_task() throws Exception {

        // given
        pendingTransition.setTask(null);

        // expecting
        expecting_stateTransitionService_pendingTransitionOf_toReturn(this.pendingTransition);

        // when
        final Optional<String> reasonIfAny = subscriber.applyPolicy(stateTransitionClass, domainObject);

        // then
        assertThat(reasonIfAny).isNotPresent();
    }

    @Test
    public void vetoed_if_no_person_for_me() throws Exception {

        // expecting
        expecting_stateTransitionService_pendingTransitionOf_toReturn(this.pendingTransition);
        expecting_personRepository_me_toReturn(null);

        // when
        final Optional<String> reasonIfAny = subscriber.applyPolicy(stateTransitionClass, domainObject);

        // then
        assertThat(reasonIfAny).isPresent();
        assertThat(reasonIfAny).hasValue("Could not locate Person for current user");
    }

    @Test
    public void vetoed_if_person_does_not_have_tasks_role() throws Exception {

        // given
        personForMe.getRoles().clear();

        // expecting
        expecting_stateTransitionService_pendingTransitionOf_toReturn(this.pendingTransition);
        expecting_personRepository_me_toReturn(this.personForMe);
//        expecting_partyRoleRepository_findByKey_toReturn(previousRoleTaskAssignedTo);

        // when
        final Optional<String> reasonIfAny = subscriber.applyPolicy(stateTransitionClass, domainObject);

        // then
        assertThat(reasonIfAny).isPresent();
        assertThat(reasonIfAny).hasValue("Task assigned to 'Treasurer' role");
    }

    @Test
    public void vetoed_if_task_assigned_to_someone_else() throws Exception {

        // given
        Assertions.assertThat(personForMe).isNotSameAs(personTaskAssignedTo);

        // expecting
        expecting_stateTransitionService_pendingTransitionOf_toReturn(this.pendingTransition);
        expecting_personRepository_me_toReturn(this.personForMe);

        // when
        final Optional<String> reasonIfAny = subscriber.applyPolicy(stateTransitionClass, domainObject);

        // then
        assertThat(reasonIfAny).isPresent();
        assertThat(reasonIfAny).hasValue("Task assigned to JBLOGGS");
    }

    @Test
    public void not_vetoed_if_task_assigned_to_noone() throws Exception {

        // given
        pendingTask.setPersonAssignedTo(null);

        // expecting
        expecting_stateTransitionService_pendingTransitionOf_toReturn(this.pendingTransition);
        expecting_personRepository_me_toReturn(this.personForMe);

        // when
        final Optional<String> reasonIfAny = subscriber.applyPolicy(stateTransitionClass, domainObject);

        // then
        assertThat(reasonIfAny).isNotPresent();
    }

    @Test
    public void not_vetoed_if_task_assigned_to_self() throws Exception {

        // given
        pendingTask.setPersonAssignedTo(personForMe);

        // expecting
        expecting_stateTransitionService_pendingTransitionOf_toReturn(this.pendingTransition);
        expecting_personRepository_me_toReturn(this.personForMe);

        // when
        final Optional<String> reasonIfAny = subscriber.applyPolicy(stateTransitionClass, domainObject);

        // then
        assertThat(reasonIfAny).isNotPresent();
    }

    private void expecting_personRepository_me_toReturn(final Person person) {
        context.checking(new Expectations() {{
            oneOf(mockPersonRepository).me();
            will(returnValue(person));
        }});
    }

    private void expecting_stateTransitionService_pendingTransitionOf_toReturn(final BankAccountVerificationStateTransition pendingTransition) {
        context.checking(new Expectations() {{
            oneOf(mockStateTransitionService).pendingTransitionOf(domainObject, stateTransitionClass);
            will(returnValue(pendingTransition));
        }});
    }

    private void expecting_partyRoleRepository_findByKey_toReturn(final PartyRoleType partyRoleType) {
        context.checking(new Expectations() {{
            oneOf(mockPartyRoleTypeRepository).findByKey("INCOMING_INVOICE_MANAGER");
            will(returnValue(partyRoleType));
        }});
    }

}