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
package org.estatio.dom.lease;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.FatalException;

import org.estatio.dom.PowerType;
import org.estatio.dom.utils.StringUtils;


public enum LeaseItemType implements PowerType<LeaseTerm>{

    RENT(LeaseTermForIndexableRent.class), 
    TURNOVER_RENT(LeaseTermForTurnoverRent.class),
    SERVICE_CHARGE(LeaseTermForServiceCharge.class),
    SERVICE_CHARGE_INDEXABLE(LeaseTermForIndexableRent.class), 
    DISCOUNT(LeaseTermForFixed.class),
    TAX(LeaseTermForTax.class);

    private final Class<? extends LeaseTerm> clss;

    private LeaseItemType(final Class<? extends LeaseTerm> clss) {
        this.clss = clss;
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    // //////////////////////////////////////
    
    public LeaseTerm create(final DomainObjectContainer container){ 
        try {
            LeaseTerm term = container.newTransientInstance(clss);
            return term;
        } catch (Exception ex) {
            throw new FatalException(ex);
        }
    }
}
