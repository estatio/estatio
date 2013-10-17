/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
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
package org.estatio.dom.party;

import javax.jdo.annotations.DiscriminatorStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.PublishedObject;

@javax.jdo.annotations.PersistenceCapable // identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME, 
        column="discriminator")
@javax.jdo.annotations.Queries({ 
    @javax.jdo.annotations.Query(
            name = "findByReferenceOrName", language = "JDOQL", 
            value = "SELECT "
                    + "FROM org.estatio.dom.party.Organisation "
                    + "WHERE reference.matches(:referenceOrName) "
                    + "   || name.matches(:referenceOrName)")
})
@PublishedObject(OrganisationChangedPayloadFactory.class)
public class Organisation extends Party {

    private String fiscalCode;

    @Optional
    @MemberOrder(sequence = "1")
    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(final String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    // //////////////////////////////////////

    private String vatCode;

    @Optional
    @MemberOrder(sequence = "1")
    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(final String vatCode) {
        this.vatCode = vatCode;
    }

}
