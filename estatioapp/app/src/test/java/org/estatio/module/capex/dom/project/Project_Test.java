package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.charge.dom.Charge;

public class Project_Test {

    @Test
    public void getBudgetedAmount_works_recursively_on_parent_project() throws Exception {

        // given
        Project parentProject = new Project();
        Project child1 = new Project();
        child1.setReference("ch1");
        Project childsChild1 = new Project(){
            @Override
            public BigDecimal getBudgetedAmount(){
                return new BigDecimal("10.00");
            }
        };
        child1.getChildren().add(childsChild1);

        Project child2 = new Project();
        child2.setReference("ch2");
        parentProject.getChildren().add(child1);
        Project childsChild2 = new Project(){
            @Override
            public BigDecimal getBudgetedAmount(){
                return new BigDecimal("1.11");
            }
        };
        child2.getChildren().add(childsChild2);

        parentProject.getChildren().add(child1);
        parentProject.getChildren().add(child2);

        // when
        BigDecimal budgetAmountParent = parentProject.getBudgetedAmount();

        // then
        Assertions.assertThat(budgetAmountParent).isEqualTo(new BigDecimal("11.11"));

    }

    @Test
    @Ignore //TODO: replace with test new budgeting structure?
    public void getBudgetedAmount_checks_items_on_normal_project() throws Exception {

        // given
        Project project = new Project();
        ProjectItem projectItem1 = new ProjectItem();
        project.getItems().add(projectItem1);
        ProjectItem projectItem2 = new ProjectItem();
        projectItem2.setCharge(new Charge()); // done for comparable for getItems returns sorted set
        project.getItems().add(projectItem2);


        // when
        BigDecimal budgetAmountParent = project.getBudgetedAmount();

        // then
        Assertions.assertThat(budgetAmountParent).isEqualTo(new BigDecimal("12.34"));

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    ClockService mockClockService;

    @Test
    public void choices0CreateBudgetForecast_works() throws Exception {

        // given
        Project project = new Project();
        project.clockService = mockClockService;

        // expect
        context.checking(new Expectations(){{
            oneOf(mockClockService).now();
            will(returnValue(new LocalDate(2020,3,31)));
        }});

        // when
        final List<LocalDate> choices = project.choices0CreateBudgetForecast();

        // then
        Assertions.assertThat(choices).hasSize(5);

    }

}