package org.estatio.capex.dom.impmgr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "orders.OrderInvoiceImportReport"
)
public class OrderInvoiceImportReport {

    public String title(){
        return "Report of orders and incoming invoices";
    }

    public List<OrderInvoiceImportReportLine> getLines(){
        List<OrderInvoiceImportReportLine> result = new ArrayList<>();
        Integer numberOfOrderlines;
        Integer numberOfinvoicelines;
        BigDecimal orderNetTotal;
        BigDecimal orderVatTotal;
        BigDecimal orderGrossTotal;
        BigDecimal invoiceNetTotal;
        BigDecimal invoiceVatTotal;
        BigDecimal invoiceGrossTotal;

        for (Project project : projectRepository.listAll()) {
            for (String period : periodsPresent(project)) {
                numberOfOrderlines = 0;
                numberOfinvoicelines = 0;
                orderNetTotal = BigDecimal.ZERO;
                orderVatTotal = BigDecimal.ZERO;
                orderGrossTotal = BigDecimal.ZERO;
                invoiceNetTotal = BigDecimal.ZERO;
                invoiceVatTotal = BigDecimal.ZERO;
                invoiceGrossTotal = BigDecimal.ZERO;
                for (OrderItem orderItem : orderItemRepository.findByProject(project)) {
                    if (orderItem.getPeriod().equals(period)) {
                        numberOfOrderlines = numberOfOrderlines + 1;
                        orderNetTotal = orderNetTotal.add(orderItem.getNetAmount());
                        orderVatTotal = orderItem.getVatAmount()==null ? orderVatTotal : orderVatTotal.add(orderItem.getVatAmount());
                        orderGrossTotal = orderGrossTotal.add(orderItem.getGrossAmount());
                    }
                }
                for (IncomingInvoiceItem invoiceItem : incomingInvoiceItemRepository.findByProject(project)) {
                    if (invoiceItem.getPeriod().equals(period)) {
                        numberOfinvoicelines = numberOfinvoicelines + 1;
                        invoiceNetTotal = invoiceNetTotal.add(invoiceItem.getNetAmount());
                        invoiceVatTotal = invoiceItem.getVatAmount()==null ? invoiceVatTotal : invoiceVatTotal.add(invoiceItem.getVatAmount());
                        invoiceGrossTotal = invoiceGrossTotal.add(invoiceItem.getGrossAmount());
                    }
                }
                result.add(new OrderInvoiceImportReportLine(
                                project.getReference(),
                                period,
                                numberOfOrderlines,
                                orderNetTotal,
                                orderVatTotal,
                                orderGrossTotal,
                                numberOfinvoicelines,
                                invoiceNetTotal,
                                invoiceVatTotal,
                                invoiceGrossTotal
                        )
                );
            }
        }
        return result;
    }

    private List<String> periodsPresent(final Project project){
        List<String> result = new ArrayList<>();
        for (OrderItem item : orderItemRepository.findByProject(project)){
            if (!result.contains(item.getPeriod())){
                result.add(item.getPeriod());
            }
        }
        for (IncomingInvoiceItem item : incomingInvoiceItemRepository.findByProject(project)){
            if (!result.contains(item.getPeriod())){
                result.add(item.getPeriod());
            }
        }
        Collections.sort(result);
        return result;
    }

    @Inject
    ProjectRepository projectRepository;

    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;

}
