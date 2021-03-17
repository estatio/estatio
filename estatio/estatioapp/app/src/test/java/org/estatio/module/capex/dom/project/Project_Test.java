package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.Test;

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
    public void getBudgetedAmount_checks_items_on_normal_project() throws Exception {

        // given
        Project project = new Project();
        ProjectItem projectItem1 = new ProjectItem();
        projectItem1.setBudgetedAmount(new BigDecimal("10.00"));
        project.getItems().add(projectItem1);
        ProjectItem projectItem2 = new ProjectItem();
        projectItem2.setBudgetedAmount(new BigDecimal("2.34"));
        projectItem2.setCharge(new Charge()); // done for comparable for getItems returns sorted set
        project.getItems().add(projectItem2);


        // when
        BigDecimal budgetAmountParent = project.getBudgetedAmount();

        // then
        Assertions.assertThat(budgetAmountParent).isEqualTo(new BigDecimal("12.34"));

    }

}