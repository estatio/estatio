package org.estatio.module.budgetassignment.app;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceItem;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
public class InvoiceLinkManager {

    public InvoiceLinkManager(){}

    public InvoiceLinkManager(final Budget budget){
        this.budget = budget;
    }

    public String title(){
        return "Link unlinked invoice items to budget";
    }

    @Getter @Setter
    private Budget budget;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<IncomingInvoiceItem> getUnlinkedInvoiceItemCandidates(){
        List<IncomingInvoiceItem> result = new ArrayList<>();
        incomingInvoiceRepository.findByPropertyAndInvoiceDateBetween(budget.getProperty(), budget.getStartDate(), budget.getEndDate())
                .forEach(i->{
                    for (InvoiceItem invoiceItem : i.getItems()){
                        IncomingInvoiceItem castedItem = (IncomingInvoiceItem) invoiceItem;
                        if (castedItem.getCharge()!=null && castedItem.getBudgetItem()==null){
                            for (BudgetItem budgetItem : budget.getItems()){
                                if (budgetItem.getCharge()==castedItem.getCharge()){
                                    result.add(castedItem);
                                }
                            }
                        }
                    }
                });
        return result;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT, associateWith = "unlinkedInvoiceItemCandidates", associateWithSequence = "1")
    public void linkToBudgetItem(@Nullable final List<IncomingInvoiceItem> invoiceItems){
        for (IncomingInvoiceItem invoiceItem : invoiceItems){
            for (BudgetItem budgetItem : budget.getItems()){
                if (budgetItem.getCharge()==invoiceItem.getCharge()){
                    invoiceItem.setBudgetItem(budgetItem);
                }
            }
        }
    }

    @Inject IncomingInvoiceRepository incomingInvoiceRepository;

}
