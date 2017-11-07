package org.estatio.capex.dom.order.itemmixins;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.Digits;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.capex.dom.documents.BudgetItemChooser;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.tax.dom.Tax;

@Mixin(method = "Act")
public class Order_itemDetails {

    private final Order order;

    public Order_itemDetails(Order order) {
        this.order = order;
    }

    @Action()
    public Order act(
            final String description,
            @Digits(integer=13, fraction = 2)
            final BigDecimal netAmount,
            @Digits(integer=13, fraction = 2)
            @Nullable
            final BigDecimal vatAmount,
            @Nullable
            final Tax tax,
            @Digits(integer=13, fraction = 2)
            final BigDecimal grossAmount,
            final Charge charge,
            @Nullable
            final Project project,
            @Nullable
            final BudgetItem budgetItem,
            final String period) {
        if (order.getItems().size()<=1){
            orderItemRepository.upsert(
                    order,
                    charge,
                    description,
                    netAmount,
                    vatAmount,
                    grossAmount,
                    tax,
                    PeriodUtil.yearFromPeriod(period).startDate(),
                    PeriodUtil.yearFromPeriod(period).endDate(),
                    order.getProperty(),
                    project,
                    budgetItem
            );
        }
        return order;
    }

    public String default0Act(){
        return order.firstItemIfAny().isPresent() ? order.firstItemIfAny().get().getDescription() : null;
    }

    public BigDecimal default1Act(){
        return order.firstItemIfAny().isPresent() ? order.firstItemIfAny().get().getNetAmount() :null;
    }

    public BigDecimal default2Act(){
        return order.firstItemIfAny().isPresent() ? order.firstItemIfAny().get().getVatAmount() :null;
    }

    public Tax default3Act(){
        return order.firstItemIfAny().isPresent() ? order.firstItemIfAny().get().getTax() :null;
    }

    public BigDecimal default4Act(){
        return order.firstItemIfAny().isPresent() ? order.firstItemIfAny().get().getGrossAmount() : null;
    }

    public Charge default5Act(){
        return order.firstItemIfAny().isPresent() ? order.firstItemIfAny().get().getCharge() : null;
    }

    public Project default6Act(){
        return order.firstItemIfAny().isPresent() ? order.firstItemIfAny().get().getProject() : null;
    }

    public BudgetItem default7Act(){
        return order.firstItemIfAny().isPresent() ? order.firstItemIfAny().get().getBudgetItem() : null;
    }

    public String default8Act(){
        return order.firstItemIfAny().isPresent() ? PeriodUtil.periodFromInterval(new LocalDateInterval(order.firstItemIfAny().get().getStartDate(), order.firstItemIfAny().get().getEndDate())) : null;
    }

    public List<BudgetItem> choices7Act(
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final Tax tax,
            final BigDecimal grossAmount,
            final Charge charge,
            final Project project,
            final BudgetItem budgetItem,
            final String period) {
        return budgetItemChooser.choicesBudgetItemFor(order.firstItemIfAny().isPresent() ? order.firstItemIfAny().get().getProperty() : null, charge);
    }

    public String disableAct() {
        if (order.getItems().size()>1){
            return "More than 1 item - make changes on an item directly";
        }
        return order.reasonDisabledDueToState();
    }

    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    BudgetItemChooser budgetItemChooser;

}
