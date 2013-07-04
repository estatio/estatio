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
package org.estatio.dom.communicationchannel;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.geography.Country;

import org.apache.isis.applib.annotation.Hidden;

@Hidden
public class PostalAddresses extends EstatioDomainService<PostalAddress> {

    public PostalAddresses() {
        super(PostalAddresses.class, PostalAddress.class);
    }

    // //////////////////////////////////////

    public CommunicationChannel findByAddress(String address1, String postalCode, String city, Country country) {
        return firstMatch("findByAddress", "address1", address1, "postalCode", postalCode, "city", city, "country", country);
    }

}
