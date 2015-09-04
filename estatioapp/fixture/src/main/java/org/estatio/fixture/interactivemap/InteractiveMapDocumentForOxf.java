package org.estatio.fixture.interactivemap;

import javax.inject.Inject;
import org.estatio.dom.asset.FixedAssetRepository;
import org.estatio.fixture.asset._PropertyForOxfGb;

public class InteractiveMapDocumentForOxf extends InteractiveMapDocumentAbstract {

    public static final String NAME = _PropertyForOxfGb.REF + ".svg";

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.executeChild(this, new _PropertyForOxfGb());
        createDocument(NAME, fixedAssetRepository.matchAssetsByReferenceOrName(_PropertyForOxfGb.REF).get(0));
    }

    @Inject
    FixedAssetRepository fixedAssetRepository;

}
