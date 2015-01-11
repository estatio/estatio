package org.estatio.fixture.interactivemap;

import javax.inject.Inject;

import org.estatio.dom.asset.FixedAssets;
import org.estatio.fixture.asset.PropertyForOxf;

public class InteractiveMapDocumentForOxf extends InteractiveMapDocumentAbstract {

    public static final String NAME = PropertyForOxf.PROPERTY_REFERENCE + ".svg";

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.executeChild(this, new PropertyForOxf());

        createDocument(NAME, fixedAssets.matchAssetsByReferenceOrName(PropertyForOxf.PROPERTY_REFERENCE).get(0));
    }

    @Inject
    FixedAssets fixedAssets;

}
