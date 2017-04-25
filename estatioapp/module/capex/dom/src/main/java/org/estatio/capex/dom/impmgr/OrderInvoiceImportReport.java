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
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.project.ProjectRepository;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.capex.dom.impmgr.OrderInvoiceImportReport"
)
public class OrderInvoiceImportReport {

    public String title(){
        return "Report of orders and incoming invoices";
    }

    public List<OrderInvoiceImportReportLine> getLines(){
        List<OrderInvoiceImportReportLine> result = new ArrayList<>();
        Integer numberOfOrderlines = 0;
        Integer numberOfinvoicelines = 0;
        BigDecimal orderNetTotal = BigDecimal.ZERO;
        BigDecimal orderVatTotal = BigDecimal.ZERO;
        BigDecimal orderGrossTotal = BigDecimal.ZERO;
        BigDecimal invoiceNetTotal = BigDecimal.ZERO;
        BigDecimal invoiceVatTotal = BigDecimal.ZERO;
        BigDecimal invoiceGrossTotal = BigDecimal.ZERO;

        for (Project project : projectRepository.listAll()) {
            for (String period : periodsPresent(project)) {
                for (OrderItem orderItem : orderItemRepository.findByProject(project)) {
                    if (orderItem.getPeriod().equals(period)) {
                        numberOfOrderlines = numberOfOrderlines + 1;
                        orderNetTotal = orderNetTotal.add(orderItem.getNetAmount());
                        orderVatTotal = orderVatTotal.add(orderItem.getVatAmount());
                        orderGrossTotal = orderGrossTotal.add(orderItem.getGrossAmount());
                    }
                }
                for (IncomingInvoiceItem invoiceItem : incomingInvoiceItemRepository.findByProject(project)) {
                    if (invoiceItem.getPeriod().equals(period)) {
                        numberOfinvoicelines = numberOfinvoicelines + 1;
                        invoiceNetTotal = invoiceNetTotal.add(invoiceItem.getNetAmount());
                        invoiceVatTotal = invoiceVatTotal.add(invoiceItem.getVatAmount());
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
