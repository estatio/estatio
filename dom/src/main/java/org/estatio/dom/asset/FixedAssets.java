package org.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

@Named("Fixed Assets")
@Hidden
public class FixedAssets extends EstatioDomainService {

    public FixedAssets() {
        super(FixedAssets.class, FixedAsset.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<FixedAsset> search(final @Named("Search Phrase") String searchPhrase) {
        return allMatches(queryForSearch(searchPhrase));
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<FixedAsset> allFixedAssets() {
        return allInstances(FixedAsset.class);
    }

    // //////////////////////////////////////

    @Hidden
    public List<FixedAsset> autoComplete(String searchPhrase) {
        return search(StringUtils.wildcardToRegex(searchPhrase));
    }

    // //////////////////////////////////////

    private static QueryDefault<FixedAsset> queryForSearch(String regex) {
        return new QueryDefault<FixedAsset>(FixedAsset.class, "search", "regex", regex);
    }

}
