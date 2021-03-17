/**
 * Defines {@link org.estatio.dom.EstatioImmutableObject reference data} {@link org.estatio.module.index.dom.Index} entity,
 * which in turn {@link org.estatio.module.index.dom.Index#getIndexBases() aggregates} {@link org.estatio.module.index.dom.IndexBase},
 * which in turn {@link org.estatio.module.index.dom.IndexBase#getValues() aggregates}
 * {@link org.estatio.module.index.dom.IndexValue}.
 * 
 * <p>
 * Indices are used in the calculation of <tt>Invoice</tt>s for certain <tt>Lease</tt>s: specifically, those with 
 * <tt>LeaseItem</tt>s of type <i>RENT</i> type, giving rise to terms implemented by <tt>LeaseTermForIndexableRent</tt>.
 */
package org.estatio.module.index.dom;