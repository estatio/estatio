/*
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.application.spiimpl.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalConfigurationUtil;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.task.dom.state.StateTransitionService;
import org.estatio.module.task.dom.task.Task;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationTenancyEvaluatorForEstatio_Test {

    final String globalObject = "/";
    final String italianObject = "/ITA";
    final String italianCaraselloObject = "/ITA/CAR";

    final String globalUser = "/";
    final String italianUser = "/ITA";
    final String italianCaraselloUser = "/ITA/CAR";
    final String italianXCaraselloUser = "/ITA/X-CAR";
    final String italianIgigliUser = "/ITA/GIG";

    final String frenchUser = "/FRA";

    ApplicationTenancyEvaluatorForEstatio evaluator;

    @Before
    public void setUp() throws Exception {
        evaluator = new ApplicationTenancyEvaluatorForEstatio();
    }

    @Test
    public void testObjectVisibleToUser() throws Exception {

        assertThat(evaluator.objectVisibleToUser(globalObject, globalUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(globalObject, italianUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(globalObject, frenchUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(globalObject, italianCaraselloUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(globalObject, italianXCaraselloUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(globalObject, italianIgigliUser)).isTrue();

        assertThat(evaluator.objectVisibleToUser(italianObject, globalUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianObject, italianUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianObject, frenchUser)).isFalse();
        assertThat(evaluator.objectVisibleToUser(italianObject, italianCaraselloUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianObject, italianXCaraselloUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianObject, italianIgigliUser)).isTrue();

        assertThat(evaluator.objectVisibleToUser(italianCaraselloObject, globalUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianCaraselloObject, italianUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianCaraselloObject, frenchUser)).isFalse();
        assertThat(evaluator.objectVisibleToUser(italianCaraselloObject, italianCaraselloUser)).isTrue();
        assertThat(evaluator.objectVisibleToUser(italianCaraselloObject, italianXCaraselloUser)).isTrue();  // would be false normally...
        assertThat(evaluator.objectVisibleToUser(italianCaraselloObject, italianIgigliUser)).isFalse();

    }

    @Test
    public void testObjectEnabledForUser() throws Exception {

        assertThat(evaluator.objectEnabledForUser(globalObject, globalUser)).isTrue();
        assertThat(evaluator.objectEnabledForUser(globalObject, italianUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(globalObject, frenchUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(globalObject, italianCaraselloUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(globalObject, italianXCaraselloUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(globalObject, italianIgigliUser)).isFalse();

        assertThat(evaluator.objectEnabledForUser(italianObject, globalUser)).isTrue();
        assertThat(evaluator.objectEnabledForUser(italianObject, italianUser)).isTrue();
        assertThat(evaluator.objectEnabledForUser(italianObject, frenchUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(italianObject, italianCaraselloUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(italianObject, italianXCaraselloUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(italianObject, italianIgigliUser)).isFalse();

        assertThat(evaluator.objectEnabledForUser(italianCaraselloObject, globalUser)).isTrue();
        assertThat(evaluator.objectEnabledForUser(italianCaraselloObject, italianUser)).isTrue();
        assertThat(evaluator.objectEnabledForUser(italianCaraselloObject, frenchUser)).isFalse();
        assertThat(evaluator.objectEnabledForUser(italianCaraselloObject, italianCaraselloUser)).isTrue();
        assertThat(evaluator.objectEnabledForUser(italianCaraselloObject, italianXCaraselloUser)).isTrue(); // would be false normally...
        assertThat(evaluator.objectEnabledForUser(italianCaraselloObject, italianIgigliUser)).isFalse();

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock PersonRepository mockPersonRepository;

    @Mock FixedAssetRoleRepository mockFixedAssetRoleRepository;

    @Test
    public void hides_when_property_for_italian_external_user_not_found() throws Exception {

        // given
        ApplicationTenancyEvaluatorForEstatio evaluator = new ApplicationTenancyEvaluatorForEstatio(){
            @Override
            List<Property> propertiesForUserAsPersonCached(final ApplicationUser applicationUser){
                return Collections.EMPTY_LIST;
            }
        };
        ApplicationUser externalUserIta = new ApplicationUser();
        externalUserIta.setUsername("user1@external.ecpnv.com");
        externalUserIta.setAtPath("/ITA");

        // when, then
        Assertions.assertThat(evaluator.hides(null, externalUserIta)).isEqualTo("No property could be derived for user");

    }

    @Test
    public void hides_when_incoming_invoice_not_visible_for_italian_external_user() throws Exception {

        // given
        ApplicationTenancyEvaluatorForEstatio evaluator = new ApplicationTenancyEvaluatorForEstatio(){
            @Override
            List<Property> propertiesForUserAsPersonCached(final ApplicationUser applicationUser){
                return Arrays.asList(new Property());
            }
        };
        ApplicationUser externalUserIta = new ApplicationUser();
        externalUserIta.setUsername("user1@external.ecpnv.com");
        externalUserIta.setAtPath("/ITA");
        Person person = new Person();
        FixedAssetRole far = new FixedAssetRole();
        Property property = new Property();
        far.setAsset(property);
        IncomingInvoice invoice = new IncomingInvoice();

        // when, then
        Assertions.assertThat(evaluator.hides(invoice, externalUserIta)).isEqualTo("Invoice not visible for user");

    }

    @Test
    public void hides_when_order_not_visible_for_italian_external_user() throws Exception {

        // given
        ApplicationTenancyEvaluatorForEstatio evaluator = new ApplicationTenancyEvaluatorForEstatio(){
            @Override
            List<Property> propertiesForUserAsPersonCached(final ApplicationUser applicationUser){
                return Arrays.asList(new Property());
            }
        };
        ApplicationUser externalUserIta = new ApplicationUser();
        externalUserIta.setUsername("user1@external.ecpnv.com");
        externalUserIta.setAtPath("/ITA");
        Person person = new Person();
        FixedAssetRole far = new FixedAssetRole();
        Property property = new Property();
        far.setAsset(property);
        Order order = new Order();

        // when, then
        Assertions.assertThat(evaluator.hides(order, externalUserIta)).isEqualTo("Order not visible for user");

    }

    @Mock StateTransitionService mockStateTransitionService;

    @Test
    public void hides_when_task_not_visible_for_italian_external_user() throws Exception {

        // given
        ApplicationTenancyEvaluatorForEstatio evaluator = new ApplicationTenancyEvaluatorForEstatio(){
            @Override
            List<Property> propertiesForUserAsPersonCached(final ApplicationUser applicationUser){
                return Arrays.asList(new Property());
            }
        };
        evaluator.stateTransitionService = mockStateTransitionService;
        ApplicationUser externalUserIta = new ApplicationUser();
        externalUserIta.setUsername("user1@external.ecpnv.com");
        externalUserIta.setAtPath("/ITA");
        Person person = new Person();
        FixedAssetRole far = new FixedAssetRole();
        Property property = new Property();
        far.setAsset(property);
        Task task = new Task(null, null, null, null, null,null);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockStateTransitionService).findFor(task);
            will(returnValue(null));
        }});

        // when, then
        Assertions.assertThat(evaluator.hides(task, externalUserIta)).isEqualTo("Task not visible for user");

    }

    @Test
    public void invoiceVisibleForExternalUser_false_when_no_property_on_invoice() throws Exception {

        IncomingInvoice invoice = new IncomingInvoice();
        List<Property> propertiesForUser = new ArrayList<>();

        // when, then
        Assertions.assertThat(evaluator.invoiceVisibleForExternalUser(invoice, propertiesForUser)).isFalse();

    }

    @Test
    public void invoiceVisibleForExternalUser_false_when_no_qualifying_property_ref_found_on_invoice() throws Exception {

        Property property = new Property();
        property.setReference("NON_QUALIFYING_PROPERTY_REF");
        IncomingInvoice invoice = new IncomingInvoice();
        List<Property> propertiesForUser = new ArrayList<>();

        // when
        invoice.setProperty(property);
        propertiesForUser.add(property);

        // then
        Assertions.assertThat(evaluator.invoiceVisibleForExternalUser(invoice, propertiesForUser)).isFalse();

    }

    @Test
    public void invoiceVisibleForExternalUser_false_when_no_qualifying_project_ref_found_on_invoice_items() throws Exception {

        Property property = new Property();
        property.setReference("COL");
        IncomingInvoice invoice = new IncomingInvoice();
        List<Property> propertiesForUser = new ArrayList<>();

        // when
        invoice.setProperty(property);
        propertiesForUser.add(property);

        // then
        Assertions.assertThat(evaluator.invoiceVisibleForExternalUser(invoice, propertiesForUser)).isFalse();

    }

    @Test
    public void invoiceVisibleForExternalUser_false_when_property_of_invoice_not_in_user_properties_list() throws Exception {

        Property property = new Property();
        property.setReference("COL");
        Property otherProperty = new Property();
        IncomingInvoice invoice = new IncomingInvoice();
        List<Property> propertiesForUser = new ArrayList<>();
        IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem();
        invoiceItem.setInvoice(invoice);
        final Project qualifyingProject = new Project();
        qualifyingProject.setReference(IncomingInvoiceApprovalConfigurationUtil.PROPERTY_REF_EXTERNAL_PROJECT_REF_MAP.get("COL"));
        invoiceItem.setProject(qualifyingProject);
        invoice.getItems().add(invoiceItem);


        // when
        invoice.setProperty(property);
        propertiesForUser.add(otherProperty);

        // then
        Assertions.assertThat(evaluator.invoiceVisibleForExternalUser(invoice, propertiesForUser)).isFalse();

    }

    @Test
    public void invoiceVisibleForExternalUser_true_when_all_conditions_met() throws Exception {

        Property property = new Property();
        property.setReference("COL");
        IncomingInvoice invoice = new IncomingInvoice();
        List<Property> propertiesForUser = new ArrayList<>();
        IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem();
        invoiceItem.setInvoice(invoice);
        final Project qualifyingProject = new Project();
        qualifyingProject.setReference(IncomingInvoiceApprovalConfigurationUtil.PROPERTY_REF_EXTERNAL_PROJECT_REF_MAP.get("COL"));
        invoiceItem.setProject(qualifyingProject);
        invoice.getItems().add(invoiceItem);

        // when
        invoice.setProperty(property);
        propertiesForUser.add(property);

        // then
        Assertions.assertThat(evaluator.invoiceVisibleForExternalUser(invoice, propertiesForUser)).isTrue();

    }

    @Test
    public void orderVisibleForExternalUser_false_when_no_property_on_invoice() throws Exception {

        Order order = new Order();
        List<Property> propertiesForUser = new ArrayList<>();

        // when, then
        Assertions.assertThat(evaluator.orderVisibleForExternalUser(order, propertiesForUser)).isFalse();

    }

    @Test
    public void orderVisibleForExternalUser_false_when_no_qualifying_property_ref_found_on_invoice() throws Exception {

        Property property = new Property();
        property.setReference("NON_QUALIFYING_PROPERTY_REF");
        Order order = new Order();
        List<Property> propertiesForUser = new ArrayList<>();

        // when
        order.setProperty(property);
        propertiesForUser.add(property);

        // then
        Assertions.assertThat(evaluator.orderVisibleForExternalUser(order, propertiesForUser)).isFalse();

    }

    @Test
    public void orderVisibleForExternalUser_false_when_no_qualifying_project_ref_found_on_invoice_items() throws Exception {

        Property property = new Property();
        property.setReference("COL");
        Order order = new Order();
        List<Property> propertiesForUser = new ArrayList<>();

        // when
        order.setProperty(property);
        propertiesForUser.add(property);

        // then
        Assertions.assertThat(evaluator.orderVisibleForExternalUser(order, propertiesForUser)).isFalse();

    }

    @Test
    public void orderVisibleForExternalUser_false_when_property_of_invoice_not_in_user_properties_list() throws Exception {

        Property property = new Property();
        property.setReference("COL");
        Property otherProperty = new Property();
        Order order = new Order();
        List<Property> propertiesForUser = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        final Project qualifyingProject = new Project();
        qualifyingProject.setReference(IncomingInvoiceApprovalConfigurationUtil.PROPERTY_REF_EXTERNAL_PROJECT_REF_MAP.get("COL"));
        orderItem.setProject(qualifyingProject);
        order.getItems().add(orderItem);


        // when
        order.setProperty(property);
        propertiesForUser.add(otherProperty);

        // then
        Assertions.assertThat(evaluator.orderVisibleForExternalUser(order, propertiesForUser)).isFalse();

    }

    @Test
    public void orderVisibleForExternalUser_true_when_all_conditions_met() throws Exception {

        Property property = new Property();
        property.setReference("COL");
        Order order = new Order();
        List<Property> propertiesForUser = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        final Project qualifyingProject = new Project();
        qualifyingProject.setReference(IncomingInvoiceApprovalConfigurationUtil.PROPERTY_REF_EXTERNAL_PROJECT_REF_MAP.get("COL"));
        orderItem.setProject(qualifyingProject);
        order.getItems().add(orderItem);

        // when
        order.setProperty(property);
        propertiesForUser.add(property);

        // then
        Assertions.assertThat(evaluator.orderVisibleForExternalUser(order, propertiesForUser)).isTrue();

    }

    @Test
    public void taskVisibleForExternalUser_false_when_task_not_attached_to_invoice_or_order() throws Exception {

        // given
        evaluator.stateTransitionService = mockStateTransitionService;
        Task task = new Task(null, null, null, null, null, null);
        List<Property> propertiesForUser = new ArrayList<>();
        BankAccountVerificationStateTransition stateTransition = new BankAccountVerificationStateTransition();

        // expect
        context.checking(new Expectations(){{
            allowing(mockStateTransitionService).findFor(task);
            will(returnValue(stateTransition));
        }});

        // then
        Assertions.assertThat(evaluator.taskVisibleForExternalUser(task, propertiesForUser)).isFalse();

    }

    @Test
    public void taskVisibleForExternalUser_false_when_task_attached_to_invoice_but_invoice_not_visible() throws Exception {

        // given
        evaluator.stateTransitionService = mockStateTransitionService;
        Task task = new Task(null, null, null, null, null, null);
        List<Property> propertiesForUser = new ArrayList<>();
        IncomingInvoice invoice = new IncomingInvoice();
        IncomingInvoiceApprovalStateTransition stateTransition = new IncomingInvoiceApprovalStateTransition();
        stateTransition.setInvoice(invoice);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockStateTransitionService).findFor(task);
            will(returnValue(stateTransition));
        }});

        // then
        Assertions.assertThat(evaluator.taskVisibleForExternalUser(task, propertiesForUser)).isFalse();

    }

    @Test
    public void taskVisibleForExternalUser_false_when_task_attached_to_order_but_order_not_visible() throws Exception {

        // given
        evaluator.stateTransitionService = mockStateTransitionService;
        Task task = new Task(null, null, null, null, null, null);
        List<Property> propertiesForUser = new ArrayList<>();
        Order order = new Order();
        OrderApprovalStateTransition stateTransition = new OrderApprovalStateTransition();
        stateTransition.setOrdr(order);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockStateTransitionService).findFor(task);
            will(returnValue(stateTransition));
        }});

        // then
        Assertions.assertThat(evaluator.taskVisibleForExternalUser(task, propertiesForUser)).isFalse();

    }

}