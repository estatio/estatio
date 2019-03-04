package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ProjectItemTerm_Test {

    @Test
    public void getPercentageOfTotalBudget() {

        // given
        ProjectItem projectItem = new ProjectItem();
        projectItem.setBudgetedAmount(new BigDecimal("12345.56"));

        ProjectItemTerm term = new ProjectItemTerm();
        term.setProjectItem(projectItem);

        // when
        term.setBudgetedAmount(new BigDecimal("1000.12"));
        // then
        Assertions.assertThat(term.getPercentageOfTotalBudget()).isEqualTo(8);

        // when
        term.setBudgetedAmount(new BigDecimal("1234.56"));
        // then
        Assertions.assertThat(term.getPercentageOfTotalBudget()).isEqualTo(10);

        // when
        term.setBudgetedAmount(new BigDecimal("12345.00"));
        // then
        Assertions.assertThat(term.getPercentageOfTotalBudget()).isEqualTo(100);

    }

    @Test
    public void getPercentageOfTotalBudget_when_budgeted_amount_equals_zero() {

        // given
        ProjectItem projectItem = new ProjectItem();
        projectItem.setBudgetedAmount(new BigDecimal("0.00"));

        ProjectItemTerm term = new ProjectItemTerm();
        term.setProjectItem(projectItem);

        // when
        term.setBudgetedAmount(new BigDecimal("1000.12"));
        // then
        Assertions.assertThat(term.getPercentageOfTotalBudget()).isEqualTo(0);


    }

    @Test
    public void getPercentageOfTotalBudget_when_no_budgeted_amount() {

        // given
        ProjectItem projectItem = new ProjectItem();
        projectItem.setBudgetedAmount(null);

        ProjectItemTerm term = new ProjectItemTerm();
        term.setProjectItem(projectItem);

        // when
        term.setBudgetedAmount(new BigDecimal("1000.12"));
        // then
        Assertions.assertThat(term.getPercentageOfTotalBudget()).isEqualTo(0);

    }
}