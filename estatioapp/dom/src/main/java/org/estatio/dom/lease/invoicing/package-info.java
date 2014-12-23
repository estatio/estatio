/**
 * Defines {@link org.estatio.dom.lease.invoicing.InvoiceItemForLease}, which extends the 
 * <tt>invoice</tt> module's {@link org.estatio.dom.invoice.InvoiceItem} and 
 * {@link org.estatio.dom.lease.invoicing.InvoiceItemForLease#getLeaseTerm() associates} it with a 
 * {@link org.estatio.dom.lease.LeaseTerm}.
 * 
 * <p>
 * The <tt>lease</tt> module depends on the <tt>invoice</tt> module, but - to minimize coupling - 
 * the latter is not true.  The {@link org.estatio.dom.lease.invoicing.InvoiceItemForLease} enables a many-to-many 
 * association between {@link org.estatio.dom.lease.LeaseTerm} and {@link org.estatio.dom.invoice.InvoiceItem}.
 */
package org.estatio.dom.lease.invoicing;