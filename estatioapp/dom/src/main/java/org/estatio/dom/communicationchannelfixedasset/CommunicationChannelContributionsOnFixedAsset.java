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
package org.estatio.dom.communicationchannelfixedasset;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;

import org.estatio.dom.communicationchannel.CommunicationChannelContributions;
import org.estatio.dom.communicationchannel.CommunicationChannelOwner;

@DomainService
@Hidden
public class CommunicationChannelContributionsOnFixedAsset extends CommunicationChannelContributions {

    public CommunicationChannelContributionsOnFixedAsset() {
        super(CommunicationChannelContributionsOnFixedAsset.class);
    }


    /**
     * Hidden if the {@link org.estatio.dom.communicationchannel.CommunicationChannelOwner} is a {@link org.estatio.dom.asset.FixedAsset}.
     *
     * <p>
     * Why? because we intend to remove the ability to associate {@link org.estatio.dom.communicationchannel.CommunicationChannel}s with
     * {@link org.estatio.dom.asset.FixedAsset}s.  See <a href="https://stromboli.atlassian.net/browse/EST-421">EST-421</a> for
     * further discussion.
     * </p>
     */
    public boolean hideCommunicationChannels(final CommunicationChannelOwner owner) {
        //return owner instanceof FixedAsset;
        return false;
    }

}
