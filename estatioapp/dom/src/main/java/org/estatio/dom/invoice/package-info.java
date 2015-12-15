/**
 * Defines {@link org.estatio.dom.invoice.Invoice} and {@link Invoice#getItems() aggregated}
 * {@link org.estatio.dom.invoice.InvoiceItem}s, where each {@link org.estatio.dom.invoice.InvoiceItem} represents
 * a {@link org.estatio.dom.invoice.InvoiceItem#getCharge() charge} to the 
 * {@link org.estatio.dom.invoice.Invoice#getBuyer() party} for a particular reason.
 * 
 * <p>
 * Every invoice {@link org.estatio.dom.invoice.Invoice#getStatus() has} a
 * {@link org.estatio.dom.invoice.InvoiceStatus status}; this defines a fixed set of statii (
 * <i>NEW</i>, <i>APPROVED</i>, <i>COLLECTED</i> and <i>INVOICED</i>.  After an invoice has been
 * approved, it is collected, whereby the direct debit request is submitted to the accounting system (which takes care
 * of the mechanics of the direct debitting with the banks).  The final status indicates that the invoice has been 
 * posted into the general ledger by the accounting system.
 * 
 * <p>
 * Each invoice is named by {@link org.estatio.dom.asset.Property},eg <i>CAR-01234</i> for the
 * Property with {@link org.estatio.dom.asset.Property#getReference() reference} of <i>CAR</i>.  The tracking of the 
 * &quot;next&quot; number is managed not by the property but by a {@link org.estatio.dom.numerator.Numerator} 
 * {@link org.estatio.dom.numerator.Numerator#getObjectIdentifier() scoped} to the property.
 */
package org.estatio.dom.invoice;