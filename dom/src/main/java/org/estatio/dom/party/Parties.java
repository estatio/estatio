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
