package org.estatio.module.capex.app.project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.user.UserService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.capex.dom.invoice.contributions.Project_InvoiceItemsNotOnProjectItem;
import org.estatio.module.capex.dom.order.contributions.Project_OrderItemsNotOnProjectItem;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.charge.dom.Charge;

@Mixin
public class Project_CreateMissingItems {

    private final Project project;

    public Project_CreateMissingItems(Project project) {
        this.project = project;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @MemberOrder(name="items", sequence = "2")
    public Project $$(final Property property, final List<Charge> charges) {
        charges.forEach(x->{
            project.addItem(x, x.getDescription(), null, null, null, property, null);
        });
        return project;
    }

    public List<Charge> default1$$(){
        return chargesLinkedNotOnItems();
    }

    public List<Charge> choices1$$(){
        return chargesLinkedNotOnItems();
    }

    public String disable$$(){
        if (!EstatioRole.SUPERUSER.isApplicableFor(userService.getUser())) return "You need super user rights for this action";
        if (project.isParentProject()) return "This is a parent project";
        if (chargesLinkedNotOnItems().isEmpty()) return "No unmapped charges found on linked order or invoice items";
        return null;
    }

    private List<Charge> chargesLinkedNotOnItems(){
        List<Charge> result = new ArrayList<>();
        Project_OrderItemsNotOnProjectItem orderItemsMixin = new Project_OrderItemsNotOnProjectItem(project);
        serviceRegistry2.injectServicesInto(orderItemsMixin);
        for (Charge chargeCandidate : orderItemsMixin.orderItemsNotOnProjectItem().stream()
                .filter(x->x.getCharge()!=null)
                .map(x->x.getCharge()).collect(Collectors.toList())){
            if (!result.contains(chargeCandidate)) {
                result.add(chargeCandidate);
            }
        }
        Project_InvoiceItemsNotOnProjectItem invoiceItemsMixin = new Project_InvoiceItemsNotOnProjectItem(project);
        serviceRegistry2.injectServicesInto(invoiceItemsMixin);
        for (Charge chargeCandidate : invoiceItemsMixin.invoiceItemsNotOnProjectItem().stream()
                .filter(x->x.getCharge()!=null)
                .map(x->x.getCharge()).collect(Collectors.toList())){
            if (!result.contains(chargeCandidate)) {
                result.add(chargeCandidate);
            }
        }
        return result;
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    UserService userService;

}
