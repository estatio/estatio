package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.charge.dom.Charge;

import static org.junit.Assert.*;

public class BudgetCreationManager_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    ProjectBudgetRepository mockProjectBudgetRepository;

    @Test
    public void getBudgetLines_when_project_has_no_project_items() {

        // given
        Project project = new Project();
        BudgetCreationManager manager = new BudgetCreationManager(project);
        manager.projectBudgetRepository = mockProjectBudgetRepository;

        ProjectBudget budget = new ProjectBudget();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockProjectBudgetRepository).findOrCreate(project,1 );
            will(returnValue(budget));
        }});

        // when
        final List<BudgetLine> budgetLines = manager.getBudgetLines();

        // then
        Assertions.assertThat(budgetLines).isEmpty();

    }

    @Test
    public void getBudgetLines_when_project_has_project_items() {

        // given
        Project project = new Project();
        project.setReference("TSTPR123");
        Charge ch1 = new Charge();
        ch1.setReference("ITWT002");
        Charge ch2 = new Charge();
        ch2.setReference("ITWT003");
        Charge ch3 = new Charge();
        ch3.setReference("ITWT001");
        ProjectItem i1 = new ProjectItem();
        i1.setCharge(ch1);
        ProjectItem i2 = new ProjectItem();
        i2.setCharge(ch2);
        ProjectItem i3 = new ProjectItem();
        i3.setCharge(ch3);
        project.getItems().add(i1);
        project.getItems().add(i2);
        project.getItems().add(i3);
        BudgetCreationManager manager = new BudgetCreationManager(project);
        manager.projectBudgetRepository = mockProjectBudgetRepository;

        ProjectBudget budget = new ProjectBudget(){
            // (partly) immitates the 'real' process
            @Override public ProjectBudget findOrCreateBudgetItem(final ProjectItem item) {
                final ProjectBudgetItem bi = new ProjectBudgetItem();
                bi.setProjectItem(item);
                bi.setProjectBudget(this);
                getItems().add(bi);
                return null;
            }
        };
        budget.setProject(project);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockProjectBudgetRepository).findOrCreate(project,1 );
            will(returnValue(budget));
        }});

        // when
        final List<BudgetLine> budgetLines = manager.getBudgetLines();

        // then
        Assertions.assertThat(budgetLines).hasSize(3);
        Assertions.assertThat(budgetLines.get(0).getChargeReference()).isEqualTo(ch3.getReference());
        Assertions.assertThat(budgetLines.get(1).getChargeReference()).isEqualTo(ch1.getReference());
        Assertions.assertThat(budgetLines.get(2).getChargeReference()).isEqualTo(ch2.getReference());
        budgetLines.forEach(l->{
            Assertions.assertThat(l.getProjectReference()).isEqualTo(project.getReference());
        });


    }
}