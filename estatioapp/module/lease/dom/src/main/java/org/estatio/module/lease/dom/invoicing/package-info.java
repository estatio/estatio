/**
 * Defines {@link org.estatio.module.lease.dom.invoicing.InvoiceItemForLease}, which extends the
 * <tt>invoice</tt> module's {@link org.estatio.module.invoice.dom.InvoiceItem} and
 * {@link org.estatio.module.lease.dom.invoicing.InvoiceItemForLease#getLeaseTerm() associates} it with a
 * {@link org.estatio.module.lease.dom.LeaseTerm}.
 * 
 * <p>
 * The <tt>lease</tt> module depends on the <tt>invoice</tt> module, but - to minimize coupling - 
 * the latter is not true.  The {@link org.estatio.module.lease.dom.invoicing.InvoiceItemForLease} enables a many-to-many
 * association between {@link org.estatio.module.lease.dom.LeaseTerm} and {@link org.estatio.module.invoice.dom.InvoiceItem}.
 */
package org.estatio.module.lease.dom.invoicing;