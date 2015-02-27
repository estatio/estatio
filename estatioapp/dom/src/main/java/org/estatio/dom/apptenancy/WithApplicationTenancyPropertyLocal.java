/*
 *  Copyright 2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.apptenancy;

/**
 * For entities that are at the most local level under the property in the application tenancy hierarchy, for example
 * <tt>/it/CAR/ta</tt> for the Carasello Tenants Association, or <tt>it/CAR/_</tt> for all Carasello-specific data
 * that is <i>not</i> the purvue of the Carasello TA.
 * 
 * <p>
 *     Examples of entities include {@link org.estatio.dom.lease.LeaseItem}, {@link org.estatio.dom.lease.LeaseTerm},
 *     {@link org.estatio.dom.invoice.InvoiceItem} and {@link org.estatio.dom.invoice.Invoice}.
 * </p>
 */
public interface WithApplicationTenancyPropertyLocal {

}
