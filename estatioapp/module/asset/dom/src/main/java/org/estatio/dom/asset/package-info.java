/**
 * Defines {@link org.estatio.dom.asset.FixedAsset asset}s, with {@link org.estatio.dom.asset.Property Property} and
 * {@link org.estatio.dom.asset.Unit} being the two subtypes.
 * 
 * <p>
 * Each property has {@link org.estatio.dom.asset.Property#getUnits() associated} units. 
 *
 * <p>
 * Every {@link org.estatio.dom.asset.FixedAsset} (property and unit) can be 
 * {@link org.estatio.dom.asset.FixedAsset#getRoles() associated} {@link org.estatio.module.party.dom.Party}, for example
 * the <i>TENANT</i> or the <i>LANDLORD</i>.  The party playing each role can change over time.  
 * 
 * <p>
 * Each property has a {@link org.estatio.dom.asset.Property#getType() corresponding} 
 * {@link org.estatio.dom.asset.PropertyType}; each unit has a 
 * {@link org.estatio.dom.asset.Unit#getType() corresponding} {@link org.estatio.dom.asset.UnitType}. 
 */
package org.estatio.dom.asset;