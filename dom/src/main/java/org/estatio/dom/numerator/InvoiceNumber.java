package org.estatio.dom.numerator;

import java.math.BigInteger;

import org.estatio.dom.invoice.Invoice;

import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

public class InvoiceNumber implements NumeratorStrategy {

    private Invoice invoice;

    public InvoiceNumber(Invoice invoice) {
        this.invoice = invoice;
    }

    public void assign() {
        Numerator numerator = numerators.retrieve(key(), description());
        BigInteger next = numerator.increment();
        invoice.setReference(next.toString());
    }

    @Override
    public String key() {
        Bookmark bookmark = bookmarkService.bookmarkFor(invoice.getSeller());
        return String.format("InvoiceNumber~", bookmark.toString());
    }

    @Override
    public String description() {
        return invoice.getSeller().getReference();
    }

    private BookmarkService bookmarkService;

    public void setBookmarkService(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    private Numerators numerators;

    public void setNumerators(Numerators numerators) {
        this.numerators = numerators;
    }

}
