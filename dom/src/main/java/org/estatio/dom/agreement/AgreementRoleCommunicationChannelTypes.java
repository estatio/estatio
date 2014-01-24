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
package org.estatio.dom.agreement;

import java.util.List;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.EstatioDomainService;

@Hidden
public class AgreementRoleCommunicationChannelTypes 
        extends EstatioDomainService<AgreementRoleCommunicationChannelType> {

    public AgreementRoleCommunicationChannelTypes() {
        super(AgreementRoleCommunicationChannelTypes.class, AgreementRoleCommunicationChannelType.class);
    }
    
    // //////////////////////////////////////


    @NotContributed
    public AgreementRoleCommunicationChannelType findByTitle(final String title) {
        return firstMatch("findByTitle", "title", title);
    }

    @NotContributed
    public List<AgreementRoleCommunicationChannelType> findApplicableTo(final AgreementType agreementType) {
        return allMatches("findByAgreementType", "agreementType", agreementType);
    }


}
