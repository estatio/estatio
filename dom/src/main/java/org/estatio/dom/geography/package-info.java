/**
 * Defines the {@link org.estatio.dom.EstatioRefDataObject reference data} {@link org.estatio.dom.geography.Geography}
 * entity, with concrete subtypes {@link org.estatio.dom.geography.Country} and
 * {@link org.estatio.dom.geography.State}.
 * 
 * <p>
 * A {@link org.estatio.dom.geography.Country} consists of {@link org.estatio.dom.geography.State}s, however this
 * relationship is performed through a 
 * {@link org.estatio.dom.geography.StateContributions#states(Country) contributed collection} rather than being a
 * mapped association.
 */
package org.estatio.dom.geography;