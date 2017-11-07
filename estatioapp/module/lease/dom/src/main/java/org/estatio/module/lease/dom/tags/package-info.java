/**
 * Defines a set of semi- {@link org.estatio.dom.EstatioImmutableObject reference data} like entities - 
 * {@link org.estatio.module.lease.dom.tags.UnitSize UnitSize}, {@link org.estatio.module.lease.dom.tags.Brand Brand},
 * {@link org.estatio.module.lease.dom.tags.Sector Sector} and {@link org.estatio.module.lease.dom.tags.Activity Activity} that
 * characterize (or tag) a particular {@link org.estatio.module.lease.dom.occupancy.Occupancy Occupancy}.
 *
 * <p>
 * {@link org.estatio.module.lease.dom.tags.Sector Sector}
 * {@link org.estatio.module.lease.dom.tags.Sector#getActivities() aggregates}
 * {@link org.estatio.module.lease.dom.tags.Activity Activity}.  The other entities are standalone.
 * 
 * <p>
 * New characteristics can be created as necessary, but existing instances of these entities are offered as choices
 * in order (to attempt to) use a canonical set of characteristics.
 */
package org.estatio.module.lease.dom.tags;