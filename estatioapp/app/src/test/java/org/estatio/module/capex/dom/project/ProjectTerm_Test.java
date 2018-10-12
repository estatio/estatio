package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProjectTerm_Test {

    @Test
    public void getPercentageOfTotalBudget() {

        // given
        Project project = new Project(){
            @Override
            public BigDecimal getBudgetedAmount() {
                return new BigDecimal("12345.56");
            }
        };

        ProjectTerm term = new ProjectTerm();
        term.setProject(project);

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
        Project project = new Project(){
            @Override
            public BigDecimal getBudgetedAmount() {
                return new BigDecimal("0.00");
            }
        };

        ProjectTerm term = new ProjectTerm();
        term.setProject(project);

        // when
        term.setBudgetedAmount(new BigDecimal("1000.12"));
        // then
        Assertions.assertThat(term.getPercentageOfTotalBudget()).isEqualTo(0);


    }

    @Test
    public void getPercentageOfTotalBudget_when_no_budgeted_amount() {

        // given
        Project project = new Project(){
            @Override
            public BigDecimal getBudgetedAmount() {
                return null;
            }
        };

        ProjectTerm term = new ProjectTerm();
        term.setProject(project);

        // when
        term.setBudgetedAmount(new BigDecimal("1000.12"));
        // then
        Assertions.assertThat(term.getPercentageOfTotalBudget()).isEqualTo(0);

    }
}