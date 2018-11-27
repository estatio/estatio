package org.estatio.module.capex.app;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.security.app.user.MeService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "incomingInvoice.IncomingInvoiceMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Invoices In",
        menuOrder = "65.2"
)
public class IncomingInvoiceMenu {

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<IncomingInvoice> allInvoices() {
        return incomingInvoiceRepository.listAll();
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<IncomingInvoice> findInvoice(
            @Nullable final String barcode,
            @Nullable final String sellerNameOrReference,
            @ParameterLayout(named = "Gross Amount (Approximately)")
            @Nullable final BigDecimal grossAmount,
            @ParameterLayout(named = "Invoice Date (Approximately)")
            @Nullable final LocalDate invoiceDate
    ) {
        return new IncomingInvoiceFinder(invoiceRepository, incomingInvoiceRepository, partyRepository)
                .filterOrFindByDocumentName(barcode)
                .filterOrFindBySeller(sellerNameOrReference)
                .filterOrFindByGrossAmount(grossAmount)
                .filterOrFindByInvoiceDate(invoiceDate)
                .getResult();
    }

    public String validateFindInvoice(final String barcode, final String sellerNameOfReference, final BigDecimal grossAmount, final LocalDate invoiceDate) {
        if (barcode != null && barcode.length() < 3) {
            return "Give at least 3 characters for barcode (document name)";
        }
        if (sellerNameOfReference != null && sellerNameOfReference.length() < 3) {
            return "Give at least 3 characters for seller name or reference";
        }
        return null;
    }

    static class IncomingInvoiceFinder {

        public IncomingInvoiceFinder(InvoiceRepository invoiceRepository, IncomingInvoiceRepository incomingInvoiceRepository, PartyRepository partyRepository) {
            this.result = new ArrayList<>();
            this.incomingInvoiceRepository = incomingInvoiceRepository;
            this.partyRepository = partyRepository;
            this.invoiceRepository = invoiceRepository;
        }

        @Getter @Setter
        List<IncomingInvoice> result;

        IncomingInvoiceRepository incomingInvoiceRepository;

        PartyRepository partyRepository;

        InvoiceRepository invoiceRepository;

        IncomingInvoiceFinder filterOrFindByDocumentName(final String barcode) {
            if (barcode == null)
                return this;

            List<IncomingInvoice> resultsForBarcode = incomingInvoiceRepository.findIncomingInvoiceByDocumentName(barcode);
            if (!this.result.isEmpty()) {
                filterByDocumentNameResults(resultsForBarcode);
            } else {
                setResult(resultsForBarcode);
            }
            return this;
        }

        IncomingInvoiceFinder filterOrFindBySeller(final String sellerNameOrReference) {
            if (sellerNameOrReference == null || sellerNameOrReference.equals(""))
                return this;

            List<Organisation> sellerCandidates =
                    partyRepository.findParties("*".concat(sellerNameOrReference).concat("*"))
                            .stream()
                            .filter(Organisation.class::isInstance)
                            .map(Organisation.class::cast)
                            .collect(Collectors.toList());

            if (!this.result.isEmpty()) {
                filterBySellerCandidates(sellerCandidates);
            } else {
                createResultForSellerCandidates(sellerCandidates);
            }

            return this;
        }

        IncomingInvoiceFinder filterOrFindByGrossAmount(final BigDecimal grossAmount) {
            if (grossAmount == null)
                return this;

            BigDecimal grossAmountMin;
            BigDecimal grossAmountMax;

            if (grossAmount.equals(BigDecimal.ZERO)) {
                grossAmountMin = BigDecimal.ZERO;
                grossAmountMax = BigDecimal.ZERO;
            } else {
                grossAmountMin = subtractFivePercent(grossAmount);
                grossAmountMax = addFivePercent(grossAmount);
            }

            if (!this.result.isEmpty()) {
                filterByGrossAmount(grossAmountMin, grossAmountMax);
            } else {
                createResultForGrossAmount(grossAmountMin, grossAmountMax);
            }

            return this;
        }

        BigDecimal subtractFivePercent(final BigDecimal amount) {
            return amount.subtract(amount.abs().divide(new BigDecimal("20"), MathContext.DECIMAL64).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        BigDecimal addFivePercent(final BigDecimal amount) {
            return amount.add(amount.abs().divide(new BigDecimal("20"), MathContext.DECIMAL64).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        IncomingInvoiceFinder filterOrFindByInvoiceDate(final LocalDate invoiceDate) {
            if (invoiceDate == null)
                return this;
            LocalDate invoiceDateStart = invoiceDate.minusDays(5);
            LocalDate invoiceDateEnd = invoiceDate.plusDays(5);
            if (!this.result.isEmpty()) {
                filterByInvoiceDate(invoiceDateStart, invoiceDateEnd);
            } else {
                createResultForInvoiceDate(invoiceDateStart, invoiceDateEnd);
            }
            return this;
        }

        void filterByDocumentNameResults(List<IncomingInvoice> resultsForBarcode) {
            setResult(
                    this.result
                            .stream()
                            .filter(x -> resultsForBarcode.contains(x))
                            .collect(Collectors.toList())
            );
        }

        void filterBySellerCandidates(final List<Organisation> sellerCandidates) {
            if (sellerCandidates.isEmpty()) {
                // reset result
                this.result = new ArrayList<>();
            } else {
                // filter result
                Predicate<IncomingInvoice> isInSellerCandidatesList =
                        x -> sellerCandidates.contains(x.getSeller());
                setResult(result.stream().filter(isInSellerCandidatesList).collect(Collectors.toList()));
            }
        }

        void createResultForSellerCandidates(final List<Organisation> sellerCandidates) {
            for (Party candidate : sellerCandidates) {
                this.result.addAll(
                        invoiceRepository.findBySeller(candidate)
                                .stream()
                                .filter(IncomingInvoice.class::isInstance)
                                .map(IncomingInvoice.class::cast)
                                .collect(Collectors.toList())
                );
            }
        }

        void filterByGrossAmount(final BigDecimal grossAmountMin, final BigDecimal grossAmountMax) {
            Predicate<IncomingInvoice> hasGrossAmount = x -> x.getGrossAmount() != null;
            Predicate<IncomingInvoice> equalOrGreaterThanMin = x -> x.getGrossAmount().compareTo(grossAmountMin) >= 0;
            Predicate<IncomingInvoice> equalOrSmallerThanMax = x -> x.getGrossAmount().compareTo(grossAmountMax) <= 0;
            setResult(
                    this.result
                            .stream()
                            .filter(hasGrossAmount)
                            .filter(equalOrGreaterThanMin)
                            .filter(equalOrSmallerThanMax)
                            .collect(Collectors.toList())
            );
        }

        void createResultForGrossAmount(final BigDecimal grossAmountMin, final BigDecimal grossAmountMax) {
            Predicate<IncomingInvoice> hasGrossAmount = x -> x.getGrossAmount() != null;
            Predicate<IncomingInvoice> equalOrGreaterThanMin = x -> x.getGrossAmount().compareTo(grossAmountMin) >= 0;
            Predicate<IncomingInvoice> equalOrSmallerThanMax = x -> x.getGrossAmount().compareTo(grossAmountMax) <= 0;
            setResult(
                    incomingInvoiceRepository.listAll()
                            .stream()
                            .filter(hasGrossAmount)
                            .filter(equalOrGreaterThanMin)
                            .filter(equalOrSmallerThanMax)
                            .collect(Collectors.toList())
            );
        }

        void filterByInvoiceDate(final LocalDate invoiceDateStart, final LocalDate invoiceDateEnd) {
            Predicate<IncomingInvoice> hasInvoiceDate = x -> x.getInvoiceDate() != null;
            Predicate<IncomingInvoice> invoiceDateInInterval = x -> new LocalDateInterval(invoiceDateStart, invoiceDateEnd).contains(x.getInvoiceDate());
            setResult(
                    this.result
                            .stream()
                            .filter(hasInvoiceDate)
                            .filter(invoiceDateInInterval)
                            .collect(Collectors.toList())
            );
        }

        void createResultForInvoiceDate(final LocalDate invoiceDateStart, final LocalDate invoiceDateEnd) {
            Predicate<IncomingInvoice> hasInvoiceDate = x -> x.getInvoiceDate() != null;
            Predicate<IncomingInvoice> invoiceDateInInterval = x -> new LocalDateInterval(invoiceDateStart, invoiceDateEnd).contains(x.getInvoiceDate());
            setResult(
                    incomingInvoiceRepository.listAll()
                            .stream()
                            .filter(hasInvoiceDate)
                            .filter(invoiceDateInInterval)
                            .collect(Collectors.toList())
            );
        }

    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<IncomingInvoice> findInvoicesByInvoiceDateBetween(final LocalDate fromDate, final LocalDate toDate) {
        return incomingInvoiceRepository.findByInvoiceDateBetween(fromDate, toDate);
    }

    public LocalDate default0FindInvoicesByInvoiceDateBetween() {
        return clockService.now().minusMonths(3);
    }

    public LocalDate default1FindInvoicesByInvoiceDateBetween() {
        return clockService.now();
    }

    ///////////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    public List<IncomingInvoice> findInvoicesByDueDateBetween(final LocalDate fromDate, final LocalDate toDate) {
        return incomingInvoiceRepository.findByDueDateBetween(fromDate, toDate);
    }

    public LocalDate default0FindInvoicesByDueDateBetween() {
        return clockService.now().minusMonths(3);
    }

    public LocalDate default1FindInvoicesByDueDateBetween() {
        return clockService.now();
    }

    ///////////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    public List<IncomingInvoice> findInvoicesByPropertyAndDateReceivedBetween(final Property property, final LocalDate fromDate, final LocalDate toDate) {
        return incomingInvoiceRepository.findByPropertyAndDateReceivedBetween(property, fromDate, toDate);
    }

    public LocalDate default1FindInvoicesByPropertyAndDateReceivedBetween() {
        return clockService.now().minusMonths(3);
    }

    public LocalDate default2FindInvoicesByPropertyAndDateReceivedBetween() {
        return clockService.now();
    }

    ///////////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    public List<IncomingInvoice> findInvoicesPayableByBankTransferWithDifferentHistoricalPaymentMethods(
            final LocalDate fromDueDate,
            final LocalDate toDueDate) {
        return incomingInvoiceRepository.findInvoicesPayableByBankTransferWithDifferentHistoricalPaymentMethods(fromDueDate, toDueDate, meService.me().getFirstAtPathUsingSeparator(';'));
    }

    ///////////////////////////////////////////

    @Inject
    ClockService clockService;
    @Inject PartyRepository partyRepository;
    @Inject InvoiceRepository invoiceRepository;
    @Inject IncomingInvoiceRepository incomingInvoiceRepository;
    @Inject MeService meService;

}
