/**
 * Defines {@link org.estatio.dom.EstatioRefDataObject reference data} {@link org.estatio.dom.index.Index} entity,
 * which in turn {@link org.estatio.dom.index.Index#getIndexBases() aggregates} {@link org.estatio.dom.index.IndexBase},
 * which in turn {@link org.estatio.dom.index.IndexBase#getValues() aggregates} 
 * {@link org.estatio.dom.index.IndexValue}.
 * 
 * <p>
 * Indices are used in the calculation of <tt>Invoice</tt>s for certain <tt>Lease</tt>s: specifically, those with 
 * <tt>LeaseItem</tt>s of type <i>RENT</i> type, giving rise to terms implemented by <tt>LeaseTermForIndexableRent</tt>.
 */
package org.estatio.dom.index;