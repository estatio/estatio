package org.estatio.fixture.interactivemap;

import javax.inject.Inject;

import org.estatio.dom.asset.FixedAssetRepository;
import org.estatio.fixture.asset.PropertyForOxfGb;

public class InteractiveMapDocumentForOxf extends InteractiveMapDocumentAbstract {

    public static final String NAME = PropertyForOxfGb.REF + ".svg";

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.executeChild(this, new PropertyForOxfGb());
        createDocument(NAME, fixedAssetRepository.matchAssetsByReferenceOrName(PropertyForOxfGb.REF).get(0));
    }

    @Inject
    FixedAssetRepository fixedAssetRepository;

}
