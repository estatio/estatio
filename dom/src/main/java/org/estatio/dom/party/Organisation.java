/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.PublishedObject;

import org.estatio.dom.JdoColumnLength;

@javax.jdo.annotations.PersistenceCapable // identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
//no @DatastoreIdentity nor @Version, since inherited from supertype
@PublishedObject(OrganisationChangedPayloadFactory.class)
public class Organisation extends Party {

    private String fiscalCode;

    @javax.jdo.annotations.Column(length=JdoColumnLength.Organisation.FISCAL_CODE)
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

    @javax.jdo.annotations.Column(length=JdoColumnLength.Organisation.VAT_CODE)
    @Optional
    @MemberOrder(sequence = "1")
    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(final String vatCode) {
        this.vatCode = vatCode;
    }

}
