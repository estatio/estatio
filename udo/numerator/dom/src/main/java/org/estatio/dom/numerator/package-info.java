/**
 * Defines the {@link org.estatio.dom.numerator.Numerator Numerator} entity, which is a mechanism for generating
 * a sequence of unique identifiers.
 * 
 * <p>
 * NumeratorMenu can either be global, or can be scoped for any entity object.
 * 
 * <p>
 * Within Estatio, a global numerator is used to generate invoice collection numbers, while a scoped numerator, scoped
 * against properties, is used to generate invoice numbers.  For example, the property with reference <i>CAR</i> has 
 * invoices numbered <i>CAR-00001</i>, <i>CAR-00002</i> etc.
 */
package org.estatio.dom.numerator;