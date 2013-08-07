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
package org.estatio.fixture;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

import org.estatio.dom.agreement.AgreementRoleCommunicationChannelType;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexBase;
import org.estatio.dom.index.IndexValue;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;


public class EstatioRefDataObjectsTeardownFixture extends AbstractFixture {

    @Override
    public void install() {
        
        isisJdoSupport.deleteAll(
        State.class,
        Country.class,
        Currency.class,
        Charge.class,
        ChargeGroup.class,
        TaxRate.class,
        Tax.class,
        AgreementRoleCommunicationChannelType.class,
        AgreementRoleType.class,
        AgreementType.class,
        IndexValue.class,
        IndexBase.class,
        Index.class
        );

    }

    /**
     * unused, but equivalent to {@link #truncateTables(IsisJdoSupport)}, above
     */
    @SuppressWarnings("unused")
    private void truncateTablesSQL(IsisJdoSupport isisJdoSupport) {
        isisJdoSupport.executeUpdate("TRUNCATE TABLE \"STATE\"");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE COUNTRY");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE GEOGRAPHY");
        
        isisJdoSupport.executeUpdate("TRUNCATE TABLE CURRENCY");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE CHARGE");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE CHARGEGROUP");
        
        isisJdoSupport.executeUpdate("TRUNCATE TABLE TAXRATE");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE TAX");
        
        isisJdoSupport.executeUpdate("TRUNCATE TABLE AGREEMENTROLETYPE");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE AGREEMENTROLECOMMUNICATIONCHANNELTYPE");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE AGREEMENTTYPE");
        
        isisJdoSupport.executeUpdate("TRUNCATE TABLE INDEXVALUE");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE INDEXBASE");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE \"INDEX\"");
    }

    private IsisJdoSupport isisJdoSupport;
    public void injectIsisJdoSupport(IsisJdoSupport isisJdoSupport) {
        this.isisJdoSupport = isisJdoSupport;
    }

}
