package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.services.message.MessageService2;

import org.estatio.module.charge.dom.ChargeRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.dom.project.ForecastLineViewmodel"
)
@NoArgsConstructor
public class ForecastLineViewmodel {

    public ForecastLineViewmodel(final BudgetForecastItemTerm term){
        this.chargeReference = term.getForecastItem().getProjectItem().getCharge().getReference();
        this.projectReference = term.getForecastItem().getForecast().getProject().getReference();
        this.termStartDate = term.getStartDate();
        this.termEndDate = term.getEndDate();
        this.forecastedAmount = term.getForecastItem().getAmount();
        this.termAmount = term.getAmount();
        this.forecastedAmountCovered = term.getForecastItem().getForecastedAmountCovered();
        this.sumTerms = term.getForecastItem().getSumTerms();
    }

    public ForecastLineViewmodel(final ProjectBudgetItem budgetItem, final LocalDate startDate, final BigDecimal forecastedAmount){
        this.chargeReference = budgetItem.getProjectItem().getCharge().getReference();
        this.projectReference = budgetItem.getProjectBudget().getProject().getReference();
        this.termStartDate = startDate;
        this.termEndDate = startDate.plusMonths(3).minusDays(1);
        this.forecastedAmount = forecastedAmount;
    }

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String projectReference;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String chargeReference;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private BigDecimal forecastedAmount;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private LocalDate termStartDate;

    @Getter @Setter
    @MemberOrder(sequence = "5")
    private LocalDate termEndDate;

    @Getter @Setter
    @MemberOrder(sequence = "6")
    private BigDecimal termAmount;

    @Getter @Setter
    @MemberOrder(sequence = "7")
    private boolean forecastedAmountCovered;

    @Getter @Setter
    @MemberOrder(sequence = "8")
    private BigDecimal sumTerms;

    public void importData(final Project project) {

    }

    @Inject
    private ProjectBudgetRepository projectBudgetRepository;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private MessageService2 messageService2;

}
