/**
 * Defines {@link org.estatio.asset.dom.FixedAsset asset}s, with {@link org.estatio.asset.dom.Property Property} and
 * {@link org.estatio.asset.dom.Unit} being the two subtypes.
 * 
 * <p>
 * Each property has {@link org.estatio.asset.dom.Property#getUnits() associated} units.
 *
 * <p>
 * Every {@link org.estatio.asset.dom.FixedAsset} (property and unit) can be
 * {@link org.estatio.asset.dom.FixedAsset#getRoles() associated} {@link org.estatio.dom.party.Party}, for example
 * the <i>TENANT</i> or the <i>LANDLORD</i>.  The party playing each role can change over time.  
 * 
 * <p>
 * Each property has a {@link org.estatio.asset.dom.Property#getType() corresponding}
 * {@link org.estatio.asset.dom.PropertyType}; each unit has a
 * {@link org.estatio.asset.dom.Unit#getType() corresponding} {@link org.estatio.asset.dom.UnitType}.
 */
package org.estatio.asset.dom;