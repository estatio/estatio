/**
 * Defines a set of semi- {@link org.estatio.dom.EstatioImmutableObject reference data} like entities - 
 * {@link org.estatio.dom.lease.tags.UnitSize UnitSize}, {@link org.estatio.dom.lease.tags.Brand Brand}, 
 * {@link org.estatio.dom.lease.tags.Sector Sector} and {@link org.estatio.dom.lease.tags.Activity Activity} that 
 * characterize (or tag) a particular {@link org.estatio.module.lease.dom.Occupancy Occupancy}.
 *
 * <p>
 * {@link org.estatio.dom.lease.tags.Sector Sector} 
 * {@link org.estatio.dom.lease.tags.Sector#getActivities() aggregates}   
 * {@link org.estatio.dom.lease.tags.Activity Activity}.  The other entities are standalone.
 * 
 * <p>
 * New characteristics can be created as necessary, but existing instances of these entities are offered as choices
 * in order (to attempt to) use a canonical set of characteristics.
 */
package org.estatio.dom.lease.tags;