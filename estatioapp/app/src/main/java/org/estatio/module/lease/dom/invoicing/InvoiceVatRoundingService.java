package org.estatio.module.lease.dom.invoicing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

@DomainService(nature = NatureOfService.DOMAIN)
public class InvoiceVatRoundingService {

    /**
     * In order to comply with Italian rules for vat rounding, this service compares the vat calculation
     * for each line with the calculation on the lines grouped by vat percentage.
     * Any differences caused by rounding on line level will be 'distributed' over the lines for the same vat percentage.
     *
     * @param invoice
     * @return
     */
    public InvoiceForLease distributeVatRoundingByVatPercentage(final InvoiceForLease invoice){

        //only apply to italian invoices - extra safeguard here
        if (!invoice.getAtPath().startsWith("/ITA")) return invoice;

        List<BigDecimal> distinctVatPercentages = distinctPercentagesOn(invoice);
        distinctVatPercentages.forEach(p->{
            List<InvoiceItemForLease> itemsWithVatPercentage = itemsWithVatPercentage(invoice, p);
            recalculateVatIfNeeded(itemsWithVatPercentage, p);
        });

        return invoice;
    }

    List<InvoiceItemForLease> itemsWithVatPercentage(final InvoiceForLease invoice, final BigDecimal percentage){
        List<InvoiceItemForLease> itemsWithVatPercentage = new ArrayList<>();
        new ArrayList<>(invoice.getItems()).forEach(ii->{
            if (ii.getTaxRate()!=null) {
                if (ii.getTaxRate().getPercentage().equals(percentage)) itemsWithVatPercentage.add((InvoiceItemForLease) ii);
            }
        });
        return itemsWithVatPercentage;
    }

    List<BigDecimal> distinctPercentagesOn(final InvoiceForLease invoice) {
        List<BigDecimal> distinctVatPercentages = new ArrayList<>();
        new ArrayList<>(invoice.getItems()).forEach(ii->{
            if (ii.getTaxRate()!=null) {
                final BigDecimal percentage = ii.getTaxRate().getPercentage();
                if (!distinctVatPercentages.contains(percentage)) distinctVatPercentages.add(percentage);
            }
        });
        return distinctVatPercentages;
    }

    void recalculateVatIfNeeded(final List<InvoiceItemForLease> items, final BigDecimal percentage){

        final BigDecimal delta = deltaVatCalculationForPercentage(items, percentage);
        if (delta.compareTo(BigDecimal.ZERO)>0){
            // too much vat on lines - we subtract from the highest amounts
            List<InvoiceItemForLease> sortedItems = items.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            for (int i=0; i < numberOfIterations(delta); i++){
                InvoiceItemForLease itemToCorrect = sortedItems.get(0);
                BigDecimal correctedVatAmount = itemToCorrect.getVatAmount().subtract(new BigDecimal("0.01"));
                BigDecimal correctedGrossAmount = itemToCorrect.getGrossAmount().subtract(new BigDecimal("0.01"));
                itemToCorrect.setVatAmount(correctedVatAmount);
                itemToCorrect.setGrossAmount(correctedGrossAmount);
                sortedItems.remove(itemToCorrect);
            }
        } else {
            if (delta.compareTo(BigDecimal.ZERO)<0){
                // too little vat on lines - this can only happen when having credit lines (negative net amounts); we add to the lowest amounts
                List<InvoiceItemForLease> sortedItems = items.stream().sorted().collect(Collectors.toList());
                for (int i=0; i < numberOfIterations(delta); i++){
                    InvoiceItemForLease itemToCorrect = sortedItems.get(0);
                    BigDecimal correctedVatAmount = itemToCorrect.getVatAmount().add(new BigDecimal("0.01"));
                    BigDecimal correctedGrossAmount = itemToCorrect.getGrossAmount().add(new BigDecimal("0.01"));
                    itemToCorrect.setVatAmount(correctedVatAmount);
                    itemToCorrect.setGrossAmount(correctedGrossAmount);
                    sortedItems.remove(itemToCorrect);
                }
            }
        }
    }

    BigDecimal deltaVatCalculationForPercentage(final List<InvoiceItemForLease> items, final BigDecimal percentage){
        final BigDecimal target = totalNetAmountFor(items).multiply(percentage).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP);
        return totalVatAmountFor(items).subtract(target);
    }

    private BigDecimal totalNetAmountFor(final List<InvoiceItemForLease> items) {
        return items.isEmpty() ? BigDecimal.ZERO : items.stream().map(InvoiceItemForLease::getNetAmount).reduce(BigDecimal::add).get();
    }

    private BigDecimal totalVatAmountFor(final List<InvoiceItemForLease> items) {
        return items.isEmpty() ? BigDecimal.ZERO : items.stream().map(InvoiceItemForLease::getVatAmount).reduce(BigDecimal::add).get();
    }

    private int numberOfIterations(final BigDecimal delta) {
        return delta.abs().divide(new BigDecimal("0.01")).intValue();
    }

}
