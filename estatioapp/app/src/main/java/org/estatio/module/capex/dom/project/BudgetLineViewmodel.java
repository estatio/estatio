package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.services.message.MessageService2;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ViewModel
@NoArgsConstructor
public class BudgetLineViewmodel {

    public BudgetLineViewmodel(final ProjectBudgetItem budgetItem){
        this.chargeReference = budgetItem.getProjectItem().getCharge().getReference();
        this.amount = budgetItem.getAmount();
        this.projectReference = budgetItem.getProjectBudget().getProject().getReference();
        this.budgetVersion = budgetItem.getProjectBudget().getBudgetVersion();
    }

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String projectReference;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String chargeReference;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private Integer budgetVersion;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private BigDecimal amount;


    public void importData(final Project project) {
        Project projectFromRef = null;
        if (project==null){
            projectFromRef = projectRepository.findByReference(getProjectReference());
            if (projectFromRef==null){
                messageService2.raiseError(String.format("Project not found for reference", getProjectReference()));
                return;
            }
        }

        Project projectToUse = project!=null ? project : projectFromRef;

        if (getBudgetVersion()==null) {
            messageService2.raiseError("Budget version is mandatory");
            return;
        }

        if (getBudgetVersion()!=1 && projectToUse.getLatestCommittedBudget()==null){
            messageService2.raiseError(String.format("No committed budget found for %s; budget version should be 1", getProjectReference()));
            return;
        }

        if (projectToUse.getProjectBudget()!=null && projectToUse.getProjectBudget().getBudgetVersion()!=getBudgetVersion()){
            messageService2.raiseError(String.format("Budget for %s should have version %s", getProjectReference(), projectToUse.getProjectBudget().getBudgetVersion()));
            return;
        }

        if (projectToUse.getLatestCommittedBudget()!=null && projectToUse.getLatestCommittedBudget().getBudgetVersion()+1!=getBudgetVersion()){
            messageService2.raiseError(String.format("Budget for %s should have version %s", getProjectReference(), projectToUse.getLatestCommittedBudget().getBudgetVersion()+1));
            return;
        }

        final ProjectBudget unapprovedBudget = projectBudgetRepository.findOrCreate(projectToUse, getBudgetVersion());
        if (unapprovedBudget.getApprovedOn()!=null){
            messageService2.raiseError("You are trying to modify an approved budget");
            return;
        }

        final Charge charge = chargeRepository.findByReference(getChargeReference());
        if (charge==null) {
            messageService2.raiseError(String.format("Charge with reference %s not found", getChargeReference()));
            return;
        }

        final ProjectBudgetItem projectBudgetItem = Lists.newArrayList(unapprovedBudget.getItems()).stream()
                .filter(bi -> bi.getProjectItem().getCharge().getReference().equals(getChargeReference()))
                .findFirst().orElse(null);
        if (projectBudgetItem!=null && getAmount()!=null){
            projectBudgetItem.setAmount(getAmount());
        } else {
            // should not happen
            messageService2.raiseError(String.format("Budget item for charge %s not found", getChargeReference()));
            return;
        }

    }

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private ProjectBudgetRepository projectBudgetRepository;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private MessageService2 messageService2;

}
