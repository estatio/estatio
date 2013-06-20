package org.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

@Hidden
public class FixedAssets extends EstatioDomainService<FixedAsset> {

    public FixedAssets() {
        super(FixedAssets.class, FixedAsset.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    public List<FixedAsset> search(String searchPhrase) {
        return allMatches("search", "regex", searchPhrase);
    }

    // //////////////////////////////////////

    @Hidden
    public List<FixedAsset> autoComplete(String searchPhrase) {
        return search(StringUtils.wildcardToRegex(searchPhrase));
    }


}
