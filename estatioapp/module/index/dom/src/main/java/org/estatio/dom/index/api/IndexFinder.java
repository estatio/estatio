package org.estatio.dom.index.api;

import org.estatio.dom.index.Index;

public interface IndexFinder {

    Index findByReference(String reference);
}
