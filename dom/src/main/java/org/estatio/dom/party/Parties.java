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

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public class Parties extends EstatioDomainService<Party> {

    public Parties() {
        super(Parties.class, Party.class);
    }

    
    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "6")
    public List<Party> findParties(@Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") final String searchPattern) {
        return allMatches("findByReferenceOrName", "searchPattern", StringUtils.wildcardToCaseInsensitiveRegex(searchPattern));
    }

    @Hidden
    @ActionSemantics(Of.SAFE)
    public Party findPartyByReferenceOrName(final String searchPattern) {
        return firstMatch("findByReferenceOrName", "searchPattern", StringUtils.wildcardToRegex(searchPattern));
    }


    // //////////////////////////////////////

    @Hidden
    public List<Party> autoComplete(String searchPhrase) {
        return searchPhrase.length()>2 
                ? findParties("*"+searchPhrase+"*") 
                : null;
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<Party> allParties() {
        return allInstances();
    }

}
