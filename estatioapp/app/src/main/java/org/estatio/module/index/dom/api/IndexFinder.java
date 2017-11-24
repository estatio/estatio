package org.estatio.module.index.dom.api;

import org.estatio.module.index.dom.Index;

public interface IndexFinder {

    Index findByReference(String reference);
}
